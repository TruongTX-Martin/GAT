package com.gat.feature.personal.fragment;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.listener.RecyclerItemClickListener;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.DataLocal;
import com.gat.feature.main.MainActivity;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.personal.adapter.BookSharingAdapter;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.repository.entity.book.BookSharingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookSharing extends Fragment {

    private ProgressBar progressBar, progressLoadMore;
    private ImageView imgFilter;
    private TextView txtMessage;
    private List<BookSharingEntity> listBook = new ArrayList<>();
    private PersonalFragment parrentActivity;
    private Context context;
    private View rootView;
    private BookInstanceInput currentInput;
    private boolean isSharing, isNotSharing, isLost;
    //for loadmore listview
    private boolean isRequesting;
    private boolean isContinueMore = true;
    private RelativeLayout layoutBottom;

    private RecyclerView recyclerView;
    private BookSharingAdapter adapterBookSharing;
    private int numberSharing, numberNotSharing, numberLost;

    private boolean setTitleSharing, setTitleNotSharing, setTitleLost;

    public void setParrentActivity(PersonalFragment parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    public void setCurrentInput(BookInstanceInput currentInput) {
        this.currentInput = currentInput;
    }

    public void refreshDate(){
//        currentInput.setPage(1);
//        searchBook(currentInput);
    }

    public BookInstanceInput getCurrentInput() {
        return currentInput;
    }

    public void setListBook(List<BookSharingEntity> listBook) {
        try {
            if (listBook != null && listBook.size() > 0) {
                this.listBook.addAll(listBook);
            }
            for (int i = 0; i < this.listBook.size(); i++) {
                this.listBook.get(i).setHeader(false);
            }
            setTitleSharing = false;
            setTitleNotSharing = false;
            setTitleLost = false;
            for (int i = 0; i < this.listBook.size(); i++) {
                if (this.listBook.get(i).getSharingStatus() == 2 || this.listBook.get(i).getSharingStatus() == 1) {
                    if (!setTitleSharing) {
                        this.listBook.get(i).setHeader(true);
                        setTitleSharing = true;
                    }
                } else if (this.listBook.get(i).getSharingStatus() == 0) {
                    if (!setTitleNotSharing) {
                        this.listBook.get(i).setHeader(true);
                        setTitleNotSharing = true;
                    }
                } else if (this.listBook.get(i).getSharingStatus() == 3) {
                    if (!setTitleLost) {
                        this.listBook.get(i).setHeader(true);
                        setTitleLost = true;
                    }
                }
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
            adapterBookSharing.notifyDataSetChanged();
//            adapterBookSharing = new BookSharingAdapter(this.listBook, getActivity().getApplicationContext(), this);
//            recyclerView.setAdapter(adapterBookSharing);

        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        context = getActivity().getApplicationContext();
        initView();
        handleEvent();
        showLoading();
        if (DataLocal.getPersonalInputSharing() != null ) {
            currentInput = DataLocal.getPersonalInputSharing();
            searchBook(currentInput);
        }else if(currentInput != null) {
            searchBook(currentInput);
        }
        return rootView;
    }

    private void initView() {
        adapterBookSharing = new BookSharingAdapter(listBook, getActivity().getApplicationContext(), this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerRequest);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressLoadMore = (ProgressBar) rootView.findViewById(R.id.progressLoadMore);
        imgFilter = (ImageView) rootView.findViewById(R.id.imgFilter);
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        layoutBottom = (RelativeLayout) rootView.findViewById(R.id.layoutBottom);
        recyclerView.setAdapter(adapterBookSharing);
    }


    private void handleEvent() {
        layoutBottom.setOnClickListener(v -> {
            showDialogFilter();
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
                BookSharingEntity entity = listBook.get(position);
                if(entity.getBorrowingUserId() == 0) {
                    showDialogDeleteBook(entity);
                }
            }
        }));
    }

    private BookSharingEntity currentDelete;
    private void showDialogDeleteBook(BookSharingEntity entity) {
        Dialog dialog = new Dialog(MainActivity.instance);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        dialog.setCanceledOnTouchOutside(false);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTopTitle);
        TextView txtContent = (TextView) dialog.findViewById(R.id.txtContent);
        txtTitle.setText("Xóa sách");
        txtContent.setText("Bạn muốn xoá sách "+ entity.getTitle() + " khỏi tủ sách của mình??");
        Button btnCancle = (Button) dialog.findViewById(R.id.btnCancle);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnCancle.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btnOk.setOnClickListener(v -> {
            parrentActivity.removeBookInstance(entity.getInstanceId());
            this.currentDelete = entity;
            dialog.dismiss();
        });
        if(dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }

    public void updateAfterDelete(){
        listBook.remove(currentDelete);
        adapterBookSharing.notifyDataSetChanged();
    }
    public void loadMore() {
        if (isRequesting == false && isContinueMore) {
            showLoadMore();
            currentInput.setPage(currentInput.getPage() + 1);
            searchBook(currentInput);
        }
    }

    private void showLoading() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showLoadMore() {
        progressLoadMore.setVisibility(View.VISIBLE);
    }

    private void hideLoadMore() {
        progressLoadMore.setVisibility(View.GONE);
    }

    private void fakeData() {
        //fake data listbook sharing
        for (int i = 0; i < 10; i++) {
            BookSharingEntity info = new BookSharingEntity();
            info.setTitle("Cuộc đời, sự nghiệp Steve Jobs" + i);
            info.setAuthor("Frank Luca" + i);
            info.setRateCount(4);
            info.setBorrowingUserName("Trần Duy Hưng " + i);
            listBook.add(info);
        }

    }

    private void showDialogFilter() {
        if (currentInput == null) return;
        isSharing = currentInput.isSharingFilter();
        isNotSharing = currentInput.isNotSharingFilter();
        isLost = currentInput.isLostFilter();
        LayoutInflater inflater = LayoutInflater.from(MainActivity.instance);
        View customView = inflater.inflate(R.layout.layout_popup_book_filter, null);
        PopupWindow popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        RelativeLayout imgClose = (RelativeLayout) customView.findViewById(R.id.layoutClose);
        RelativeLayout layoutSharingBorder = (RelativeLayout) customView.findViewById(R.id.layoutSharingBorder);
        RelativeLayout layoutSharingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutSharingOverlay);
        RelativeLayout layoutNotSharingBorder = (RelativeLayout) customView.findViewById(R.id.layoutMyBookBorder);
        RelativeLayout layoutNotSharingOverlay = (RelativeLayout) customView.findViewById(R.id.layoutMyBookOverLay);
        RelativeLayout layoutLostBorder = (RelativeLayout) customView.findViewById(R.id.layoutBookLostBorder);
        RelativeLayout layoutLostOverlay = (RelativeLayout) customView.findViewById(R.id.layoutBookLostOverlay);
        RelativeLayout layoutRoot = (RelativeLayout) customView.findViewById(R.id.layoutParrent);
        imgClose.setOnClickListener(v -> {
            if (currentInput.isSharingFilter() == isSharing && currentInput.isNotSharingFilter() == isNotSharing
                    && currentInput.isLostFilter() == isLost) {
                //do nothing
            } else {
                currentInput.setSharingFilter(isSharing);
                currentInput.setNotSharingFilter(isNotSharing);
                currentInput.setLostFilter(isLost);
                currentInput.setPage(1);
                isContinueMore = true;
                numberNotSharing = 0;
                numberLost = 0;
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
        if (currentInput.isSharingFilter()) {
            layoutSharingBorder.setVisibility(View.VISIBLE);
            layoutSharingOverlay.setVisibility(View.GONE);
        } else {
            layoutSharingBorder.setVisibility(View.GONE);
            layoutSharingOverlay.setVisibility(View.VISIBLE);
        }
        if (currentInput.isNotSharingFilter()) {
            layoutNotSharingBorder.setVisibility(View.VISIBLE);
            layoutNotSharingOverlay.setVisibility(View.GONE);
        } else {
            layoutNotSharingBorder.setVisibility(View.GONE);
            layoutNotSharingOverlay.setVisibility(View.VISIBLE);
        }
        if (currentInput.isLostFilter()) {
            layoutLostBorder.setVisibility(View.VISIBLE);
            layoutLostOverlay.setVisibility(View.GONE);
        } else {
            layoutLostBorder.setVisibility(View.GONE);
            layoutLostOverlay.setVisibility(View.VISIBLE);
        }
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }


    private void searchBook(BookInstanceInput input) {
        if (input == null) return;
        DataLocal.savePersonalInputSharing(input);
        try {
            isRequesting = true;
            parrentActivity.requestBookInstance(input);
            if (input.getPage() == 1) {
                txtMessage.setVisibility(View.GONE);
                showLoading();
                listBook.clear();
            }
//            else{
//                setTitleSharing = false;setTitleNotSharing = false;setTitleLost = false;
//            }
        } catch (Exception e) {
        }
    }

    public void changeStatusBook(BookSharingEntity entity, int position) {
        try {
            if (entity.getSharingStatus() == 0) {
                entity.setSharingStatus(1);
            } else {
                entity.setSharingStatus(0);
            }
            BookChangeStatusInput input = new BookChangeStatusInput(entity.getInstanceId(), entity.getSharingStatus());
            parrentActivity.requestChangeStatusBook(input);
            listBook.remove(position);
            listBook.add(position, entity);
        } catch (Exception e) {
        }
    }

    public int getNumberSharing() {
        return numberSharing;
    }

    public int getNumberNotSharing() {
        return numberNotSharing;
    }

    public int getNumberLost() {
        return numberLost;
    }

    public void setNumberSharing(int numberSharing) {
        this.numberSharing = numberSharing;
    }

    public void setNumberNotSharing(int numberNotSharing) {
        this.numberNotSharing = numberNotSharing;
    }

    public void setNumberLost(int numberLost) {
        this.numberLost = numberLost;
    }
}
