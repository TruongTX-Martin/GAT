package com.gat.data;

import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
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
    List<GroupTable> groupList;

    public DebugLocalMessageDataSource() {
        groupList = new ArrayList<>();
    }
    @Override
    public Observable<List<MessageTable>> getMessageList(String groupId, int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<GroupTable>> getGroupList(int page, int size) {
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
