package com.gat.feature.personal.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.feature.personal.PersonalActivity;
import com.gat.feature.personal.adapter.BookReadingAdapter;
import com.gat.feature.personal.entity.BookReadingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentReadingBook extends Fragment {


    private RecyclerView recyclerView;
    private View rootView;
    private List<BookReadingEntity> listBookReading = new ArrayList<>();
    private Context context;
    private BookReadingAdapter adapter;
    private RelativeLayout layoutBottom;
    private PersonalActivity parrentActivity;


    public void setParrentActivity(PersonalActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }
    public void setListBookReading(List<BookReadingEntity> listBookReading) {
        if(listBookReading.size() >0 ) {
            this.listBookReading.clear();
            this.listBookReading.addAll(listBookReading);
            adapter.notifyDataSetChanged();
        }
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
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookReadingAdapter(context,listBookReading);
        recyclerView.setAdapter(adapter);

        layoutBottom = (RelativeLayout) rootView.findViewById(R.id.layoutBottom);
    }

    private void handleEvent() {
        layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFilter();
            }
        });
    }

    private void showDialogFilter() {
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

        });
        layoutReadedOverlay.setOnClickListener(v -> {

        });
        layoutReadingBorder.setOnClickListener(v -> {

        });
        layoutReadingOverlay.setOnClickListener(v -> {

        });
        layoutToReadBorder.setOnClickListener(v -> {

        });
        layoutToReadOverlay.setOnClickListener(v -> {

        });
        imgClose.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }
}
