package com.gat.data.firebase;

import android.util.Log;
import android.util.Pair;

import com.gat.common.util.Strings;
import com.gat.data.firebase.entity.MessageTable;
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
import java.util.Date;
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
    private static final int GROUP_MAX_SIZE = 1000;
    private static final int MESSAGE_MAX_SIZE = 1000;

    private final String USER_LEVEL = "users";
    private final String GROUP_LEVEL = "groups";
    private final String MESSAGE_LEVEL = "messages";


    private final int GROUP_SIZE = 10;
    private final int MESSAGE_SIZE = 10;
    private long mUserId = 0;
    private String mGroupId = "";

    private int mGroupPage = 0;
    private int mMessagePage = 0;

    private long mGroupCnt = 0;
    private long mMessageCnt = 0;

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

    /** For group list **/
    private Subject<Long> userIdSubject;      // input to get group list
    // To emmit list of group
    private Subject<List<Group>> groupListSubject;
    private Subject<Group> updatedGroupSubject;
    // To store groups list
    private List<Group> groups;

    /** For message list **/
    private Subject<String> groupIdSubject;
    // To emmit list of message
    private Subject<List<Message>> messageListSubject;
    private Subject<Message> hasNewMessageSubject;
    // To store message list
    private List<Message> messages;

    /** send message **/
    private Subject<Pair<String, MessageTable>> sendMessageSubject;
    private Subject<Boolean> sendMessageResult;

    private CompositeDisposable compositeDisposable;

    private ChildEventListener messageChildEventListener = null;
    private ChildEventListener groupChildEventListener = null;

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            // TODO
        }

        /** Group **/
        userIdSubject = BehaviorSubject.create();
        groupListSubject = BehaviorSubject.create();
        updatedGroupSubject = BehaviorSubject.create();
        groups = new ArrayList<>();

        /** Message **/
        groupIdSubject = BehaviorSubject.create();
        messageListSubject = BehaviorSubject.create();
        hasNewMessageSubject = BehaviorSubject.create();
        messages = new ArrayList<>();

        /** send message **/
        sendMessageSubject = BehaviorSubject.create();
        sendMessageResult = BehaviorSubject.create();

        groupChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "GroupChild:Added");
                if (dataSnapshot.exists())
                    makeGroupList(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "GroupChild:Changed");
                if (dataSnapshot.exists())
                    makeGroupList(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "GroupChild:Removed");
                //if (dataSnapshot.exists())
                //    makeGroupList(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "GroupChild:Moved");
                if (dataSnapshot.exists())
                    makeGroupList(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "GroupChild:"+databaseError.getMessage());
            }
        };

        messageChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    MessageTable messageTable = dataSnapshot.getValue(MessageTable.class);
                    Message message = Message.builder()
                            .userId(messageTable.getUserId())
                            .message(messageTable.getMessage())
                            .timeStamp(messageTable.getTimeStamp())
                            .isRead(messageTable.isRead())
                            .build();
                    addMessage(message);
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
                Log.d(TAG, "MessageChild:"+databaseError.getMessage());
            }
        };

        compositeDisposable = new CompositeDisposable(
                userIdSubject.observeOn(schedulerFactory.main()).subscribe(this::getGroupOfUser),
                groupIdSubject.observeOn(schedulerFactory.main()).subscribe(this::getMessageInGroup),
                sendMessageSubject.observeOn(schedulerFactory.main()).subscribe(this::sendMes)
        );

    }

    public void onDestroy() {
        compositeDisposable.dispose();
        if (!Strings.isNullOrEmpty(mGroupId))
            databaseReference.child(MESSAGE_LEVEL).child(mGroupId).removeEventListener(messageChildEventListener);
        if (mUserId != 0)
            databaseReference.child(GROUP_LEVEL).child(Long.toString(mUserId)).removeEventListener(groupChildEventListener);
    }

    /**
     *
     * @param page
     * @param size : no use
     */
    @Override
    public void getGroupList(int page, int size) {
        mGroupPage = page;
        userDataSourceLazy.get().loadUser().subscribe(user -> {
            userIdSubject.onNext((long)user.userId());
        });
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<List<Group>> groupList() {
        return groupListSubject.observeOn(schedulerFactory.main());
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<Group> groupUpdated() {
        return updatedGroupSubject.observeOn(schedulerFactory.main());
    }

    /**
     *
     * @param userId
     */
    private void getGroupOfUser(long userId) {
        databaseReference.child(USER_LEVEL).child(Long.toString(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGroupCnt = dataSnapshot.getChildrenCount();
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext();) {
                    String groupId = iterator.next().getKey();
                    makeGroupList(groupId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "USER_"+userId+":"+databaseError.getMessage());
            }
        });
    }

    private void makeGroupList(String groupId) {
        databaseReference.child(USER_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<String>();
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext();) {
                    users.add(iterator.next().getKey());
                }
                Group group = Group.builder().groupId(groupId)
                        .users(users)
                        .build();
                makeGroupList(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void makeGroupList(Group group) {
        databaseReference.child(MESSAGE_LEVEL).child(group.groupId()).orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MessageTable messageTable = dataSnapshot.getValue(MessageTable.class);
                    Group gr = Group.builder().groupId(group.groupId())
                            .users(group.users())
                            .lastMessage(messageTable.getMessage())
                            .timeStamp(messageTable.getTimeStamp())
                            .isRead(messageTable.isRead())
                            .build();
                    addGroup(gr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addGroupChildListener(long userId) {
        if (userId == 0) {
            this.mUserId = userId;
            databaseReference.child(USER_LEVEL).child(Long.toString(userId)).addChildEventListener(groupChildEventListener);
        } else if (userId != this.mUserId) {
            databaseReference.child(USER_LEVEL).child(Long.toString(this.mUserId)).removeEventListener(groupChildEventListener);
            databaseReference.child(USER_LEVEL).child(Long.toString(userId)).addChildEventListener(groupChildEventListener);
            this.mUserId = userId;
        }
    }

    /**
     *
     * @param groupId
     * @param page
     * @param size : no use
     */
    @Override
    public void getMessageList(String groupId, int page, int size) {
        mMessagePage = page;
        groupIdSubject.onNext(groupId);
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<List<Message>> messageList() {
        return messageListSubject.observeOn(schedulerFactory.main());
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<Message> hasNewMessage() {
        return hasNewMessageSubject.observeOn(schedulerFactory.main());
    }

    private void getMessageInGroup(String groupId) {
        databaseReference.child(MESSAGE_LEVEL).child(groupId).orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext();) {
                    MessageTable messageTable = iterator.next().getValue(MessageTable.class);
                    Message message = Message.builder()
                            .message(messageTable.getMessage())
                            .userId(messageTable.getUserId())
                            .timeStamp(messageTable.getTimeStamp())
                            .isRead(messageTable.isRead())
                            .build();
                    addMessage(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendMessage(long fromUserId, long toUserId, String message) {
        Log.d(TAG, "sendMessage");
        String groupId = (fromUserId < toUserId) ? (fromUserId + "" + toUserId) : (toUserId + "" + fromUserId);
        MessageTable mes = new MessageTable(fromUserId, message, new Date().getTime(), false);
        sendMessageSubject.onNext(new Pair<>(groupId, mes));
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<Boolean> sendResult() {
        return sendMessageResult.observeOn(schedulerFactory.main());
    }

    private void sendMes(Pair<String, MessageTable> mes) {
        databaseReference.child(GROUP_LEVEL).child(mes.first).setValue(mes.second)
                .addOnCompleteListener(act -> sendMessageResult.onNext(true))
                .addOnFailureListener(act -> sendMessageResult.onNext(false));
    }

    /**
     * add last message to group
     * @param message
     */
    private void hasGroupMessage(Message message) {
        Group updated = null;
        synchronized (groups) {
            Log.d(TAG, "hasGroupMessage:" + message.message());
            for (Iterator<Group> iterator = groups.iterator();  iterator.hasNext(); ) {
                Group group = iterator.next();
                if (group.groupId().equals(message.groupId())) {
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

    /**
     * Add group to the list and emit group list
     * @param group
     */
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

            groups.add(count, group);
            // Remove most in-active in the list
            if (groups.size() > GROUP_MAX_SIZE) {
                Log.i(TAG, "Over group size");
                groups.remove(groups.size()-1);
            }
            if (groups.size() >= mGroupCnt) {
                // TODO put only 10 items
                // TODO start listen update groups
                groupListSubject.onNext(groups);
            }
        }
    }

    /**
     * add message to message list then emit messages
     * @param message
     */
    private void addMessage(Message message) {
        synchronized (messages) {
            int count = 0;
            for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext(); ) {
                Message mes = iterator.next();
                if (mes.equals(message)) {
                    return;
                } else if (mes.timeStamp() > message.timeStamp()) {
                    count++;
                }
            }
            messages.add(count, message);
            if (messages.size() > MESSAGE_MAX_SIZE) {
                Log.i(TAG, "Over message size");
                messages.remove(messages.size() - 1);
            }
            if (count < MESSAGE_SIZE) {
                messageListSubject.onNext(makeEmitMessageList(messages, 1));
            }
        }
    }

    private List<Group> makeEmitGroupList(List<Group> srcList, int page) {
        Log.d(TAG, "EmitGroup: page " + page);
        List<Group> tarList = new ArrayList<>();
        int start = (page-1) * GROUP_SIZE;
        int end = page * GROUP_SIZE;
        int count = 0;
        for (Iterator<Group> iterator = srcList.iterator(); iterator.hasNext();) {
            Group group = iterator.next();
            if (count >= start) tarList.add(group);
            count++;
            if (count >= end) break;
        }
        Log.d(TAG, "PageSize:" + tarList.size());
        return tarList;
    }

    private List<Message> makeEmitMessageList(List<Message> srcList, int page) {
        Log.d(TAG, "EmitMessage: page " + page);
        List<Message> tarList = new ArrayList<>();
        int start = (page-1) * MESSAGE_SIZE;
        int end = page * MESSAGE_SIZE;
        int count = 0;
        for (Iterator<Message> iterator = srcList.iterator(); iterator.hasNext();) {
            Message message = iterator.next();
            if (count >= start) tarList.add(message);
            count++;
            if (count >= end) break;
        }
        Log.d(TAG, "PageSize:" + tarList.size());
        return tarList;
    }

}
