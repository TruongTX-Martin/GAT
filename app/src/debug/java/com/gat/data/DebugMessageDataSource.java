package com.gat.data;

import android.util.Log;

import com.gat.data.firebase.FirebaseService;
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
    public Observable<List<Message>> getMessageList(String groupId) {
        return firebaseService.getMessageList(groupId);
        /*return Observable.fromCallable(() -> {
            Message[] messages = new Message[10];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = Message.instance(
                        Integer.toString(1000+i),
                        "Nguyen Van A",
                        "Hello, how are you?",
                        Integer.toString(10000+i),
                        "2017/04/01");
            }

            return listOfMessages(messages);
        }).delay(1000, TimeUnit.MILLISECONDS);
        */
    }

    @Override
    public Observable<List<Group>> getGroupList() {
        return firebaseService.getGroupList()
                .flatMap(groups -> firebaseService.getGroupUserList(groups))
                .flatMap(groupList -> {
                    Log.d("FlatMap", "GroupSize" + groupList.size());
                    return firebaseService.getGroupLastMessage(groupList);
                });
        /*
        return Observable.fromCallable(() -> {
            Group[] groups = new Group[10];
            for (int i = 0; i < groups.length; i++) {
                Map<String, Group.Member> map = new HashMap<String, Group.Member>();
                map.put("key" + Integer.toString(i), new Group.Member("user_name", "image_id"));
                //map.put("key" + Integer.toString(i), new Group.Member("user_name", "image_id"));
                groups[i] = new Group("userId",
                        "last message here",
                        "2017/04/01",
                        map
                );
            }

            return listOfGroups(groups);
        }).delay(1000, TimeUnit.MILLISECONDS);
        */
    }

    @Override
    public Observable<Boolean> sendMessage(String toUserId, String message) {
        return null;
    }

    @Override
    public Observable<List<Group>> storeGroupList(List<Group> groupList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Message>> storeMessageList(String groupId, List<Message> messageList) {
        throw new UnsupportedOperationException();
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
