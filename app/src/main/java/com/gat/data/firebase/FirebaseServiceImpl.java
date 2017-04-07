package com.gat.data.firebase;

import android.util.Log;

import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
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

    private final int GROUP_SIZE = 20;
    private final int MESSAGE_SIZE = 100;

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

    // To emmit list of group
    private Subject<List<Group>> groupListSubject;
    // To store groups list
    private List<Group> groups;
    // To emmit list of message
    private Subject<List<Message>> messageListSubject;
    // To store message list
    private List<Message> messages;

    private Subject<String> groupIdSubject;

    private CompositeDisposable compositeDisposable;

    private static boolean isCalled = false;
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            login();
        }

        groupListSubject = BehaviorSubject.create();
        messageListSubject = BehaviorSubject.create();
        groupIdSubject = BehaviorSubject.create();

        groups = new ArrayList<>();

        groupListSubject.onNext(groups);

        messages = new ArrayList<>();

        messageListSubject.onNext(messages);

        compositeDisposable = new CompositeDisposable(
                getGroupList(/*userDataSourceLazy.get().loadUser().blockingFirst().id()*/"123").subscribe(this::hasNewGroup),
                getMessageList().subscribe(this::hasNewMessage),
                groupIdSubject.observeOn(schedulerFactory.io()).subscribe(this::getMessageInGroup)
        );

    }

    private void login() {
        // TODO login

        // Initialize database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void onDestroy() {
        compositeDisposable.dispose();
    }

    @Override
    public Observable<List<Message>> getMessageList(String groupId) {
        groupIdSubject.onNext(groupId);
        return messageListSubject.observeOn(schedulerFactory.io());
    }


    @Override
    public Observable<List<Group>> getGroupList() {
        return groupListSubject.observeOn(schedulerFactory.io());
    }

    @Override
    public Observable<List<Group>> getGroupList(int page, int size) {
        return null;
    }

    /**
     * Get group list from user id (with group id, user id that belong to that group)
     * @param userId
     */
    private Observable<String> getGroupList(String userId) {
        Log.d(TAG, "getGroupList");
        Observable<String> observable = Observable.create(emitter -> {

            databaseReference.child(USER_LEVEL).child(userId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    emitter.onNext(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });
        return observable.observeOn(schedulerFactory.io());
    }

    /**
     * listen to new message from firebase to update last message
     */
    private Observable<Message> getMessageList() {
        Observable<Message> messageObservable = Observable.create(emitter -> {
            databaseReference.child(MESSAGE_LEVEL).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String groupId = dataSnapshot.getKey();
                    Log.d(TAG, "hasAddedMessage" + groupId);
                    if (inGroup(groupId)) {
                        DataSnapshot lastMessage = dataSnapshot.getChildren().iterator().next();
                        Message message = lastMessage.getValue(Message.class);
                        emitter.onNext(message);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });
        return messageObservable.observeOn(schedulerFactory.io());
    }

    /**
     * get new message for specified group (for chat activity)
     * @param groupId
     */
    private void getMessageInGroup(String groupId) {
        messages.clear();
        Observable<Message> messageObservable = Observable.create(emitter -> {
            databaseReference.child(MESSAGE_LEVEL).child(groupId).orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "hasAddedMessage" + groupId);
                    Message message = dataSnapshot.getValue(Message.class);
                    emitter.onNext(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });
        messageObservable.observeOn(schedulerFactory.io())
                .subscribe(mes -> {
                    addMessage(mes);
                    hasNewMessage(mes);
                });
    }

    private List<Group> makeCopyGroupList(List<Group> srcList) {
        List<Group> tarList = new ArrayList<>();
        for (Iterator<Group> iterator = srcList.iterator(); iterator.hasNext();)
            tarList.add(iterator.next());
        return tarList;
    }


    /**
     * get information for new group and add to list
     * @param groupId
     */
    private void hasNewGroup(String groupId) {
        Log.d(TAG, "hasNewGroup" + groupId);
        Observable<Group> groupObservable = Observable.create(emiter -> {
            databaseReference.child(GROUP_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userList = new ArrayList<String>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (snapshot != null)
                            userList.add(snapshot.getKey());
                    }
                    Group group = Group.builder().groupId(groupId).users(userList).build();
                    emiter.onNext(group);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }).flatMap(group -> Observable.create(emitter -> {
            databaseReference.child(MESSAGE_LEVEL).child(groupId).orderByChild("timeStamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Message message = Message.NONE;
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        message = firstChild.getValue(Message.class);

                    }
                    emitter.onNext(Group.builder()
                            .groupId(((Group) group).groupId())
                            .users(((Group) group).users())
                            .lastMessage(message.getMessage())
                            .timeStamp(message.getTimeStamp()).build());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }));

        groupObservable.subscribe(group -> {
            addGroup(group);
        });
    }

    /**
     * add last message to group
     * @param message
     */
    private void hasNewMessage(Message message) {
        Group updated = null;
        synchronized (groups) {
            Log.d(TAG, "hasNewMessage:" + message.getMessage());
            for (Iterator<Group> iterator = groups.iterator();  iterator.hasNext(); ) {
                Group group = iterator.next();
                if (group.groupId().equals(message.getGroupId())) {
                    updated = Group.instance(group);
                    iterator.remove();
                }
            }
        }
        if (updated != null)
            addGroup(updated);
    }

    private boolean inGroup(String id) {
        boolean ret = false;
        synchronized (groups) {
            for (Group group : groups) {
                Log.d(TAG, "Group:" + group.groupId() + "," + id);
                if (id.equals(group.groupId())) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    private void addGroup(Group group) {
        Log.d(TAG, "addGroup");
        synchronized (groups) {
            int count = 0;
            for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext(); ) {
                Group search = iterator.next();
                if (search.groupId().equals(group.groupId())) {
                    iterator.remove();
                    break;
                } else if (search.timeStamp() > group.timeStamp()) {
                    count++;
                }
            }
            Log.d(TAG, "onNextGroup" + ((Group) group).groupId());
            groups.add(count, (Group) group);
            groupListSubject.onNext(makeCopyGroupList(groups));
        }
    }

    private void addMessage(Message message) {
        synchronized (messages) {
            for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext(); ) {
                if (iterator.next().equals(message)) {
                    return;
                }
            }
            messages.add(message);
            messageListSubject.onNext(messages);
        }
    }
}
