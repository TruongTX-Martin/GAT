package com.gat.feature.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.NotifyEntity;

import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/27/2017.
 */

public class NotificationFragment extends ScreenFragment<NotificationScreen, NotificationPresenter> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private CompositeDisposable disposable;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_notification;
    }

    @Override
    protected Object getPresenterKey() {
        return NotificationScreen.instance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable(
                getPresenter().onLoadUserNotificationSuccess().subscribe(this::onLoadNotifiesSuccess)
        );

        setupRecyclerView();

        getPresenter().loadUserNotification();
    }

    private void setupRecyclerView () {

    }

    private void onLoadNotifiesSuccess (List<NotifyEntity> list) {
        MZDebug.w("___________________________________________ list notifies size: " + list.size());
    }


    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
