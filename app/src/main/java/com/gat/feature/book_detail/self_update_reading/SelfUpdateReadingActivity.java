package com.gat.feature.book_detail.self_update_reading;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class SelfUpdateReadingActivity extends ScreenActivity<SelfUpdateReadingScreen, SelfUpdateReadingPresenter> {

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.radio_want_to_read)
    RadioButton radioButtonWantToRead;

    @BindView(R.id.radio_reading_book)
    RadioButton radioButtonReadingBook;

    @BindView(R.id.radio_red_book)
    RadioButton radioButtonRedBook;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_self_update_reading;
    }

    @Override
    protected Class<SelfUpdateReadingPresenter> getPresenterClass() {
        return SelfUpdateReadingPresenter.class;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (radioButtonReadingBook.isChecked()) {

            } else if (radioButtonWantToRead.isChecked()) {

            } else if (radioButtonRedBook.isChecked()) {

            }
        });
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.button_remove_from_reading_list)
    void onRemoveBookFromReadingList () {

    }

    @OnClick(R.id.image_button_save)
    void onButtonSaveTap () {

    }

}
