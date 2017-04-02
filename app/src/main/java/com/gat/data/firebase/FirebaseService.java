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
    void login();
    Observable<List<Message>> getMessageList(String groupId);
    Observable<List<String>> getGroupList();
    //Observable<List<String>> getUserList(String groupId);

    Observable<List<Group>> getGroupUserList(List<String> groupIds);

    Observable<List<Group>> getGroupLastMessage(List<Group> groupList);

    Observable<List<Group>> getGroupList(int page, int size);
}
