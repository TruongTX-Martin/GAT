package com.gat.feature.book_detail.list_user_sharing_book;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gat.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class ListUserSharingBookActivity extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_user_sharing_book);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.image_button_cancel)
    void onBack () {
        finish();
    }

}
