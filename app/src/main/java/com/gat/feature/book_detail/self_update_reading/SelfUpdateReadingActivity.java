package com.gat.feature.book_detail.self_update_reading;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RadioGroup;

import com.gat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class SelfUpdateReadingActivity extends Activity {


    @BindView(R.id.radio_group)
    RadioGroup radioGroup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_self_update_reading);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.button_remove_from_reading_list)
    void onRemoveBookFromReadingList () {

    }


}
