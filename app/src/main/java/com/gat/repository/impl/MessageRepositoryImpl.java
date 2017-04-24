package com.gat.repository.impl;

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
    public Observable<List<Message>> getMessageList(String groupId, int page, int size) {
        return Observable.defer(() -> networkDataSource.get().getMessageList(groupId, page, size))
                .map(list -> {
                    List<Message> messages = new ArrayList<Message>();
                    // TODO get user
                    for (Iterator<MessageTable> iterator = list.iterator(); iterator.hasNext();) {
                        MessageTable messageTable = iterator.next();
                        messages.add(Message.builder().groupId(messageTable.getGroupId())
                                .isRead(messageTable.isRead())
                                .message(messageTable.getMessage())
                                .timeStamp(messageTable.getTimeStamp())
                                .userId(messageTable.getUserId())
                                .build()
                        );
                    }
                    return messages;
                });
    }

    @Override
    public Observable<List<Group>> getGroupList(int page, int size) {
        return Observable.defer(() -> networkDataSource.get().getGroupList(page, size))
                .flatMap(list -> {
                    int localUserId = localUserDataSourceLazy.get().loadUser().blockingFirst().userId();
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
                })
                .flatMap(list -> localDataSource.get().storeGroupList(list));
    }

    @Override
    public Observable<Boolean> sendMessage(String toUserId, String message) {
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
