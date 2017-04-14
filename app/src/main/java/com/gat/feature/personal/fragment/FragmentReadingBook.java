package com.gat.feature.personal.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.feature.personal.PersonalActivity;
import com.gat.feature.personal.adapter.BookReadingAdapter;
import com.gat.feature.personal.entity.BookReadingEntity;
import com.gat.feature.personal.entity.BookReadingInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentReadingBook extends Fragment {


    private RecyclerView recyclerView;
//    private RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager layoutManager;
    private View rootView;
    private List<BookReadingEntity> listBookReading = new ArrayList<>();
    private Context context;
    private BookReadingAdapter adapter;
    private RelativeLayout layoutBottom;
    private PersonalActivity parrentActivity;
    private BookReadingInput currentInput;
    private boolean isReaded,isReading,isToRead;
    private boolean isRequesting;
    private boolean isContinueMore = true;
    private TextView txtMessage;
    private ProgressBar progressBar,progressBarLoadMore;

    public void setParrentActivity(PersonalActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    public void setCurrentInput(BookReadingInput currentInput) {
        this.currentInput = currentInput;
    }

    public void setListBookReading(List<BookReadingEntity> listBookReading) {
        if(currentInput.getPage() == 1){
            this.listBookReading.clear();
        }
        if(currentInput.getPage() > 1 && listBookReading.size() ==0){
            isContinueMore = false;
        }
        this.listBookReading.addAll(listBookReading);
        isRequesting = false;
        hideLoading();
        hideLoadMore();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.layout_fragment_book_reading, container, false);
        initView();
        handleEvent();
        return rootView;
    }

    private void initView(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerReading);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookReadingAdapter(context,listBookReading,this);
        recyclerView.setAdapter(adapter);

        layoutBottom = (RelativeLayout) rootView.findViewById(R.id.layoutBottom);
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBarLoadMore = (ProgressBar) rootView.findViewById(R.id.progressLoadMore);
    }


    private void handleEvent() {
        layoutBottom.setOnClickListener(v -> showDialogFilter());
    }
    public  void loadMore(){
        if(isRequesting == false && isContinueMore) {
            currentInput.setPage(currentInput.getPage()+1);
            searchBook(currentInput);
            showLoadMore();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoadMore(){
        progressBarLoadMore.setVisibility(View.VISIBLE);
    }

    private void hideLoadMore() {
        progressBarLoadMore.setVisibility(View.GONE);
    }
    private void showDialogFilter() {
        if(currentInput == null){
            return;
        }
        isReaded = currentInput.isReadFilter();isReading = currentInput.isReadingFilter();isToRead = currentInput.isToReadFilter();
        LayoutInflater inflater = LayoutInflater.from(parrentActivity);
        View customView = inflater.inflate(R.layout.layout_popup_book_reading_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        ImageView imgClose = (ImageView) customView.findViewById(R.id.imgClose);
        RelativeLayout layoutReadedBorder = (RelativeLayout) customView.findViewById(R.id.layoutReadedBorder);
        RelativeLayout layoutReadedOverlay = (RelativeLayout) customView.findViewById(R.id.layoutReadedOverlay);
        RelativeLayout layoutReadingBorder = (RelativeLayout) customView.findViewById(R.id.layoutReadingBorder);
        RelativeLayout layoutReadingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutReadingOverLay);
        RelativeLayout layoutToReadBorder = (RelativeLayout) customView.findViewById(R.id.layoutToReadBorder);
        RelativeLayout layoutToReadOverlay = (RelativeLayout) customView.findViewById(R.id.layoutToReadOverlay);
        layoutReadedBorder.setOnClickListener(v -> {
            layoutReadedBorder.setVisibility(View.GONE);
            layoutReadedOverlay.setVisibility(View.VISIBLE);
            isReaded = false;
        });
        layoutReadedOverlay.setOnClickListener(v -> {
            layoutReadedOverlay.setVisibility(View.GONE);
            layoutReadedBorder.setVisibility(View.VISIBLE);
            isReaded = true;
        });
        layoutReadingBorder.setOnClickListener(v -> {
            layoutReadingOverlay.setVisibility(View.VISIBLE);
            layoutReadingBorder.setVisibility(View.GONE);
            isReading = false;
        });
        layoutReadingOverlay.setOnClickListener(v -> {
            layoutReadingOverlay.setVisibility(View.GONE);
            layoutReadingBorder.setVisibility(View.VISIBLE);
            isReading = true;
        });
        layoutToReadBorder.setOnClickListener(v -> {
            layoutToReadBorder.setVisibility(View.GONE);
            layoutToReadOverlay.setVisibility(View.VISIBLE);
            isToRead = false;
        });
        layoutToReadOverlay.setOnClickListener(v -> {
            layoutToReadOverlay.setVisibility(View.GONE);
            layoutToReadBorder.setVisibility(View.VISIBLE);
            isToRead = true;
        });
        imgClose.setOnClickListener(v -> {
            if (currentInput.isReadFilter() == isReaded && currentInput.isReadingFilter() == isReading
                    && currentInput.isToReadFilter() == isToRead){
                //do nothing
            }else{
                currentInput.setReadingFilter(isReading);
                currentInput.setReadFilter(isReaded);
                currentInput.setToReadFilter(isToRead);
                currentInput.setPage(1);
                isContinueMore = true;
                searchBook(currentInput);
            }
            popupWindow.dismiss();
        });
        if(currentInput.isReadFilter()){
            layoutReadedBorder.setVisibility(View.VISIBLE);
            layoutReadedOverlay.setVisibility(View.GONE);
        }else{
            layoutReadedBorder.setVisibility(View.GONE);
            layoutReadedOverlay.setVisibility(View.VISIBLE);
        }
        if(currentInput.isReadingFilter()){
            layoutReadingBorder.setVisibility(View.VISIBLE);
            layoutReadingOverlay.setVisibility(View.GONE);
        }else{
            layoutReadingBorder.setVisibility(View.GONE);
            layoutReadingOverlay.setVisibility(View.VISIBLE);
        }
        if(currentInput.isToReadFilter()){
            layoutToReadBorder.setVisibility(View.VISIBLE);
            layoutToReadOverlay.setVisibility(View.GONE);
        }else{
            layoutToReadBorder.setVisibility(View.GONE);
            layoutToReadOverlay.setVisibility(View.VISIBLE);
        }

        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private void searchBook(BookReadingInput input) {
        isRequesting = true;
        parrentActivity.requestReadingBooks(input);
        if(input.getPage() == 1){
            txtMessage.setVisibility(View.GONE);
            showLoading();
            listBookReading.clear();
        }
    }

}
