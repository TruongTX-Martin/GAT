package com.gat.data.firebase;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.gat.R;
import com.gat.common.util.NotificationConfig;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.firebase.entity.NotificationParcelable;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.ResultActivity;
import com.gat.feature.start.StartActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();

    public static final String PUSH_NOTIFICATION = "Notification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null)
            return;

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message:" + remoteMessage.getNotification().getBody());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null && remoteMessage.getData() != null) {
            handleNotification(remoteMessage);
        }
    }

    private void handleNotification(RemoteMessage message) {
        Notification notification = parseNotification(message);
        if (notification == null)
            return;

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        contentView.setTextViewText(R.id.title, message.getNotification().getTitle());
        contentView.setTextViewText(R.id.text, message.getNotification().getBody());

        Intent pushNotification;
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        if (notification.pushType() == NotificationConfig.PushType.PRIVATE_MESSAGE) {
            pushNotification = new Intent();
            pushNotification.setAction("com.gat.private_message");
            if (pushNotification.resolveActivity(getPackageManager()) != null) {
                // TODO
            }
        } else {
            pushNotification = new Intent(getApplicationContext(), MainActivity.class);
        }
        pushNotification.putExtra("data", new NotificationParcelable(notification));
        notificationUtils.showNotificationMessage(message.getNotification().getTitle(),
                message.getNotification().getBody(),
                new Date().getTime(),
                pushNotification,
                contentView);
    }

    private @Nullable
    Notification parseNotification(RemoteMessage remoteMessage) {
        Notification notification = null;
        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification noti = remoteMessage.getNotification();

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();
        int badge = data.containsKey("badge") ? Integer.parseInt(data.get("badge")) : 0;
        int pushType = data.containsKey("pushType") ? Integer.parseInt(data.get("pushType"))  : 0;
        Log.d(TAG, "RemoteMessage:" + data.toString());
        switch (pushType) {
            case NotificationConfig.PushType.PRIVATE_MESSAGE:
            case NotificationConfig.PushType.BOOK_ACCEPTED:
                if (data.containsKey("senderId")) {
                    int senderId = Integer.parseInt(data.get("senderId"));
                    notification = Notification.instance(title, message, pushType, sound, badge, senderId, 0);
                }
                break;
            case NotificationConfig.PushType.REQUEST_BORROW:
            case NotificationConfig.PushType.BOOK_BORROWED:
            case NotificationConfig.PushType.BOOK_INFORM_LENT:
            case NotificationConfig.PushType.BOOK_INFORM_RETURN:
            case NotificationConfig.PushType.BOOK_REJECTED:
            case NotificationConfig.PushType.BOOK_INFORM_LOST:
            case NotificationConfig.PushType.BOOK_REQUEST_CANCEL:
                if (data.containsKey("requestId")) {
                    int requestId = Integer.parseInt(data.get("requestId"));
                    notification = Notification.instance(title, message, pushType, sound, badge, 0, requestId);
                }
                break;
            case NotificationConfig.PushType.BOOK_INFORM_BORROW:
            case NotificationConfig.PushType.BOOK_REQUESTED_QUANTITY:
                notification = Notification.instance(title, message, pushType, sound, badge, 0, 0);
                break;
            default:
                break;

        }
        return notification;
        /*TypeAdapter<Notification> typeAdapter = Notification.typeAdapter(new Gson());
        String data = remoteMessage.getData().toString();
        try {
            Notification notification = typeAdapter.fromJson(data);
            return notification;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    }
}
