package com.gat.repository;

import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/30/17.
 */

public interface MessageRepository {
    public Observable<Integer> getGroupUnReadCnt();
    public Observable<List<Message>> getMessageList(int userId, int page, int size);
    public Observable<Message> messageUpdate(int userId);
    public Observable<List<Group>> getGroupList(int page, int size);
    public Observable<Group> groupUpdate();
    public Observable<Boolean> sendMessage(int toUserId, String message);
    public Observable<Boolean> sawMessage(String groupId, long timeStamp);
}
