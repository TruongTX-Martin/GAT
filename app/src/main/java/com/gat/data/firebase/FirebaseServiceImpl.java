package com.gat.data.firebase;

import android.util.Log;

import com.gat.data.id.LongId;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;
import com.gat.repository.entity.User;
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

    private int groupPageCnt = 1;
    private int mesPageCnt = 1;
    private String groupId;

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
    private Subject<List<Group>> loadMoreGroupListSubject;
    // To store groups list
    private List<Group> groups;
    // To emmit list of message
    private Subject<List<Message>> messageListSubject;
    private Subject<List<Message>> loadMoreMessageListSubject;
    // To store message list
    private List<Message> messages;

    private Subject<String> groupIdSubject;

    private Subject<Message> messageSubject;

    private Subject<Message> groupMessageSubject;

    private Subject<String> newGroupSubject;

    private CompositeDisposable compositeDisposable;

    private Subject<Integer> loadMoreGroupSubject;

    private Subject<Integer> loadMoreMessageSubject;

    private ChildEventListener messageChildEventListener;
    private ChildEventListener groupChildEventListener;


    private static boolean isCalled = false;
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            login();
        }

        groupListSubject = BehaviorSubject.create();
        loadMoreGroupListSubject = BehaviorSubject.create();
        messageListSubject = BehaviorSubject.create();
        loadMoreMessageListSubject = BehaviorSubject.create();
        groupIdSubject = BehaviorSubject.create();
        messageSubject = BehaviorSubject.create();
        groupMessageSubject = BehaviorSubject.create();
        newGroupSubject = BehaviorSubject.create();
        loadMoreGroupSubject = BehaviorSubject.create();
        loadMoreMessageSubject = BehaviorSubject.create();

        groups = new ArrayList<>();

        groupListSubject.onNext(groups);

        messages = new ArrayList<>();

        messageListSubject.onNext(messages);

        compositeDisposable = new CompositeDisposable(
                newGroupSubject.observeOn(schedulerFactory.io()).subscribe(this::hasNewGroup),
                groupMessageSubject.observeOn(schedulerFactory.io()).subscribe(this::hasGroupMessage),
                groupIdSubject.observeOn(schedulerFactory.io()).subscribe(this::getMessageInGroup),
                messageSubject.observeOn(schedulerFactory.io()).subscribe(this::addMessage),
                loadMoreGroupSubject.observeOn(schedulerFactory.io()).subscribe(this::loadGroup),
                loadMoreMessageSubject.observeOn(schedulerFactory.io()).subscribe(this::loadMessage)
        );

        messageChildEventListener = getMessageList();
        groupChildEventListener = getGroupList("123");
    }

    private void login() {
        SignInFirebase signInFirebase = new SignInFirebaseImpl(userDataSourceLazy.get().loadLoginData().blockingFirst(), schedulerFactory);

        signInFirebase.login();

        // Initialize database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void onDestroy() {
        compositeDisposable.dispose();
        databaseReference.child(MESSAGE_LEVEL).removeEventListener(messageChildEventListener);
        databaseReference.child(GROUP_LEVEL).child("123").removeEventListener(groupChildEventListener); // TODO
    }

    @Override
    public Observable<List<Message>> getMessageList(String userId) {
        String groupId = userId + userDataSourceLazy.get().loadUser().blockingFirst().id();    // TODO
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

    @Override
    public Observable<List<Group>> loadMoreGroup() {
        Log.d(TAG, "LoadMoreGroup");
        loadMoreGroupSubject.onNext(groupPageCnt++);
        return loadMoreGroupListSubject;
    }

    @Override
    public Observable<List<Message>> loadMoreMessage() {
        Log.d(TAG, "LoadMoreMessage");
        loadMoreMessageSubject.onNext(mesPageCnt++);
        return loadMoreMessageListSubject;
    }

    @Override
    public Observable<Boolean> sendMessage(String userId, String message) {
        Log.d(TAG, "sendMessage");
        User user = userDataSourceLazy.get().loadUser().blockingFirst();
        String groupId = userId + user.id(); // TODO
        Message mes = new Message(123l, user.name(), message, user.avatar(), new Date().getTime());
        databaseReference.child(GROUP_LEVEL).child(groupId).setValue(mes);
        return Observable.just(true);
    }

    /**
     * Get group list from user id (with group id, user id that belong to that group)
     * @param userId
     */
    private ChildEventListener getGroupList(String userId) {
        Log.d(TAG, "getGroupList");
        return databaseReference.child(USER_LEVEL).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                newGroupSubject.onNext(dataSnapshot.getKey());
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
    }

    /**
     * listen to new message from firebase to update last message
     */
    private ChildEventListener getMessageList() {
        return databaseReference.child(MESSAGE_LEVEL).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String groupId = dataSnapshot.getKey();
                Log.d(TAG, "hasAddedMessage" + groupId);
                if (inGroup(groupId)) {
                    DataSnapshot lastMessage = dataSnapshot.getChildren().iterator().next();
                    Message message = lastMessage.getValue(Message.class);
                    messageSubject.onNext(message);
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
    }

    /**
     * get new message for specified group (for chat activity)
     * @param groupId
     */
    private void getMessageInGroup(String groupId) {
        this.groupId = groupId;
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
                    messageSubject.onNext(mes);
                    hasGroupMessage(mes);
                });
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
    private void hasGroupMessage(Message message) {
        Group updated = null;
        synchronized (groups) {
            Log.d(TAG, "hasGroupMessage:" + message.getMessage());
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
            if (count < GROUP_SIZE) {
                groupListSubject.onNext(makeEmitGroupList(groups, 1));
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
                } else if (mes.getTimeStamp() > message.getTimeStamp()) {
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

    void loadGroup(int page) {
        Log.d(TAG, "loadMoreGroup");
        loadMoreGroupListSubject.onNext(makeEmitGroupList(groups, page));
    }

    void loadMessage(int page) {
        Log.d(TAG, "loadMoreMessage");
        loadMoreMessageListSubject.onNext(makeEmitMessageList(messages, page));
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
