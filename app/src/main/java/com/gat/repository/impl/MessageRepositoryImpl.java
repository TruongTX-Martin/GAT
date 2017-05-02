package com.gat.repository.impl;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.repository.MessageRepository;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;
import com.gat.repository.entity.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageRepositoryImpl implements MessageRepository {
    private final String TAG = MessageRepositoryImpl.class.getSimpleName();

    private final Lazy<MessageDataSource> networkDataSource;
    private final Lazy<MessageDataSource> localDataSource;
    private final Lazy<UserDataSource> netWorkUserDataSourceLazy;
    private final Lazy<UserDataSource> localUserDataSourceLazy;

    private final Subject<List<Group>> groupListSubject;
    private final List<Group> groupList;

    private final List<Message> messageList;

    private Subject<List<Message>> messageListSubject;

    private Subject<Integer> groupUnReadCnt;

    private int mUserId = 0;

    public MessageRepositoryImpl(Lazy<MessageDataSource> networkDataSource,
                                 Lazy<MessageDataSource> localDataSource,
                                 Lazy<UserDataSource> netWorkUserDataSourceLazy,
                                 Lazy<UserDataSource> localUserDataSourceLazy
                                 ) {
        this.networkDataSource = networkDataSource;
        this.localDataSource = localDataSource;
        this.netWorkUserDataSourceLazy = netWorkUserDataSourceLazy;
        this.localUserDataSourceLazy = localUserDataSourceLazy;
        this.groupList = new ArrayList<>();
        this.groupListSubject = BehaviorSubject.createDefault(groupList);
        this.groupUnReadCnt = BehaviorSubject.createDefault(unReadGroupCnt());

        messageList = new ArrayList<>();
        messageListSubject = PublishSubject.create();

        localUserDataSourceLazy.get().loadUser().subscribe(user -> {
            if (user.isValid()) {
                // Get from local database
                localDataSource.get().loadGroupList().subscribe(list -> {
                    Log.d(TAG, "----------------LoadList:" + list.size());
                    groupList.addAll(list);
                    groupUnReadCnt.onNext(unReadGroupCnt());
                });
                mUserId = user.userId();
            }
        });

    }

    @Override
    public Observable<Integer> getGroupUnReadCnt() {
        return groupUnReadCnt;
    }

    @Override
    public Observable<List<Message>> getMessageList(int userId, int page, int size) {
        return messageListSubject;
        /*return Observable.defer(() -> networkDataSource.get().getMessageList(userId, page, size))
                .flatMap(list -> {
                    // Get user id list
                    int length = list.size();
                    if (length > 0) {
                        // Get user list from server
                        return netWorkUserDataSourceLazy.get().getUserInformation(userId).flatMap(user -> {
                            List<Message> messageList = new ArrayList<Message>();
                            String imageId = (user != null) ? user.imageId() : Strings.EMPTY;
                            for (int i = 0; i < list.size(); i++) {
                                MessageTable messageTable = list.get(i);
                                messageList.add(Message.builder()
                                        .groupId(CommonCheck.getGroupId(mUserId, userId))
                                        .isRead(messageTable.getIsRead())
                                        .message(messageTable.getMessage())
                                        .imageId(messageTable.getUserId() == mUserId ? Strings.EMPTY : imageId)
                                        .timeStamp(messageTable.getTimeStamp())
                                        .userId(messageTable.getUserId())
                                        .isLocal(messageTable.getUserId() == mUserId)
                                        .build());
                            }
                            return Observable.just(messageList);
                        });
                    } else {
                        return Observable.just(new ArrayList<>());
                    }
                });*/
    }

    @Override
    public Observable<Message> messageUpdate(int userId) {
        return Observable.defer(() -> localDataSource.get().loadMessageList(CommonCheck.getGroupId(mUserId, userId)))
                .flatMap(list -> {
                    synchronized (messageList) {
                        Log.d(TAG, "Clear message list:" + userId);
                        messageList.clear();
                        messageList.addAll(list);
                        Log.d(TAG, "Add local list:" + userId + "," + messageList.size());
                        messageListSubject.onNext(messageList);
                    }
                    return networkDataSource.get().messageUpdate(userId)
                            .flatMap(messageTable -> {
                                Log.d(TAG, "new message:" + messageTable.getMessage());
                                if (messageList.isEmpty()) {
                                    return netWorkUserDataSourceLazy.get().getUserInformation(userId).flatMap(user -> Observable.just(Message.builder()
                                                .groupId(CommonCheck.getGroupId(mUserId, userId))
                                                .isRead(messageTable.getIsRead())
                                                .message(messageTable.getMessage())
                                                .imageId(messageTable.getUserId() == mUserId ? Strings.EMPTY : user.imageId())
                                                .timeStamp(messageTable.getTimeStamp())
                                                .userId(messageTable.getUserId())
                                                .isLocal(messageTable.getUserId() == mUserId)
                                                .build()));
                                } else {
                                    return Observable.just(Message.builder()
                                            .groupId(CommonCheck.getGroupId(mUserId, userId))
                                            .isRead(messageTable.getIsRead())
                                            .message(messageTable.getMessage())
                                            .imageId(messageTable.getUserId() == mUserId ? Strings.EMPTY : messageList.get(0).imageId())
                                            .timeStamp(messageTable.getTimeStamp())
                                            .userId(messageTable.getUserId())
                                            .isLocal(messageTable.getUserId() == mUserId)
                                            .build());
                                }
                            })
                            .filter(message -> {
                                Log.d(TAG, "MessageSize:" +messageList.size());
                                if (!messageList.isEmpty())
                                    Log.d(TAG, "CheckLasMessage:" + messageList.get(messageList.size()-1).toString());
                                if ((messageList.isEmpty() || (messageList.get(messageList.size()-1).timeStamp() < message.timeStamp()))) {
                                    return true;
                                } else {
                                    Log.d(TAG, message.toString());
                                    return false;
                                }
                            })
                            .flatMap(message -> localDataSource.get().storeMessage(CommonCheck.getGroupId(userId,mUserId),message))
                            .doOnNext(message -> messageList.add(message))
                            .doOnNext(message -> messageListSubject.onNext(messageList));
                });
    }

    @Override
    public Observable<List<Group>> getGroupList(int page, int size) {
        Log.d(TAG, "GetGroupList");
        /*return Observable.defer(() -> networkDataSource.get().groupUpdate())
                .flatMap(groupTable -> {
                    int localUser = localUserDataSourceLazy.get().loadUser().blockingFirst().userId();
                    int userId = 0;
                    int length = groupTable.users().size();
                    for (int i = 0; i < length; i++) {
                        userId = Integer.parseInt(groupTable.users().get(i));
                        if (userId != localUser)
                            break;
                    }
                    return netWorkUserDataSourceLazy.get().getPublicUserInfo(userId)
                            .flatMap(newUser -> Observable.just(Group.builder()
                                    .groupId(groupTable.groupId())
                                    .isRead(groupTable.isRead())
                                    .timeStamp(groupTable.timeStamp())
                                    .userName(newUser.name())
                                    .userImage(newUser.imageId())
                                    .lastMessage(groupTable.lastMessage())
                                    .users(groupTable.users())
                                    .build()));
                }).toList().toObservable();*/
        // TODO #170502
        return groupListSubject;
        /*return Observable.defer(() -> networkDataSource.get().getGroupList(page, size))
                .flatMap(list -> {
                    // Get user id list
                    int length = list.size();
                    if (length > 0) {
                        List<Integer> userList = new ArrayList<Integer>(length);
                        for (Iterator<GroupTable> iterator = list.iterator(); iterator.hasNext(); ) {
                            int id = Integer.parseInt(iterator.next().users().get(0));
                            userList.add(id);
                        }
                        // Get user list from server
                        return netWorkUserDataSourceLazy.get().getListUserInfo(userList).map(users -> {
                            List<Group> groups = new ArrayList<Group>();
                            for (int i = 0; i < length; i++) {
                                GroupTable groupTable = list.get(i);
                                User user = getUser(users, userList.get(i));
                                if (user != null ) {
                                    groups.add(Group.builder()
                                            .groupId(groupTable.groupId())
                                            .isRead(groupTable.isRead())
                                            .timeStamp(groupTable.timeStamp())
                                            .userName(user.name())
                                            .userImage(user.imageId())
                                            .lastMessage(groupTable.lastMessage())
                                            .users(groupTable.users())
                                            .build());
                                }
                            }
                            return groups;
                        });
                    } else {
                        return Observable.just(new ArrayList<Group>());
                    }
                }).flatMap(list -> localDataSource.get().storeGroupList(list));
                */
                /*.flatMapIterable(groupTables -> {
                    Log.d("FlatIterable", groupTables.size() + "");
                    return groupTables;
                })
                .flatMap(groupTable -> {
                    int userId = Integer.parseInt(groupTable.users().get(0));
                    return localUserDataSourceLazy.get().loadPublicUserInfo(userId)
                            .flatMap(user -> {
                                if (user.isValid()) {
                                    Log.d("USER", user.userId() + "");
                                    return Observable.just(Group.builder()
                                            .groupId(groupTable.groupId())
                                            .isRead(groupTable.isRead())
                                            .timeStamp(groupTable.timeStamp())
                                            .userName(user.name())
                                            .userImage(user.imageId())
                                            .lastMessage(groupTable.lastMessage())
                                            .users(groupTable.users())
                                            .build());
                                } else {
                                    return netWorkUserDataSourceLazy.get().getPublicUserInfo(userId)
                                            .flatMap(newUser -> Observable.just(Group.builder()
                                                    .groupId(groupTable.groupId())
                                                    .isRead(groupTable.isRead())
                                                    .timeStamp(groupTable.timeStamp())
                                                    .userName(newUser.name())
                                                    .userImage(newUser.imageId())
                                                    .lastMessage(groupTable.lastMessage())
                                                    .users(groupTable.users())
                                                    .build()));
                                }
                            });
                })
                .toList()
                .toObservable()
                .flatMap(list -> localDataSource.get().storeGroupList(list));
                */
    }

    @Override
    public Observable<Group> groupUpdate() {
        return Observable.defer(() -> networkDataSource.get().groupUpdate())
                .filter(this::isGroupUpdate)
                .flatMap(groupTable -> {
                    // TODO #170502
                    Group group = getGroup(groupTable);
                    if (group != null) {
                        Group updated = Group.builder()
                                .groupId(groupTable.groupId())
                                .isRead(groupTable.isRead())
                                .timeStamp(groupTable.timeStamp())
                                .userName(group.userName())
                                .userImage(group.userImage())
                                .lastMessage(groupTable.lastMessage())
                                .users(groupTable.users())
                                .build();
                        return Observable.just(updated);
                    } else {
                        int localUser = localUserDataSourceLazy.get().loadUser().blockingFirst().userId();
                        int userId = 0;
                        int size = groupTable.users().size();
                        for (int i = 0; i < size; i++) {
                            userId = Integer.parseInt(groupTable.users().get(i));
                            if (userId != localUser)
                                break;
                        }
                        return netWorkUserDataSourceLazy.get().getUserInformation(userId)
                                .filter(user -> user.isValid())
                                .flatMap(newUser -> Observable.just(Group.builder()
                                        .groupId(groupTable.groupId())
                                        .isRead(groupTable.isRead())
                                        .timeStamp(groupTable.timeStamp())
                                        .userName(newUser.name())
                                        .userImage(newUser.imageId())
                                        .lastMessage(groupTable.lastMessage())
                                        .users(groupTable.users())
                                        .build()));
                    }
                })
                .flatMap(group -> localDataSource.get().storeGroup(group))
                .doOnNext(this::addGroup);
    }

    @Override
    public Observable<Boolean> sendMessage(int toUserId, String message) {
        return Observable.defer(() -> networkDataSource.get().sendMessage(toUserId, message))
                .flatMap(result -> netWorkUserDataSourceLazy.get().messageNotification(toUserId, message));
    }

    @Override
    public Observable<Boolean> sawMessage(String groupId, long timeStamp) {
        return Observable.defer(() -> {
            networkDataSource.get().sawMessage(groupId, timeStamp);
            return Observable.just(true);
        });
    }

    private User getUser(List<User> users, int userId) {
        User user = null;
        for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
            user = iterator.next();
            if (user.userId() == userId) {
                break;
            }
        }
        return user;
    }

    // TODO #170502
    private @Nullable Group getGroup(GroupTable groupTable) {
        synchronized (groupList) {
            for (Iterator<Group> iterator = groupList.iterator(); iterator.hasNext();) {
                Group group = iterator.next();
                if (group.groupId().equals(groupTable.groupId())) {
                    return group;
                }
            }
            return null;
        }
    }

    private void addGroup(Group update) {
        synchronized (groupList) {
            int count = 0;
            for (Iterator<Group> iterator = groupList.iterator(); iterator.hasNext();) {
                Group group = iterator.next();
                if (group.timeStamp() > update.timeStamp()) {
                    count++;
                }
                if (group.groupId().equals(update.groupId())) {
                    iterator.remove();
                    break;
                }
            }
            groupList.add(count, update);
            groupListSubject.onNext(groupList);
        }
    }

    private boolean isGroupUpdate(GroupTable update) {
        synchronized (groupList) {
            boolean find = false;
            boolean isUpdate = false;
            for (Iterator<Group> iterator = groupList.iterator(); iterator.hasNext();) {
                Group group = iterator.next();
                if (group.groupId().equals(update.groupId())) {
                    find = true;
                    if (update.timeStamp() > group.timeStamp()) {
                        isUpdate = true;
                    }
                    break;
                }
            }
            Log.d(TAG, "CheckUpdate:" + update.groupId() + "," + (!find || isUpdate));
            return (!find || isUpdate);
        }
    }

    private int unReadGroupCnt() {
        synchronized (groupList) {
            int count = 0;
            for (Iterator<Group> iterator = groupList.iterator(); iterator.hasNext();) {
                if (!iterator.next().isRead())
                    count++;
            }
            return count;
        }
    }

    private void addMessage(int userId, Message message) {
        synchronized (messageList) {
            if (messageList.isEmpty() || messageList.get(messageList.size()-1).timeStamp() < message.timeStamp()) {
                Log.d(TAG, messageList.size() + "," + message.timeStamp());
                messageList.add(message);
            }
        }
    }
}
