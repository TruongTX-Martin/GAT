package com.gat.feature.book_detail.self_update_reading;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class SelfUpdateReadingActivity
        extends ScreenActivity<SelfUpdateReadingScreen, SelfUpdateReadingPresenter>
        implements RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.radio_want_to_read)
    RadioButton radioButtonWantToRead;

    @BindView(R.id.radio_reading_book)
    RadioButton radioButtonReadingBook;

    @BindView(R.id.radio_red_book)
    RadioButton radioButtonRedBook;

    @BindView(R.id.image_button_save)
    ImageView imageButtonSave;

    private int mReadingState;
    private int mNewReadingState = -2;

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

        radioGroup.setOnCheckedChangeListener(this);

        if (getScreen().bookReadingInfo() != null) {
            mReadingState = getScreen().bookReadingInfo().getReadingStatus();
            setChecked (mReadingState);
        }

    }


    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.button_remove_from_reading_list)
    void onRemoveBookFromReadingList () {
        // show dialog warning
        // yes -> update reading status = -1
        //        finish();
        // no -> do nothing
    }

    @OnClick(R.id.image_button_save)
    void onButtonSaveTap () {
        MZDebug.w("___________________________________________________________ onButtonSaveTap __");
        getPresenter().updateReadingStatus(mNewReadingState);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (radioButtonReadingBook.isChecked()) {
            mNewReadingState = ReadingState.READING;
        } else if (radioButtonWantToRead.isChecked()) {
            mNewReadingState = ReadingState.TO_READ;
        } else if (radioButtonRedBook.isChecked()) {
            mNewReadingState = ReadingState.RED;
        }

        updateImageState ();
    }


    private void setChecked (int readingStatus) {
        switch (readingStatus) {
            case ReadingState.TO_READ:
                radioGroup.check(R.id.radio_want_to_read);
                break;

            case ReadingState.READING:
                radioGroup.check(R.id.radio_reading_book);
                break;

            case ReadingState.RED:
                radioGroup.check(R.id.radio_red_book);
                break;
        }
    }

    private void updateImageState () {
        if (mNewReadingState == mReadingState) {
            imageButtonSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
            imageButtonSave.setClickable(false);
            return;
        }

        imageButtonSave.setClickable(true);
        imageButtonSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
    }

}
