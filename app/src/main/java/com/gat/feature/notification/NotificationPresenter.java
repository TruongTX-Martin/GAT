package com.gat.feature.notification;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.impl.NotifyEntity;
import com.rey.mvp2.Presenter;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mryit on 4/27/2017.
 */

public interface NotificationPresenter extends Presenter {

    // isRefresh = true -> lấy dữ liệu mới và cache cái mới xuống local
    // = false -> nếu có trong local rồi thì lấy từ local chứ không lấy từ server.
    void loadUserNotification (boolean isRefresh);
    Observable<DataResultListResponse<NotifyEntity>> onLoadUserNotificationSuccess();


    // isRefresh = false -> không hiện vì nó là lần load đầu tiên.

    // nếu isRefresh = true -> mà lỗi thì hiện lỗi
    Observable<String> onError ();

    // nếu isRefresh = true -> mà lỗi 401 thì quăng ra thằng này.
    Observable<String> onUnAuthorization ();

}
