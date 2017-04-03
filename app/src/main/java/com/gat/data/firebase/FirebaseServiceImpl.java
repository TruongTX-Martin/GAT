package com.gat.data.firebase;

import android.util.Log;

import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class FirebaseServiceImpl implements FirebaseService{
    private static String TAG = FirebaseServiceImpl.class.getSimpleName();

    private final String USER_LEVEL = "users";
    private final String GROUP_LEVEL = "groups";
    private final String MESSAGE_LEVEL = "messages";

    private final Lazy<UserDataSource> userDataSourceLazy;
    private final SchedulerFactory schedulerFactory;

    public FirebaseServiceImpl(Lazy<UserDataSource> userDataSourceLazy, SchedulerFactory schedulerFactory) {
        this.userDataSourceLazy = userDataSourceLazy;
        this.schedulerFactory = schedulerFactory;
        init();
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Subject<List<Group>> groupListSubject;
    private Subject<List<Group>> groupListSubject2;

    private Subject<List<String>> groupListIdSubject;

    private List<Group> groups;

    Subject<List<Message>> messageList;

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        groupListSubject = BehaviorSubject.create();
        groupListSubject2 = BehaviorSubject.create();
        groupListIdSubject = BehaviorSubject.create();

        messageList = BehaviorSubject.create();

        groups = new ArrayList();

        if (firebaseUser == null) {
            login();
        }
    }

    @Override
    public void login() {
        // TODO login

        // Initialize database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getGroupList(String userId) {
        // TODO
        databaseReference.child(USER_LEVEL).child("123").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "OnDataChanged");
                List<String> stringList = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Add groupId as key, true as value for more efficient searching
                    stringList.add(snapshot.getKey());
                    Log.d(TAG, "GroupID=" + snapshot.getKey());
                }
                groupListIdSubject.onNext(stringList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "AddValueEventListenerError:" + databaseError.getDetails());
            }
        });
    }

    private void getMessageFromGroup(String groupId) {
        Log.d(TAG, "GetMessageFrom:"+groupId);
        databaseReference.child(MESSAGE_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> list = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    message.userId = snapshot.getKey();
                    list.add(message);
                    Log.d(TAG, "Message:"+message.message);
                }
                messageList.onNext(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserInGroup(String groupId) {
        databaseReference.child(GROUP_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userList = new ArrayList<String>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot != null)
                        userList.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get user Id of input groups
     * @param groupIds
     */
    private void getUsersInGroups(List<String> groupIds) {
        groups.clear();
        Log.d(TAG, "getUsersInGroups");
        for (String groupId : groupIds) {
            Log.d(TAG, "GetUserFrom:"+ groupId);
            databaseReference.child(GROUP_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userId = new ArrayList<String>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        userId.add(snapshot.getKey());
                        Log.d(TAG, "UserID"+snapshot.getKey());
                    }
                    if (userId.size() > 0)
                        groups.add(Group.builder().groupId(groupId).users(userId).build());
                    Log.d(TAG, "GroupSize:" + Integer.toString(groups.size()));
                    if (groups.size() >= groupIds.size())
                        groupListSubject.onNext(makeCopyGroupList(groups));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.toString());
                }
            });
        }
    }

    private void getLastMessageInGroups(List<Group> listGroup) {
        groups.clear();
        Log.d(TAG, "getLastMessage:GroupSize " + listGroup.size());
        for (Group group : listGroup) {
            Log.d(TAG, "GetMessageFrom:"+group.groupId());
            databaseReference.child(MESSAGE_LEVEL).child(group.groupId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);
                        Log.d(TAG, "Message:"+message.message);
                        groups.add(Group.builder().groupId(group.groupId())
                                .users(group.users())
                                .timeStamp(message.timeStamp)
                                .lastMessage(message.message)
                                .build());
                        if (groups.size() >= listGroup.size())
                            groupListSubject2.onNext(makeCopyGroupList(groups));
                        break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public Observable<List<Message>> getMessageList(String groupId) {
        getMessageFromGroup(groupId);
        return messageList.observeOn(schedulerFactory.io());
    }


    @Override
    public Observable<List<String>> getGroupList() {
        getGroupList(userDataSourceLazy.get().loadUser().blockingFirst().id().toString());
        return groupListIdSubject.observeOn(schedulerFactory.io());
    }

    @Override
    public Observable<List<Group>> getGroupUserList(List<String> groupIds) {
        getUsersInGroups(groupIds);
        return groupListSubject.observeOn(schedulerFactory.io());
    }

    @Override
    public Observable<List<Group>> getGroupLastMessage(List<Group> listGroup) {
        getLastMessageInGroups(listGroup);
        return groupListSubject2.observeOn(schedulerFactory.io());
    }

    @Override
    public Observable<List<Group>> getGroupList(int page, int size) {
        return null;
    }

    private List<Group> makeCopyGroupList(List<Group> srcList) {
        List<Group> tarList = new ArrayList<>();
        for (Group group : srcList)
            tarList.add(Group.instance(group));
        return tarList;
    }

}
