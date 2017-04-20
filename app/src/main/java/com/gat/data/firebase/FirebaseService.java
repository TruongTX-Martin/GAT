package com.gat.data.firebase;

import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public interface FirebaseService {
    void getMessageList(String groupId, int page, int size);
    Observable<List<Message>> messageList();
    Observable<Message> hasNewMessage();

    void getGroupList(int page, int size);
    Observable<List<Group>> groupList();
    Observable<Group> groupUpdated();

    void sendMessage(long fromUserId, long toUserId, String message);
    Observable<Boolean> sendResult();
}
