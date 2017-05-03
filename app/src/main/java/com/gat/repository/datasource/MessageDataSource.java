package com.gat.repository.datasource;

import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public interface MessageDataSource {

    public Observable<List<GroupTable>> getGroupList(int page, int size);
    public Observable<GroupTable> groupUpdate();
    public Observable<List<MessageTable>> getMessageList(int userId, int page, int size);
    public Observable<MessageTable> messageUpdate(int userId);
    public Observable<Boolean> sendMessage(int toUserId, String message);

    public Observable<List<Group>> loadGroupList();
    public Observable<Group> storeGroup(Group group);
    public Observable<List<Message>> loadMessageList(String groupId);
    public Observable<Message> storeMessage(String groupId, Message message);
    public void sawMessage(String groupId, long timeStamp);
}
