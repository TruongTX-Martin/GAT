package com.gat.repository.impl;

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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageRepositoryImpl implements MessageRepository {
    private final Lazy<MessageDataSource> networkDataSource;
    private final Lazy<MessageDataSource> localDataSource;
    private final Lazy<UserDataSource> netWorkUserDataSourceLazy;
    private final Lazy<UserDataSource> localUserDataSourceLazy;


    public MessageRepositoryImpl(Lazy<MessageDataSource> networkDataSource,
                                 Lazy<MessageDataSource> localDataSource,
                                 Lazy<UserDataSource> netWorkUserDataSourceLazy,
                                 Lazy<UserDataSource> localUserDataSourceLazy
                                 ) {
        this.networkDataSource = networkDataSource;
        this.localDataSource = localDataSource;
        this.netWorkUserDataSourceLazy = netWorkUserDataSourceLazy;
        this.localUserDataSourceLazy = localUserDataSourceLazy;
    }

    @Override
    public Observable<List<Message>> getMessageList(int userId, int page, int size) {
        return Observable.defer(() -> networkDataSource.get().getMessageList(userId, page, size))
                .flatMap(list -> {
                    // Get user id list
                    int length = list.size();
                    if (length > 0) {
                        int localUser = localUserDataSourceLazy.get().loadUser().blockingFirst().userId();
                        // Get user list from server
                        return netWorkUserDataSourceLazy.get().getPublicUserInfo(userId).flatMap(user -> {
                            List<Message> messageList = new ArrayList<Message>();
                            for (int i = 0; i < length; i++) {
                                MessageTable messageTable = list.get(i);
                                messageList.add(Message.builder()
                                        .groupId(CommonCheck.getGroupId(localUser, userId))
                                        .isRead(messageTable.isRead())
                                        .message(messageTable.getMessage())
                                        .imageId((user != null && messageTable.getUserId() != userId) ? user.imageId() : Strings.EMPTY)
                                        .timeStamp(messageTable.getTimeStamp())
                                        .build());
                            }
                            return Observable.just(messageList);
                        });
                    } else {
                        return Observable.just(new ArrayList<>());
                    }
                });
    }

    @Override
    public Observable<List<Group>> getGroupList(int page, int size) {
        return Observable.defer(() -> networkDataSource.get().groupUpdate())
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
                }).toList().toObservable();
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
                .flatMap(groupTable -> {
                    int localUser = localUserDataSourceLazy.get().loadUser().blockingFirst().userId();
                    int userId = 0;
                    int size = groupTable.users().size();
                    for (int i = 0; i < size; i++) {
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
                });
    }

    @Override
    public Observable<Boolean> sendMessage(int toUserId, String message) {
        return Observable.defer(() -> networkDataSource.get().sendMessage(toUserId, message));
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
}
