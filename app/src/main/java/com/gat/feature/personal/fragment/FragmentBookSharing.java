package com.gat.feature.personal.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gat.R;
import com.gat.feature.personal.PersonalActivity;
import com.gat.feature.personal.adapter.BookSharingAdapter;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookEntity;
import com.gat.feature.personal.entity.BookInstanceInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookSharing extends Fragment {

    private ListView lvBookSharing;
    private ProgressBar progressBar,progressLoadMore;
    private ImageView imgFilter;
    private TextView txtMessage;
    private BookSharingAdapter adapterBookSharing;
    private List<BookEntity> listBook = new ArrayList<>();
    private PersonalActivity parrentActivity;
    private Context context;
    private View rootView;
    private BookInstanceInput currentInput;
    private boolean isSharing,isNotSharing,isLost;
    //for loadmore listview
    private int firstVisibleItem, visibleItemCount,totalItemCount;
    private boolean isRequesting = false;

    public void setParrentActivity(PersonalActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    public void setListBook(List<BookEntity> listBook) {
        if (listBook != null && listBook.size() > 0) {
            this.listBook.addAll(listBook);
        }
        if(currentInput.getPage() == 1){
            if(listBook.size() == 0){
                lvBookSharing.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
            }else{
                lvBookSharing.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
            }
        }
        hideLoading();
        hideLoadMore();
        isRequesting = false;
        adapterBookSharing.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        context = getActivity().getApplicationContext();
        adapterBookSharing = new BookSharingAdapter(listBook, getActivity().getApplicationContext(),this);
        lvBookSharing = (ListView) rootView.findViewById(R.id.lvBookSharing);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressLoadMore = (ProgressBar) rootView.findViewById(R.id.progressLoadMore);
        imgFilter = (ImageView) rootView.findViewById(R.id.imgFilter);
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        lvBookSharing.setAdapter(adapterBookSharing);
        handleEvent();
        showLoading();
        currentInput = new BookInstanceInput(true,false,false);
        searchBook(currentInput);
        return rootView;
    }

    private void handleEvent() {
        imgFilter.setOnClickListener(v -> {
            showDialogFilter();
        });
        lvBookSharing.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                try {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if (lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                        if(isRequesting == false) {
                            showLoadMore();
                            currentInput.setPage(currentInput.getPage()+1);
                            searchBook(currentInput);
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstItem, int visibleCount, int totalCount) {
                firstVisibleItem = firstItem;
                visibleItemCount = visibleCount;
                totalItemCount = totalCount;
            }
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
    private void showLoadMore(){
        progressLoadMore.setVisibility(View.VISIBLE);
    }
    private void hideLoadMore(){
        progressLoadMore.setVisibility(View.GONE);
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
        isSharing = currentInput.isSharingFilter();isNotSharing = currentInput.isNotSharingFilter(); isLost = currentInput.isLostFilter();
        LayoutInflater inflater = LayoutInflater.from(parrentActivity);
        View customView = inflater.inflate(R.layout.layout_popup_book_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        ImageView imgClose = (ImageView) customView.findViewById(R.id.imgClose);
        RelativeLayout layoutSharingBorder = (RelativeLayout) customView.findViewById(R.id.layoutSharingBorder);
        RelativeLayout layoutSharingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutSharingOverlay);
        RelativeLayout layoutNotSharingBorder = (RelativeLayout) customView.findViewById(R.id.layoutMyBookBorder);
        RelativeLayout layoutNotSharingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutMyBookOverLay);
        RelativeLayout layoutLostBorder = (RelativeLayout) customView.findViewById(R.id.layoutBookLostBorder);
        RelativeLayout layoutLostOverlay = (RelativeLayout) customView.findViewById(R.id.layoutBookLostOverlay);
        imgClose.setOnClickListener(v -> {
            if (currentInput.isSharingFilter() == isSharing && currentInput.isNotSharingFilter() == isNotSharing
                    && currentInput.isLostFilter() == isLost){
                //do nothing
            }else{
                currentInput.setSharingFilter(isSharing);
                currentInput.setNotSharingFilter(isNotSharing);
                currentInput.setLostFilter(isLost);
                currentInput.setPage(1);
                searchBook(currentInput);
            }
            currentInput.setSharingFilter(isSharing);
            currentInput.setNotSharingFilter(isNotSharing);
            currentInput.setLostFilter(isLost);
            popupWindow.dismiss();
        });
        layoutSharingBorder.setOnClickListener(v -> {
            layoutSharingBorder.setVisibility(View.GONE);
            layoutSharingOverlay.setVisibility(View.VISIBLE);
            isSharing = false;
        });
        layoutSharingOverlay.setOnClickListener(v -> {
            layoutSharingBorder.setVisibility(View.VISIBLE);
            layoutSharingOverlay.setVisibility(View.GONE);
           isSharing = true;
        });
        layoutNotSharingBorder.setOnClickListener(v -> {
            layoutNotSharingBorder.setVisibility(View.GONE);
            layoutNotSharingOverlay.setVisibility(View.VISIBLE);
            isNotSharing = false;
        });
        layoutNotSharingOverlay.setOnClickListener(v -> {
            layoutNotSharingBorder.setVisibility(View.VISIBLE);
            layoutNotSharingOverlay.setVisibility(View.GONE);
            isNotSharing = true;
        });
        layoutLostBorder.setOnClickListener(v -> {
            layoutLostBorder.setVisibility(View.GONE);
            layoutLostOverlay.setVisibility(View.VISIBLE);
            isLost = false;
        });
        layoutLostOverlay.setOnClickListener(v -> {
            layoutLostBorder.setVisibility(View.VISIBLE);
            layoutLostOverlay.setVisibility(View.GONE);
            isLost = true;
        });
        if(currentInput.isSharingFilter()){
            layoutSharingBorder.setVisibility(View.VISIBLE);
            layoutSharingOverlay.setVisibility(View.GONE);
        }else{
            layoutSharingBorder.setVisibility(View.GONE);
            layoutSharingOverlay.setVisibility(View.VISIBLE);
        }
        if(currentInput.isNotSharingFilter()){
            layoutNotSharingBorder.setVisibility(View.VISIBLE);
            layoutNotSharingOverlay.setVisibility(View.GONE);
        }else{
            layoutNotSharingBorder.setVisibility(View.GONE);
            layoutNotSharingOverlay.setVisibility(View.VISIBLE);
        }
        if(currentInput.isLostFilter()){
            layoutLostBorder.setVisibility(View.VISIBLE);
            layoutLostOverlay.setVisibility(View.GONE);
        }else{
            layoutLostBorder.setVisibility(View.GONE);
            layoutLostOverlay.setVisibility(View.VISIBLE);
        }
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }


    private void searchBook(BookInstanceInput input) {
        isRequesting = true;
        parrentActivity.requestBookInstance(input);
        if(input.getPage() == 1){
            txtMessage.setVisibility(View.GONE);
            showLoading();
            listBook.clear();
        }
    }
    public void changeStatusBook(BookEntity entity,int position){
        if(entity.getSharingStatus() == 0){
            entity.setSharingStatus(1);
        }else{
            entity.setSharingStatus(0);
        }
        BookChangeStatusInput input = new BookChangeStatusInput(entity.getInstanceId(),entity.getSharingStatus());
        parrentActivity.requestChangeStatusBook(input);
        listBook.remove(position);
        listBook.add(position,entity);
    }

}
