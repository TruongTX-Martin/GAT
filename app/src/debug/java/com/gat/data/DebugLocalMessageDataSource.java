package com.gat.data;

import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/2/17.
 */

public class DebugLocalMessageDataSource implements MessageDataSource {
    List<Group> groupList;

    public DebugLocalMessageDataSource() {
        groupList = new ArrayList<>();
    }
    @Override
    public Observable<List<Message>> getMessageList(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Group>> getGroupList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Group>> loadMoreGroup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Message>> loadMoreMessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Boolean> sendMessage(String toUserId, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Group>> storeGroupList(List<Group> groupList) {
        groupList = groupList;
        return Observable.just(groupList);
    }

    @Override
    public Observable<List<Message>> storeMessageList(String groupId, List<Message> messageList) {
        return null;
    }
}
