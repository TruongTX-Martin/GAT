package com.gat.feature.book_detail.add_to_bookcase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gat.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class AddToBookcaseActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_to_bookcase);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }
}
