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
import com.gat.feature.main.MainActivity;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.personal.adapter.BookRequestAdapter;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.repository.entity.book.BookRequestEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookRequest extends Fragment {

    private View rootView;
    private Context context;
    private RecyclerView recyclerView;
    private RelativeLayout layoutFilter;
    private List<BookRequestEntity> listBookRequest = new ArrayList<>();
    private BookRequestAdapter adapter;
    private PersonalFragment parrentFragment;
    private BookRequestInput currentInput = new  BookRequestInput(true,true,true,true);
    //layout for popup filter
    private RelativeLayout layoutWaitingBorder, layoutWaitingOverlay, layoutContactingBorder, layoutContactingOverlay;
    private RelativeLayout layoutBorrowingBorder, layoutBorrowingOverlay, layoutOtherBorder, layoutOtherOverlay;

    private boolean sharingWaitToConfirm, sharingContacting, sharingBorrowing, sharingOther;
    private boolean borrowingWaitToConfirm, borrowingContacting, borrowingBorrowing, borrowingOther;

    private TextView txtMessage;
    private ProgressBar progressBar, progressBarLoadMore;
    private boolean isRequesting;
    private boolean isContinueMore = true;
    private boolean isInitView;

    private int numberRequestFromYou,numberRequestToYou;
    private boolean setTitleTo, setTitleFrom;

    public void setCurrentInput(BookRequestInput currentInput) {
        this.currentInput = currentInput;
    }


    public void setNumberRequestFromYou(int numberRequestFromYou) {
        this.numberRequestFromYou = numberRequestFromYou;
    }

    public void setNumberRequestToYou(int numberRequestToYou) {
        this.numberRequestToYou = numberRequestToYou;
    }

    public int getNumberRequestFromYou() {
        return numberRequestFromYou;
    }

    public int getNumberRequestToYou() {
        return numberRequestToYou;
    }

    public void setListBookRequest(List<BookRequestEntity> list) {
        if (currentInput != null && currentInput.getPage() == 1) {
            listBookRequest.clear();
            if (isInitView) {
                if (list.size() == 0) {
                    txtMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    txtMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

        }
        if (currentInput != null && currentInput.getPage() > 1 && list.size() == 0) {
            isContinueMore = false;
        }
        this.listBookRequest.addAll(list);
        setTitleFrom = false; setTitleTo = false;
        if(listBookRequest.size() > 0) {
            for (int i=0; i< listBookRequest.size() ; i++) {
                if (listBookRequest.get(i).getRecordType() == 1) {
                    if(!setTitleTo) {
                        listBookRequest.get(i).setHeader(true);
                        setTitleTo = true;
                    }
                }else if(listBookRequest.get(i).getRecordType() == 2 || listBookRequest.get(i).getRecordType() == 0){
                    if (!setTitleFrom) {
                        listBookRequest.get(i).setHeader(true);
                        setTitleFrom = true;
                    }
                }
            }
        }
        hideLoading();
        hideLoadMore();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        isRequesting = false;
    }

    public void setParrentFragment(PersonalFragment parrentFragment) {
        this.parrentFragment = parrentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.layout_fragment_book_request, container, false);
        initView();
        handleEvent();
        isInitView = true;
        searchBook();
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
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBarLoadMore = (ProgressBar) rootView.findViewById(R.id.progressLoadMore);
    }

    private void handleEvent() {
        layoutFilter.setOnClickListener(v -> showDialogFilter());
    }

    public void agreedRequest(BookRequestEntity entity){
        RequestStatusInput statusInput = new RequestStatusInput();
        statusInput.setCurrentStatus(0);
        statusInput.setNewStatus(2);
        statusInput.setRecordId(entity.getRecordId());
        parrentFragment.requestBookOwner(statusInput);
    }

    public void rejectRequest(BookRequestEntity entity){
        RequestStatusInput statusInput = new RequestStatusInput();
        statusInput.setCurrentStatus(0);
        statusInput.setNewStatus(5);
        statusInput.setRecordId(entity.getRecordId());
        parrentFragment.requestBookOwner(statusInput);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadMore() {
        progressBarLoadMore.setVisibility(View.VISIBLE);
    }

    private void hideLoadMore() {
        if (progressBarLoadMore != null) {
            progressBarLoadMore.setVisibility(View.GONE);
        }
    }

    public void loadMore() {
        if (isRequesting == false && isContinueMore) {
            currentInput.setPage(currentInput.getPage() + 1);
            searchBook();
            showLoadMore();
        }
    }

    private void showDialogFilter() {
        if(currentInput == null) return;
        sharingWaitToConfirm = currentInput.isSharingWaitConfirm();
        sharingContacting = currentInput.isSharingContacting();
        sharingBorrowing = currentInput.isSharingBorrowing();
        sharingOther = currentInput.isSharingOther();
        borrowingWaitToConfirm = currentInput.isBorrowWaitConfirm();
        borrowingContacting = currentInput.isBorrowBorrowing();
        borrowingBorrowing = currentInput.isBorrowBorrowing();
        borrowingOther = currentInput.isBorrowOther();
        LayoutInflater inflater = LayoutInflater.from(MainActivity.instance);
        View customView = inflater.inflate(R.layout.layout_popup_book_request_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        RelativeLayout imgClose = (RelativeLayout) customView.findViewById(R.id.layoutClose);
        TextView txtRequestToYou = (TextView) customView.findViewById(R.id.txtRequestToYou);
        TextView txtRequestFromYou = (TextView) customView.findViewById(R.id.txtRequestFromYou);
        RelativeLayout layoutRequestToYouOVerlay = (RelativeLayout) customView.findViewById(R.id.layoutRequestToYouOverlay);
        RelativeLayout layoutRequestFromYouOVerlay = (RelativeLayout) customView.findViewById(R.id.layoutRequestFromYouOverlay);
        layoutWaitingBorder = (RelativeLayout) customView.findViewById(R.id.layoutWaitingBorder);
        layoutWaitingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutWaitingOverlay);
        layoutContactingBorder = (RelativeLayout) customView.findViewById(R.id.layoutContactingBorder);
        layoutContactingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutContactingOverLay);
        layoutBorrowingBorder = (RelativeLayout) customView.findViewById(R.id.layoutBorrowingBorder);
        layoutBorrowingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutBorrowingOverlay);
        layoutOtherBorder = (RelativeLayout) customView.findViewById(R.id.layoutOtherBorder);
        layoutOtherOverlay = (RelativeLayout) customView.findViewById(R.id.layoutOtherOverlay);

        imgClose.setOnClickListener(v -> {
            if (sharingWaitToConfirm == currentInput.isSharingWaitConfirm()
                    && sharingContacting == currentInput.isSharingContacting()
                    && sharingBorrowing == currentInput.isSharingBorrowing()
                    && sharingOther == currentInput.isSharingOther()
                    && borrowingWaitToConfirm == currentInput.isBorrowWaitConfirm()
                    && borrowingContacting == currentInput.isBorrowContacting()
                    && borrowingBorrowing == currentInput.isBorrowBorrowing()
                    && borrowingOther == currentInput.isBorrowOther()) {
                //do nothing
            } else {
                currentInput.setSharingWaitConfirm(sharingWaitToConfirm);
                currentInput.setSharingContacting(sharingContacting);
                currentInput.setSharingBorrowing(sharingBorrowing);
                currentInput.setSharingOther(sharingOther);
                currentInput.setBorrowWaitConfirm(borrowingWaitToConfirm);
                currentInput.setBorrowContacting(borrowingContacting);
                currentInput.setBorrowBorrowing(borrowingBorrowing);
                currentInput.setBorrowOther(borrowingOther);
                currentInput.setPage(1);
                searchBook();
            }
            popupWindow.dismiss();
        });
        layoutRequestToYouOVerlay.setVisibility(View.GONE);
        layoutRequestFromYouOVerlay.setVisibility(View.VISIBLE);

        refreshViewSharing();


        txtRequestToYou.setOnClickListener(v -> {
            layoutRequestToYouOVerlay.setVisibility(View.VISIBLE);
            layoutRequestFromYouOVerlay.setVisibility(View.GONE);
            refreshViewBorrowing();
        });
        txtRequestFromYou.setOnClickListener(v -> {
            layoutRequestFromYouOVerlay.setVisibility(View.VISIBLE);
            layoutRequestToYouOVerlay.setVisibility(View.GONE);
            refreshViewSharing();
        });
        layoutRequestToYouOVerlay.setOnClickListener(v -> {
            layoutRequestToYouOVerlay.setVisibility(View.GONE);
            layoutRequestFromYouOVerlay.setVisibility(View.VISIBLE);
            refreshViewSharing();
        });
        layoutRequestFromYouOVerlay.setOnClickListener(v -> {
            layoutRequestFromYouOVerlay.setVisibility(View.GONE);
            layoutRequestToYouOVerlay.setVisibility(View.VISIBLE);
            refreshViewBorrowing();
        });
        layoutWaitingBorder.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingWaitToConfirm = false;
            } else {
                borrowingWaitToConfirm = false;
            }
            layoutWaitingOverlay.setVisibility(View.VISIBLE);
            layoutWaitingBorder.setVisibility(View.GONE);
        });
        layoutWaitingOverlay.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingWaitToConfirm = true;
            } else {
                borrowingWaitToConfirm = true;
            }
            layoutWaitingOverlay.setVisibility(View.GONE);
            layoutWaitingBorder.setVisibility(View.VISIBLE);
        });
        layoutContactingBorder.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingContacting = false;
            } else {
                borrowingContacting = false;
            }
            layoutContactingOverlay.setVisibility(View.VISIBLE);
            layoutContactingBorder.setVisibility(View.GONE);
        });
        layoutContactingOverlay.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingContacting = true;
            } else {
                borrowingContacting = true;
            }
            layoutContactingOverlay.setVisibility(View.GONE);
            layoutContactingBorder.setVisibility(View.VISIBLE);
        });
        layoutBorrowingBorder.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingBorrowing = false;
            } else {
                borrowingBorrowing = false;
            }
            layoutBorrowingOverlay.setVisibility(View.VISIBLE);
            layoutBorrowingBorder.setVisibility(View.GONE);
        });
        layoutBorrowingOverlay.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingBorrowing = true;
            } else {
                borrowingBorrowing = true;
            }
            layoutBorrowingOverlay.setVisibility(View.GONE);
            layoutBorrowingBorder.setVisibility(View.VISIBLE);
        });
        layoutOtherBorder.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingOther = false;
            } else {
                borrowingOther = false;
            }
            layoutOtherOverlay.setVisibility(View.VISIBLE);
            layoutOtherBorder.setVisibility(View.GONE);
        });
        layoutOtherOverlay.setOnClickListener(v -> {
            if (layoutRequestToYouOVerlay.getVisibility() == View.GONE) {
                sharingOther = true;
            } else {
                borrowingOther = true;
            }
            layoutOtherOverlay.setVisibility(View.GONE);
            layoutOtherBorder.setVisibility(View.VISIBLE);
        });
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private void refreshViewSharing() {
        if (sharingWaitToConfirm) {
            layoutWaitingOverlay.setVisibility(View.GONE);
            layoutWaitingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutWaitingOverlay.setVisibility(View.VISIBLE);
            layoutWaitingBorder.setVisibility(View.GONE);
        }

        if (sharingContacting) {
            layoutContactingOverlay.setVisibility(View.GONE);
            layoutContactingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutContactingOverlay.setVisibility(View.VISIBLE);
            layoutContactingBorder.setVisibility(View.GONE);
        }

        if (sharingBorrowing) {
            layoutBorrowingOverlay.setVisibility(View.GONE);
            layoutBorrowingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutBorrowingOverlay.setVisibility(View.VISIBLE);
            layoutBorrowingBorder.setVisibility(View.GONE);
        }

        if (sharingOther) {
            layoutOtherOverlay.setVisibility(View.GONE);
            layoutOtherBorder.setVisibility(View.VISIBLE);
        } else {
            layoutOtherOverlay.setVisibility(View.VISIBLE);
            layoutOtherBorder.setVisibility(View.GONE);
        }
    }

    private void refreshViewBorrowing() {
        if (borrowingWaitToConfirm) {
            layoutWaitingOverlay.setVisibility(View.GONE);
            layoutWaitingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutWaitingOverlay.setVisibility(View.VISIBLE);
            layoutWaitingBorder.setVisibility(View.GONE);
        }

        if (borrowingContacting) {
            layoutContactingOverlay.setVisibility(View.GONE);
            layoutContactingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutContactingOverlay.setVisibility(View.VISIBLE);
            layoutContactingBorder.setVisibility(View.GONE);
        }

        if (borrowingBorrowing) {
            layoutBorrowingOverlay.setVisibility(View.GONE);
            layoutBorrowingBorder.setVisibility(View.VISIBLE);
        } else {
            layoutBorrowingOverlay.setVisibility(View.VISIBLE);
            layoutBorrowingBorder.setVisibility(View.GONE);
        }

        if (borrowingOther) {
            layoutOtherOverlay.setVisibility(View.GONE);
            layoutOtherBorder.setVisibility(View.VISIBLE);
        } else {
            layoutOtherOverlay.setVisibility(View.VISIBLE);
            layoutOtherBorder.setVisibility(View.GONE);
        }
    }

    private void searchBook() {
        if (currentInput == null) return;
        isRequesting = true;
        parrentFragment.requestBookRequest(currentInput);
        if (currentInput.getPage() == 1) {
            txtMessage.setVisibility(View.GONE);
            showLoading();
            listBookRequest.clear();
        }
    }
}
