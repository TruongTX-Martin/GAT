package com.gat.feature.book_detail.list_user_sharing_book;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class ListUserSharingBookActivity extends ScreenActivity<ListUserSharingBookScreen, ListUserSharingBookPresenter> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


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
        MZDebug.e("position 0:  " + getScreen().listUser().get(0).toString());
        getPresenter().setListUser(getScreen().listUser());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.image_button_cancel)
    void onBack () {
        finish();
    }

}
