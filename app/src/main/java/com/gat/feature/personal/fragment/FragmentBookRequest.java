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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.feature.personal.PersonalActivity;
import com.gat.feature.personal.adapter.BookRequestAdapter;
import com.gat.feature.personal.entity.BookRequestEntity;

import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookRequest extends Fragment {

    private View rootView;
    private Context context;
    private RecyclerView recyclerView;
    private RelativeLayout layoutFilter;
    private List<BookRequestEntity> listBookRequest;
    private BookRequestAdapter adapter;
    private PersonalActivity parrentActivity;

    public void setListBookRequest(List<BookRequestEntity> list) {
        this.listBookRequest = list;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setParrentActivity(PersonalActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.layout_fragment_book_request, container, false);
        initView();
        handleEvent();
        return rootView;
    }

    private void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerRequest);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookRequestAdapter(context, listBookRequest, this);
        recyclerView.setAdapter(adapter);

        layoutFilter = (RelativeLayout) rootView.findViewById(R.id.layoutBottom);

    }

    private void handleEvent() {
        layoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFilter();
            }
        });
    }

    private void showDialogFilter() {
        LayoutInflater inflater = LayoutInflater.from(parrentActivity);
        View customView = inflater.inflate(R.layout.layout_popup_book_request_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        ImageView imgClose = (ImageView) customView.findViewById(R.id.imgClose);
        TextView txtRequestToYou = (TextView) customView.findViewById(R.id.txtRequestToYou);
        TextView txtRequestFromYou = (TextView) customView.findViewById(R.id.txtRequestFromYou);
        RelativeLayout layoutRequestToYouOVerlay = (RelativeLayout) customView.findViewById(R.id.layoutRequestToYouOverlay);
        RelativeLayout layoutRequestFromYouOVerlay = (RelativeLayout) customView.findViewById(R.id.layoutRequestFromYouOverlay);
        RelativeLayout layoutWaitingBorder = (RelativeLayout) customView.findViewById(R.id.layoutWaitingBorder);
        RelativeLayout layoutWaitingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutWaitingOverlay);
        RelativeLayout layoutContactingBorder = (RelativeLayout) customView.findViewById(R.id.layoutContactingBorder);
        RelativeLayout layoutContactingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutContactingOverLay);
        RelativeLayout layoutBorrowingBorder = (RelativeLayout) customView.findViewById(R.id.layoutBorrowingBorder);
        RelativeLayout layoutBorrowingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutBorrowingOverlay);
        RelativeLayout layoutOtherBorder = (RelativeLayout) customView.findViewById(R.id.layoutOtherBorder);
        RelativeLayout layoutOtherOverlay = (RelativeLayout) customView.findViewById(R.id.layoutOtherOverlay);

        imgClose.setOnClickListener(v -> popupWindow.dismiss());
        txtRequestToYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRequestToYouOVerlay.setVisibility(View.VISIBLE);
                layoutRequestFromYouOVerlay.setVisibility(View.GONE);
            }
        });
        txtRequestFromYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRequestFromYouOVerlay.setVisibility(View.VISIBLE);
                layoutRequestToYouOVerlay.setVisibility(View.GONE);
            }
        });
        layoutRequestToYouOVerlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRequestToYouOVerlay.setVisibility(View.GONE);
                layoutRequestFromYouOVerlay.setVisibility(View.VISIBLE);
            }
        });
        layoutRequestFromYouOVerlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRequestFromYouOVerlay.setVisibility(View.GONE);
                layoutRequestToYouOVerlay.setVisibility(View.VISIBLE);
            }
        });
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }
}
