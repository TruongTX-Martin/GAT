package com.gat.feature.personaluser.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.personaluser.adapter.BookUserReadingAdapter;
import com.gat.feature.personaluser.adapter.BookUserSharingAdapter;
import com.gat.repository.entity.book.BookReadingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 20/04/2017.
 */

public class FragmentBookUserReading extends Fragment {


    private PersonalUserActivity parrentActivity;
    private View rootView;
    private BookReadingInput currentInput;
    private List<BookReadingEntity> listBook = new ArrayList<>();


    private ProgressBar progressLoadMore,progressBar;
    private TextView txtMessage;
    private RecyclerView recyclerView;
    private BookUserReadingAdapter adapter;
    private boolean isRequesting;
    private boolean isContinueMore = true;
    private Context context;

    public void setParrentActivity(PersonalUserActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    public void setListBook(List<BookReadingEntity> listBook) {
        if(listBook != null && listBook.size() > 0) {
            this.listBook.addAll(listBook);
        }
        if (currentInput.getPage() == 1) {
            if (listBook.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
            }
        }
        if (currentInput.getPage() > 1 && listBook.size() == 0) {
            isContinueMore = false;
        }
        hideLoading();
        hideLoadMore();
        isRequesting = false;
        adapter.notifyDataSetChanged();
    }

    public void setCurrentInput(BookReadingInput currentInput) {
        this.currentInput = currentInput;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.layout_fragment_book_user_reading, container, false);
        initView();
        requestUserReadingBook();
        return rootView;
    }

    private void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerRequest);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookUserReadingAdapter(context,listBook,this);
        recyclerView.setAdapter(adapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressLoadMore = (ProgressBar) rootView.findViewById(R.id.progressLoadMore);
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
    }

    private void showLoadMore(){
        progressLoadMore.setVisibility(View.VISIBLE);
    }

    private void hideLoadMore(){
        progressLoadMore.setVisibility(View.GONE);
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void loadMore(){
        if (isRequesting == false && isContinueMore) {
            showLoadMore();
            currentInput.setPage(currentInput.getPage() + 1);
            requestUserReadingBook();
        }
    }
    private void requestUserReadingBook() {
        isRequesting = true;
        parrentActivity.requestBookUserReading(currentInput);
        if (currentInput.getPage() == 1) {
            txtMessage.setVisibility(View.GONE);
            showLoading();
            listBook.clear();
        }
    }
}
