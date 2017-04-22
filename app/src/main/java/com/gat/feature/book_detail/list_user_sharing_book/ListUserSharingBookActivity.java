package com.gat.feature.book_detail.list_user_sharing_book;

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

    CompositeDisposable disposable;
    UserSharingItemAdapter adapter;

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

        disposable = new CompositeDisposable(
                getPresenter().onUserIdSuccess().subscribe(this::onGetUserSuccess),
                getPresenter().onUserIdFailure().subscribe(this::onGetUserFailure),
                getPresenter().onRequestBorrowBookSuccess().subscribe(this::onRequestBorrowSuccess)
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

    @Override
    public void onButtonBorrowClick(int position, UserResponse userResponse) {
        Toast.makeText(this, "position: " + position + ", User: " + userResponse.getName(), Toast.LENGTH_SHORT).show();
        getPresenter().requestBorrowBook(userResponse.getEditionId(), userResponse.getUserId());
    }

    private void onGetUserSuccess (int userId) {
        setupRecyclerViewComments(userId);
        adapter.setItems(getScreen().listUser());
    }

    private void onGetUserFailure (String message) {
//        setupRecyclerViewComments(0);
//        adapter.setItems(getScreen().listUser());
        Toast.makeText(this, "Không lấy được thông tin user, chưa đăng nhập?", Toast.LENGTH_SHORT).show();
    }

    private void onRequestBorrowSuccess (BorrowResponse borrowResponse) {
        Toast.makeText(this, "Borrow success, xử lý row", Toast.LENGTH_SHORT).show();
    }



}
