package com.gat.feature.book_detail.list_user_sharing_book;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BorrowResponse;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/17/2017.
 */

public class ListUserSharingBookActivity extends ScreenActivity<ListUserSharingBookScreen, ListUserSharingBookPresenter>
        implements OnButtonBorrowClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private CompositeDisposable disposable;
    private UserSharingItemAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_list_user_sharing_book;
    }

    @Override
    protected Class<ListUserSharingBookPresenter> getPresenterClass() {
        return ListUserSharingBookPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return ListUserSharingBookScreen.instance(getScreen().listUser());
    }

    @Override
    protected ListUserSharingBookScreen getDefaultScreen() {
        return ListUserSharingBookScreen.instance(getScreen().listUser());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);

        disposable = new CompositeDisposable(
                getPresenter().onUserIdSuccess().subscribe(this::onGetUserSuccess),
                getPresenter().onUserIdFailure().subscribe(this::onGetUserFailure),
                getPresenter().onRequestBorrowBookSuccess().subscribe(this::onRequestBorrowSuccess),
                getPresenter().onRequestBorrowBookFailure().subscribe(this::onRequestBorrowFailure)
        );
        getPresenter().setListUser(getScreen().listUser());
        getPresenter().getUserId();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        hideProgress();
        progressDialog = null;
    }

    @OnClick(R.id.image_button_cancel)
    void onBack () {
        finish();
    }

    private void setupRecyclerViewComments (int userId) {
        adapter = new UserSharingItemAdapter(userId, this);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(adapter);
    }

    private int positionClicked;
    @Override
    public void onButtonBorrowClick(int position, UserResponse userResponse) {
        positionClicked= position;
        progressDialog.show();
        Toast.makeText(this, "position: " + position + ", User: " + userResponse.getName(), Toast.LENGTH_SHORT).show();
        getPresenter().requestBorrowBook(userResponse.getEditionId(), userResponse.getUserId());
    }

    private void onGetUserSuccess (int userId) {
        setupRecyclerViewComments(userId);
        adapter.setItems(getScreen().listUser());
    }

    private void onGetUserFailure (String message) {
        setupRecyclerViewComments(0);
        adapter.setItems(getScreen().listUser());
        Toast.makeText(this, "Không lấy được thông tin user, chưa đăng nhập?", Toast.LENGTH_SHORT).show();
    }

    private void onRequestBorrowSuccess (BorrowResponse borrowResponse) {
        hideProgress();
        adapter.updateBorrowStatus(positionClicked, borrowResponse.getRecordStatus());

    }

    private void onRequestBorrowFailure (String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideProgress () {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }


}
