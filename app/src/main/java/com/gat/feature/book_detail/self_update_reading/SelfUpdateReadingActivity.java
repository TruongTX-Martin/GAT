package com.gat.feature.book_detail.self_update_reading;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.feature.book_detail.BookDetailActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

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

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable(
                getPresenter().onUpdateReadingStatusSuccess().subscribe(this::onUpdateReadingStatusSuccess)
        );

        radioGroup.setOnCheckedChangeListener(this);

        getPresenter().setEditionId(getScreen().editionId());
        getPresenter().setReadingStatus(getScreen().readingStatus());
        mReadingState = getScreen().readingStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setChecked (mReadingState);
        updateImageState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }

    @OnClick(R.id.button_remove_from_reading_list)
    void onRemoveBookFromReadingList () {
        mNewReadingState = ReadingState.REMOVE;
        // show dialog warning
        // yes -> update reading status = -1
        // no -> do nothing
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.ask_remove_from_reading_list));
        builder.setPositiveButton(android.R.string.yes, (dialog, id) -> {
            getPresenter().updateReadingStatus(ReadingState.REMOVE);
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void onUpdateReadingStatusSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(BookDetailActivity.KEY_UPDATE_READING_STATUS, mNewReadingState);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
