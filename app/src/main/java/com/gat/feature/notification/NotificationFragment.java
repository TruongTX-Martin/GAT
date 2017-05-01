package com.gat.feature.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.feature.notification.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/27/2017.
 */

public class NotificationFragment extends ScreenFragment<NotificationScreen, NotificationPresenter>
implements NotificationAdapter.OnItemNotifyClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<NotifyEntity> mListNotifies;
    private CompositeDisposable disposable;
    private NotificationAdapter adapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().loadUserNotification();
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private void setupRecyclerView () {
        mListNotifies = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new NotificationAdapter();
        adapter.setOnItemNotifyClick(this);
        recyclerView.setAdapter(adapter);
    }

    private void onLoadNotifiesSuccess (List<NotifyEntity> list) {
        mListNotifies.addAll(list);
        MZDebug.w("___________________________________________ list notifies size: " + list.size());

        adapter.setItems(list);
    }


    @Override
    public void onItemNotifyClick(NotifyEntity item) {
        Toast.makeText(mContext, "Chuyển trang theo: Notify Type: " + item.notificationType(), Toast.LENGTH_SHORT).show();

        switch (item.notificationType()) {
            case NotifyType.MESSAGE_UNREAD:

                break;

            case NotifyType.MESSAGE_FROM:

                break;

            case NotifyType.BORROW_REQUEST:


                break;

            case NotifyType.BORROW_UNLUCKY:


                break;

            case NotifyType.BORROW_ACCEPT:


                break;

            case NotifyType.BORROW_NEEDED:


                break;

            case NotifyType.BORROW_REFUSE:


                break;

            case NotifyType.BORROW_LOST:


                break;

            case NotifyType.BORROW_YOUR_TOTAL:


                break;

            case NotifyType.BORROW_FROM_ANOTHER:

                break;

            case NotifyType.BORROW_CANCEL:

                 break;
        }
    }
}
