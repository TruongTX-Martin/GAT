package com.gat.data;

import android.util.Log;
import android.util.Pair;

import com.gat.common.util.Strings;
import com.gat.data.firebase.entity.GroupTable;
import com.gat.data.firebase.entity.MessageTable;
import com.gat.repository.datasource.MessageDataSource;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/2/17.
 */

public class DebugLocalMessageDataSource implements MessageDataSource {
    private final String TAG = DebugLocalMessageDataSource.class.getSimpleName();
    private static final String GROUP = "groups";
    private static final String MESSAGE = "messages";

    private final Book groupBook = Paper.book(GROUP);
    private final Book messageBook = Paper.book(MESSAGE);

    private final HashMap<String, Integer> messageCount;

    public DebugLocalMessageDataSource() {
        messageCount = new HashMap<>();
        List<String> groupIds = messageBook.getAllKeys();
        for (Iterator<String> iterator = groupIds.iterator(); iterator.hasNext();) {
            String groupId = iterator.next();
            messageCount.put(groupId, Paper.book(groupId).getAllKeys().size());
        }
    }


    @Override
    public Observable<List<GroupTable>> getGroupList(int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<GroupTable> groupUpdate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<MessageTable>> getMessageList(int userId, int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<MessageTable> messageUpdate(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Boolean> sendMessage(int toUserId, String message) {
        return null;
    }

    @Override
    public Observable<List<Group>> loadGroupList() {
        return Observable.fromCallable(() -> {
            List<String> groupIds = groupBook.getAllKeys();
            List<Group> groupList = new ArrayList<Group>();
            for (Iterator<String> iterator = groupIds.iterator(); iterator.hasNext();) {
                String groupId = iterator.next();
                Group group = groupBook.read(groupId);
                Log.d(TAG, "--------LoadGroup:" + group.toString());
                if (group != null && !Strings.isNullOrEmpty(group.userName()))
                    addSortGroupList(groupList, group);
                else
                    groupBook.delete(groupId);
            }
            Log.d(TAG, "LocalGroupList:" + groupList.size());
            return groupList;
        });
    }

    @Override
    public Observable<Group> storeGroup(Group group) {
        return Observable.fromCallable(() -> {
            Log.d(TAG, "--------StoreGroup:" + group.toString());
            groupBook.write(group.groupId(), group);
            return group;
        });
    }

    @Override
    public Observable<List<Message>> loadMessageList(String groupId) {
        return Observable.fromCallable(() -> {
            Integer count = messageCount.get(groupId);
            List<Message> messageList = new ArrayList<Message>();
            if (count != null) {
                int updateCount = count;
                for (int i = 1; i <= count; i++) {
                    Message message = Paper.book(groupId).read(Integer.toString(i));
                    if (message != null)
                        messageList.add(message);
                    else {
                        Paper.book(groupId).delete(Integer.toString(i));
                        updateCount--;
                    }
                }
                messageCount.put(groupId, updateCount);
            }
            Log.d(TAG, "--------LoadMessage:"+groupId + "," + messageList.size());
            return messageList;
        });
    }

    @Override
    public Observable<Message> storeMessage(String groupId, Message message) {
        return Observable.fromCallable(() -> {
            Log.d(TAG, "--------StoreMessage:"+groupId + "," + message.toString());
            Integer count = messageCount.get(groupId);
            if (count != null) {
                count++;
                messageCount.put(groupId, count);
                Paper.book(groupId).write(Integer.toString(count), message);
            } else {
                count = 1;
                messageCount.put(groupId, count);
                Paper.book(groupId).write(Integer.toString(count), message);
            }
            messageBook.write(groupId, count);
            return message;
        });
    }

    @Override
    public Observable<List<Message>> storeMessageList(String groupId, List<Message> messageList) {
        return Observable.fromCallable(() -> {
            int size = messageList.size();
            Log.d(TAG, "--------StoreMessageList:" + groupId + "," + size);
            if (size <= 0)
                return messageList;

            for (int i = 0; i < size; i++) {
                Paper.book(groupId).write(Integer.toString(i + 1), messageList.get(i));
            }
            messageBook.write(groupId, messageList.size());
            return messageList;
        });
    }

    @Override
    public void sawMessage(String groupId, long timeStamp) {

    }

    @Override
    public Observable<Boolean> clearData() {
        return Observable.fromCallable(() -> {
            // Delete group
            groupBook.destroy();
            List<String> groupIds = messageBook.getAllKeys();
            for (Iterator<String> iterator = groupIds.iterator(); iterator.hasNext();) {
                Paper.book(iterator.next()).destroy();
            }
            messageBook.destroy();
            return true;
        });
    }

    @Override
    public Observable<Boolean> makeNewGroup(int userId) {
        throw new UnsupportedOperationException();
    }

    private void addSortGroupList(List<Group> groupList, Group group) {
        int count = 0;
        for (Iterator<Group> iterator = groupList.iterator(); iterator.hasNext();) {
            if (iterator.next().timeStamp() > group.timeStamp())
                count++;
        }
        groupList.add(count, group);
    }
}
