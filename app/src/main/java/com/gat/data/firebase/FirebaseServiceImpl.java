package com.gat.data.firebase;

import android.util.Log;
import android.util.Pair;

import com.gat.common.util.CommonUtil;
import com.gat.common.util.Strings;
import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
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


    private final int GROUP_SIZE = 15;
    private final int MESSAGE_SIZE = 15;
    private int mUserId = 0;
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
    private Subject<List<GroupTable>> groupListSubject;
    private Subject<GroupTable> updatedGroupSubject;
    // To store groups list
    private List<GroupTable> groups;
    // To start getting
    private Subject<Integer> groupStartSubject;

    private Subject<Boolean> groupInitializedSubject;

    /** For group list **/

    /** For message list **/
    private Subject<String> groupIdSubject;
    // To emmit list of message
    private Subject<List<MessageTable>> messageListSubject;
    private Subject<MessageTable> hasNewMessageSubject;
    // To store message list
    private List<MessageTable> messages;

    private Subject<Boolean> messageLoadFinishSubject;

    /** send message **/
    private Subject<Pair<Integer, MessageTable>> sendMessageSubject;
    private Subject<Boolean> sendMessageResult;

    private CompositeDisposable compositeDisposable;

    private ChildEventListener messageChildEventListener = null;
    private ChildEventListener groupChildEventListener = null;

    private boolean mGroupInitialized = false;

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        /** Group **/
        userIdSubject = BehaviorSubject.create();
        groupListSubject = BehaviorSubject.create();
        updatedGroupSubject = BehaviorSubject.create();
        groupStartSubject = BehaviorSubject.create();
        groupInitializedSubject = BehaviorSubject.createDefault(false);
        groups = new ArrayList<>();

        /** Message **/
        groupIdSubject = BehaviorSubject.create();
        messageListSubject = BehaviorSubject.create();
        hasNewMessageSubject = BehaviorSubject.create();
        messageLoadFinishSubject = BehaviorSubject.createDefault(false);
        messages = new ArrayList<>();

        /** Send message **/
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
                    addMessage(messageTable);
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
                messageLoadFinishSubject.observeOn(schedulerFactory.main()).subscribe(finish -> {
                    if (finish) {
                        messageListSubject.onNext(messages);
                        messageListSubject.onComplete();
                    }
                }),
                sendMessageSubject.observeOn(schedulerFactory.main()).subscribe(this::sendMes),
                groupStartSubject.observeOn(schedulerFactory.main()).subscribe(page -> {
                    mGroupPage = page;
                    if (mGroupInitialized) {
                        Log.d(TAG, "Is Already Initialized");
                        // TODO emit all items in list
                        // groupListSubject.onNext(makeEmitGroupList(groups, mGroupPage));
                        groupListSubject.onNext(groups);
                        mGroupPage = 0;
                        groupListSubject.onComplete();
                    }
                }),
                groupInitializedSubject.observeOn(schedulerFactory.main()).subscribe(isInit -> {
                    Log.d(TAG, "Initialized:" + mGroupPage + "," + mGroupInitialized + "," +isInit);
                    if (mGroupInitialized || !isInit)
                        return;
                    mGroupInitialized = isInit;

                    if (mGroupInitialized && mGroupPage > 0) {
                        // TODO emit all items in list
                        // groupListSubject.onNext(makeEmitGroupList(groups, mGroupPage));
                        groupListSubject.onNext(groups);
                        mGroupPage = 0;
                        groupListSubject.onComplete();
                    }

                    if (!mGroupInitialized) {
                        databaseReference.child(GROUP_LEVEL).child(Integer.toString(mUserId)).addChildEventListener(groupChildEventListener);
                    }
                })
        );

        // Start listen to groups list
        userDataSourceLazy.get().loadUser().subscribe(user -> {
            userIdSubject.onNext((long)user.userId());
            mUserId = user.userId();
        });
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
    public Observable<List<GroupTable>> getGroupList(int page, int size) {
        groupStartSubject.onNext(page);
        return groupListSubject.observeOn(schedulerFactory.io());
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<GroupTable> groupUpdated() {
        return updatedGroupSubject.observeOn(schedulerFactory.io());
    }

    /**
     *
     * @param userId
     */
    private void getGroupOfUser(long userId) {
        Log.d(TAG, "getGroupOfUser" + userId);
        databaseReference.child(USER_LEVEL).child(Long.toString(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGroupCnt = dataSnapshot.getChildrenCount();
                Log.d(TAG, "GroupCnt="+mGroupCnt);
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext(); ) {
                    makeGroupList(iterator.next().getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "USER_"+userId+":"+databaseError.getMessage());
            }
        });
    }

    private void makeGroupList(String groupId) {
        Log.d(TAG, "makeGroupList:" + groupId);
        databaseReference.child(GROUP_LEVEL).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<String>();
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext();) {
                    String key = iterator.next().getKey();
                    if (key.equals(Integer.toString(mUserId))) {
                        users.add(key);
                    } else {
                        users.add(0, key);
                    }
                }
                GroupTable group = GroupTable.builder().groupId(groupId)
                        .users(users)
                        .build();
                Log.d(TAG, group.users().toString());
                makeGroupList(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void makeGroupList(GroupTable group) {
        databaseReference.child(MESSAGE_LEVEL).child(group.groupId()).orderByChild("timeStamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MessageTable messageTable = dataSnapshot.getChildren().iterator().next().getValue(MessageTable.class);
                    GroupTable gr = GroupTable.builder().groupId(group.groupId())
                            .users(group.users())
                            .lastMessage(messageTable.getMessage() == null ? "" : messageTable.getMessage())
                            .timeStamp(messageTable.getTimeStamp())
                            .isRead(messageTable.isRead())
                            .build();
                    addGroup(gr);
                } else {
                    mGroupCnt--;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     *
     * @param userId
     * @param page
     * @param size : no use
     */
    @Override
    public void getMessageList(int userId, int page, int size) {
        mMessagePage = page;
        int fromUserId = userDataSourceLazy.get().loadUser().blockingFirst().userId();
        groupIdSubject.onNext(CommonUtil.getGroupId(userId, fromUserId));
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<List<MessageTable>> messageList() {
        return messageListSubject.observeOn(schedulerFactory.io());
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<MessageTable> hasNewMessage() {
        return hasNewMessageSubject.observeOn(schedulerFactory.io());
    }

    private void getMessageInGroup(String groupId) {
        messages.clear();
        databaseReference.child(MESSAGE_LEVEL).child(groupId).orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessageCnt = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child(MESSAGE_LEVEL).child(groupId).orderByChild("timeStamp").addChildEventListener(messageChildEventListener);
    }

    @Override
    public void sendMessage(/*long fromUserId, */long toUserId, String message) {
        Log.d(TAG, "sendMessage");
        int fromUserId = userDataSourceLazy.get().loadUser().blockingFirst().userId();
        MessageTable mes = new MessageTable(fromUserId, message, new Date().getTime(), false);
        sendMessageSubject.onNext(new Pair<>(fromUserId, mes));
    }

    /**
     *
     * @return
     */
    @Override
    public Observable<Boolean> sendResult() {
        return sendMessageResult.observeOn(schedulerFactory.main());
    }

    private void sendMes(Pair<Integer, MessageTable> mes) {
        int fromUserId = mes.first;
        int toUserId = (int)mes.second.getUserId();
        String groupId = (mes.first < mes.second.getUserId()) ? (fromUserId + "" + toUserId) : (toUserId + "" + fromUserId);

        if (databaseReference.child(GROUP_LEVEL).child(groupId) != null) {

        } else {
            // Add groups
            databaseReference.child(GROUP_LEVEL).push().setValue(groupId);
            databaseReference.child(GROUP_LEVEL).child(groupId).child(Integer.toString(fromUserId)).setValue(true);
            databaseReference.child(GROUP_LEVEL).child(groupId).child(Integer.toString(toUserId)).setValue(true);

            databaseReference.child(USER_LEVEL).child(Integer.toString(fromUserId)).child(groupId).setValue(true);
            databaseReference.child(USER_LEVEL).child(Integer.toString(toUserId)).child(groupId).setValue(true);
        }
        databaseReference.child(MESSAGE_LEVEL).child(groupId).push().setValue(mes.second)
                .addOnCompleteListener(act -> sendMessageResult.onNext(true))
                .addOnFailureListener(act -> sendMessageResult.onNext(false));
    }

    /**
     * add last message to group
     * @param messageTable
     */
    private void hasGroupMessage(MessageTable messageTable) {
        GroupTable updated = null;
        synchronized (groups) {
            Log.d(TAG, "hasGroupMessage:" + messageTable.getMessage());
            for (Iterator<GroupTable> iterator = groups.iterator();  iterator.hasNext(); ) {
                GroupTable group = iterator.next();
                if (group.groupId().equals(messageTable.getGroupId())) {
                    updated = GroupTable.instance(group);
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
            for (GroupTable group : groups) {
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
    private void addGroup(GroupTable group) {
        Log.d(TAG, "AddGroup:" + group.groupId());
        synchronized (groups) {
            int count = 0;
            for (Iterator<GroupTable> iterator = groups.iterator(); iterator.hasNext(); ) {
                GroupTable search = iterator.next();
                if (search.groupId().equals(group.groupId())) {
                    iterator.remove();
                    break;
                } else if (search.timeStamp() > group.timeStamp()) {
                    count++;
                }
            }

            groups.add(count, group);
            // Remove most in-active in the list
            Log.d(TAG, "GroupSize="+groups.size());

            if (groups.size() > GROUP_MAX_SIZE) {
                Log.i(TAG, "Over group size");
                groups.remove(groups.size()-1);
            }

            if (mGroupInitialized) {
                updatedGroupSubject.onNext(group);
            } else {
                if (groups.size() >= mGroupCnt) {
                    Log.d(TAG, "Group loading completed.");
                    groupInitializedSubject.onNext(true);
                }
            }
        }
    }

    /**
     * add message to message list then emit messages
     * @param message
     */
    private void addMessage(MessageTable message) {
        synchronized (messages) {
            messages.add(message);
            if (messages.size() > MESSAGE_MAX_SIZE) {
                Log.i(TAG, "Over message size");
                messages.remove(messages.size() - 1);
            }
            if (messages.size() >= MESSAGE_SIZE) {
                messageListSubject.onNext(messages);
            }
            if (messages.size() >= mMessageCnt) {
                messageListSubject.onNext(messages);
            }
        }
    }

    private List<GroupTable> makeEmitGroupList(List<GroupTable> srcList, int page) {
        Log.d(TAG, "EmitGroup: page=" + page);
        List<GroupTable> tarList = new ArrayList<>();
        int start = (page-1) * GROUP_SIZE;
        int end = page * GROUP_SIZE;
        int count = 0;
        for (Iterator<GroupTable> iterator = srcList.iterator(); iterator.hasNext();) {
            GroupTable group = iterator.next();
            if (count >= start) tarList.add(group);
            count++;
            if (count >= end) break;
        }
        Log.d(TAG, "EmitGroup:PageSize=" + tarList.size());
        return tarList;
    }

    private List<MessageTable> makeEmitMessageList(List<MessageTable> srcList, int page) {
        Log.d(TAG, "EmitMessage: page " + page);
        List<MessageTable> tarList = new ArrayList<>();
        int start = (page-1) * MESSAGE_SIZE;
        int end = page * MESSAGE_SIZE;
        int count = 0;
        for (Iterator<MessageTable> iterator = srcList.iterator(); iterator.hasNext();) {
            MessageTable messageTable = iterator.next();
            if (count >= start) tarList.add(messageTable);
            count++;
            if (count >= end) break;
        }
        Log.d(TAG, "PageSize:" + tarList.size());
        return tarList;
    }
}
