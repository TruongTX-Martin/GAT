package com.gat.feature.notification;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.gat.feature.bookdetailowner.BookDetailOwnerActivity;
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.main.IMainDelegate;
import com.gat.feature.main.MainActivity;
import com.gat.feature.message.GroupMessageActivity;
import com.gat.feature.message.MessageActivity;
import com.gat.feature.message.presenter.GroupMessageScreen;
import com.gat.feature.message.presenter.MessageScreen;
import com.gat.feature.notification.adapter.NotificationAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/27/2017.
 */

@SuppressLint("ValidFragment")
public class NotificationFragment extends ScreenFragment<NotificationScreen, NotificationPresenter>
implements NotificationAdapter.OnItemNotifyClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<NotifyEntity> mListNotifies;
    private CompositeDisposable disposable;
    private NotificationAdapter adapter;
    private IMainDelegate delegate;

    public NotificationFragment () {}

    public NotificationFragment (IMainDelegate callback) {
        this.delegate = callback;
    }

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

        getPresenter().loadUserNotification();
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
        if (null == list || list.isEmpty()) {
            return;
        }
        mListNotifies.addAll(list);
        MZDebug.w("___________________________________________ list notifies size: " + list.size());
        MZDebug.w("Notify: " + list.get(0).toString());

        adapter.setItems(list);
    }


    @Override
    public void onItemNotifyClick(NotifyEntity item) {

        switch (item.notificationType()) {
            case NotifyType.MESSAGE_UNREAD:
                MainActivity.start(getContext().getApplicationContext(), GroupMessageActivity.class, GroupMessageScreen.instance());
                break;

            case NotifyType.MESSAGE_FROM:
                MainActivity.start(getContext().getApplicationContext(), MessageActivity.class, MessageScreen.instance(item.referId()));
                break;

            case NotifyType.BORROW_REQUEST:
                startBorrowFromAnother(item.referId());
                break;

            case NotifyType.BORROW_UNLUCKY:
                startBorrowByMe(item.referId());
                break;

            case NotifyType.BORROW_ACCEPT:
                MainActivity.start(getContext().getApplicationContext(), MessageActivity.class, MessageScreen.instance(item.referId()));
                break;

            case NotifyType.BORROW_DONE:
                startBorrowByMe(item.referId());
                break;

            case NotifyType.BORROW_NEEDED:
                startBorrowByMe(item.referId());
                break;

            case NotifyType.BORROW_REFUSE:
                startBorrowByMe(item.referId());
                break;

            case NotifyType.BORROW_LOST:
                startBorrowByMe(item.referId());
                break;

            case NotifyType.BORROW_YOUR_TOTAL:
                start631RequestFromAnotherPerson();
                break;

            case NotifyType.BORROW_FROM_ANOTHER:
                start631RequestFromAnotherPerson();
                break;

            case NotifyType.BORROW_CANCEL:
                startBorrowFromAnother(item.referId());
                 break;
        }
    }

    private void startBorrowFromAnother (int borrowRecordId) {
        Intent intent = new Intent(MainActivity.instance, BookDetailSenderActivity.class);
        intent.putExtra("BorrowingRecordId", borrowRecordId);
        MainActivity.instance.startActivity(intent);
    }

    private void startBorrowByMe (int borrowRecordId) {
        Intent intent = new Intent(MainActivity.instance, BookDetailOwnerActivity.class);
        intent.putExtra("BorrowingRecordId", borrowRecordId);
        MainActivity.instance.startActivity(intent);
    }

    private void start631RequestFromAnotherPerson () {
        delegate.goTo631PageRequest();
    }
}
