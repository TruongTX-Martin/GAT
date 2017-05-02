package com.gat.data.firebase;

import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public interface FirebaseService {
    void Init();
    void getMessageList(int userId, int page, int size);
    Observable<List<MessageTable>> messageList();
    Observable<MessageTable> hasNewMessage(int userId);

    Observable<List<GroupTable>> getGroupList(int page, int size);
    Observable<GroupTable> groupUpdated();

    void sawMessage(String groupId, long timeStamp);

    void sendMessage(/*long fromUserId, */long toUserId, String message);
    Observable<Boolean> sendResult();
}
