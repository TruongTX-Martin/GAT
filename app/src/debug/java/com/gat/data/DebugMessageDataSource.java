package com.gat.data;

import android.util.Log;

import com.gat.data.firebase.FirebaseService;
import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class DebugMessageDataSource implements MessageDataSource {
    private final FirebaseService firebaseService;

    public DebugMessageDataSource(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    @Override
    public Observable<List<MessageTable>> getMessageList(int userId, int page, int size) {
        firebaseService.getMessageList(userId, page, size);
        return firebaseService.messageList();
    }

    @Override
    public Observable<MessageTable> messageUpdate(int userId) {
        return firebaseService.hasNewMessage(userId);
    }

    @Override
    public Observable<List<GroupTable>> getGroupList(int page, int size) {
        return firebaseService.getGroupList(page, size);
    }

    @Override
    public Observable<GroupTable> groupUpdate() {
        return firebaseService.groupUpdated();
    }

    @Override
    public Observable<Boolean> sendMessage(int toUserId, String message) {
        firebaseService.sendMessage((long)toUserId, message);
        return firebaseService.sendResult();
    }

    @Override
    public Observable<List<Group>> loadGroupList() {
        return null;
    }

    @Override
    public Observable<Group> storeGroup(Group group) {
        return null;
    }

    @Override
    public Observable<List<Message>> loadMessageList(String groupId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Message> storeMessage(String groupId, Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sawMessage(String groupId, long timeStamp) {
        firebaseService.sawMessage(groupId, timeStamp);
    }

    private List<Message> listOfMessages(Message... messages) {
        ArrayList list = new ArrayList();
        if (messages != null) {
            Collections.addAll(list, messages);
        }
        return list;
    }

    private List<Group> listOfGroups(Group... groups) {
        ArrayList list = new ArrayList();
        if (groups != null) {
            Collections.addAll(list, groups);
        }
        return list;
    }
}
