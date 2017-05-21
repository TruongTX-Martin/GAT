package com.gat.feature.notification;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.feature.bookdetailowner.BookDetailOwnerActivity;
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.login.LoginActivity;
import com.gat.feature.login.LoginScreen;
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

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private CompositeDisposable disposable;
    private CompositeDisposable disposablesCheckLogin;
    private NotificationAdapter adapter;
    private IMainDelegate delegate;


    private MainActivity mainActivity;
    private boolean isLogin;
    private Dialog dialog;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

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
                getPresenter().onLoadUserNotificationSuccess().subscribe(this::onLoadNotifiesSuccess),
                getPresenter().onError().subscribe(this::onError),
                getPresenter().onUnAuthorization().subscribe(this::onUnAuthorization)
        );

        disposablesCheckLogin = new CompositeDisposable(getPresenter().checkLoginSucess().subscribe(this::checkLoginSuccess),
                getPresenter().checkLoginFailed().subscribe(this::checkLoginFailed));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setupRecyclerView();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.backgroundCard);
        swipeRefreshLayout.setOnRefreshListener(() -> getPresenter().loadUserNotification(true));

        dialog = new Dialog(MainActivity.instance);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        dialog.setCanceledOnTouchOutside(false);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPresenter().loadUserNotification(true);
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if ( ! isVisibleToUser) {
            // rời khỏi page này thì tắt badge
            delegate.haveToPullNotifyPage(0);
        }

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

    public void checkLogin() {
        if (!isLogin) {
            //show dialog
            Button btnCancle = (Button) dialog.findViewById(R.id.btnCancle);
            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnCancle.setOnClickListener(v -> {
                mainActivity.setTabDesire(0);
                dialog.dismiss();
            });
            btnOk.setOnClickListener(v -> {
                MainActivity.start(MainActivity.instance.getApplicationContext(), LoginActivity.class, LoginScreen.instance(Strings.EMPTY));
            });
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                if(keyCode  == event.KEYCODE_BACK) {
                    dialog1.dismiss();
                    mainActivity.setTabDesire(0);
                    return true;
                }
                return false;
            });
            //hide loading in fragment request
        }
    }

    private void checkLoginSuccess(String input) {
        isLogin = true;
    }
    private void checkLoginFailed(String input) {
        isLogin = false;
    }

    private void setupRecyclerView () {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new NotificationAdapter();
        adapter.setOnItemNotifyClick(this);
        recyclerView.setAdapter(adapter);
    }

    private void onLoadNotifiesSuccess (DataResultListResponse<NotifyEntity> data) {

        swipeRefreshLayout.setRefreshing(false);

        if (null == data || data.getResultInfo().isEmpty()) {
            return;
        }

        MZDebug.w("Notify: " + data.getResultInfo().get(0).toString());
        delegate.haveToPullNotifyPage(data.getNotifyTotal());
        adapter.setItems(data.getResultInfo());
    }


    private void startBorrowFromAnother (int borrowRecordId) {
        Intent intent = new Intent(MainActivity.instance, BookDetailOwnerActivity.class);
        intent.putExtra("BorrowingRecordId", borrowRecordId);
        MainActivity.instance.startActivity(intent);
    }

    private void startBorrowByMe (int borrowRecordId) {
        Intent intent = new Intent(MainActivity.instance, BookDetailSenderActivity.class);
        intent.putExtra("BorrowingRecordId", borrowRecordId);
        MainActivity.instance.startActivity(intent);
    }

    private void start631RequestFromAnotherPerson () {
        delegate.goTo631PageRequest();
    }

    void onError (String message) {
        swipeRefreshLayout.setRefreshing(false);
        ClientUtils.showDialogError(getActivity(), getString(R.string.err), message);
    }

    void onUnAuthorization (String message) {
        swipeRefreshLayout.setRefreshing(false);

        // vì là fragment nằm trong MainActivity nên phải cast sang
        ClientUtils.showDialogUnAuthorization( getActivity() ,(MainActivity) getActivity(), message);
    }



}
