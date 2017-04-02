package com.gat.feature.personal.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.feature.personal.PersonalActivity;
import com.gat.feature.personal.adapter.BookSharingAdapter;
import com.gat.feature.personal.entity.BookEntity;
import com.gat.feature.personal.entity.BookInstanceInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookSharing extends Fragment {

    private ListView lvBookSharing;
    private ProgressBar progressBar;
    private ImageView imgFilter;
    private BookSharingAdapter adapterBookSharing;
    private List<BookEntity> listBook = new ArrayList<>();
    private PersonalActivity parrentActivity;
    private Context context;
    private View rootView;


    public void setParrentActivity(PersonalActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    public void setListBook(List<BookEntity> listBook) {
        this.listBook.clear();
        if (listBook != null && listBook.size() > 0) {
            this.listBook.addAll(listBook);
        }
        hideLoading();
        adapterBookSharing.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        context = getActivity().getApplicationContext();
        fakeData();
        adapterBookSharing = new BookSharingAdapter(listBook, getActivity().getApplicationContext());
        lvBookSharing = (ListView) rootView.findViewById(R.id.lvBookSharing);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        imgFilter = (ImageView) rootView.findViewById(R.id.imgFilter);
        lvBookSharing.setAdapter(adapterBookSharing);
        handleEvent();
        showLoading();
        return rootView;
    }

    private void handleEvent() {
        imgFilter.setOnClickListener(v -> {
            showDialogFilter();
        });

    }
    private void showLoading(){
        lvBookSharing.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideLoading(){
        lvBookSharing.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void fakeData() {
        //fake data listbook sharing
        for (int i = 0; i < 10; i++) {
            BookEntity info = new BookEntity();
            info.setTitle("Cuộc đời, sự nghiệp Steve Jobs" + i);
            info.setAuthor("Frank Luca" + i);
            info.setRateCount(4);
            info.setBorrowingUserName("Trần Duy Hưng " + i);
            listBook.add(info);
        }

    }

    private void showDialogFilter() {
        LayoutInflater inflater = LayoutInflater.from(parrentActivity);
        View customView = inflater.inflate(R.layout.layout_popup_book_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imgClose = (ImageView) customView.findViewById(R.id.imgClose);
        RelativeLayout layoutSharing = (RelativeLayout) customView.findViewById(R.id.layoutBookSharing);
        RelativeLayout layoutNotSharing = (RelativeLayout) customView.findViewById(R.id.layoutMyBook);
        RelativeLayout layoutLost = (RelativeLayout) customView.findViewById(R.id.layoutBookLost);
        imgClose.setOnClickListener(v -> popupWindow.dismiss());
        layoutSharing.setOnClickListener(v -> {
            BookInstanceInput input = new BookInstanceInput(true,false,false);
            parrentActivity.requestBookInstance(input);
            showLoading();
            popupWindow.dismiss();
        });
        layoutNotSharing.setOnClickListener(v -> {
            BookInstanceInput input = new BookInstanceInput(false,true,false);
            parrentActivity.requestBookInstance(input);
            showLoading();
            popupWindow.dismiss();
        });
        layoutLost.setOnClickListener(v -> {
            BookInstanceInput input = new BookInstanceInput(false,false,true);
            parrentActivity.requestBookInstance(input);
            showLoading();
            popupWindow.dismiss();
        });
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }


    private void filterBook(BookInstanceInput input) {
        parrentActivity.requestBookInstance(input);
    }

}
