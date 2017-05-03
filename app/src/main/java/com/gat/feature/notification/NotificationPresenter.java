package com.gat.feature.notification;

import com.gat.data.response.impl.NotifyEntity;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/27/2017.
 */

public interface NotificationPresenter extends Presenter {

    void loadUserNotification ();
    Observable<List<NotifyEntity>> onLoadUserNotificationSuccess();



}
