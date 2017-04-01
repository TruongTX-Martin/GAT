package com.gat.feature.personal.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.feature.personal.adapter.LoanBookAdapter;
import com.gat.feature.personal.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentBookSharing extends Fragment {

    ListView lvBookSharing;

    ImageView imgFilter;
    private LoanBookAdapter adapterLoanBook;
    private List<BookEntity> listBook = new ArrayList<>();
    private Activity parrentActivity;
    private Context context;


    public void setParrentActivity(Activity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        context = getActivity().getApplicationContext();
        prepareData();
        adapterLoanBook = new LoanBookAdapter(listBook, getActivity().getApplicationContext());
        lvBookSharing = (ListView) rootView.findViewById(R.id.lvBookSharing);
        imgFilter = (ImageView) rootView.findViewById(R.id.imgFilter);
        lvBookSharing.setAdapter(adapterLoanBook);
        handleEvent();
        return rootView;
    }

    private void handleEvent() {
        imgFilter.setOnClickListener(v -> {
            showPopupFilter();
        });
    }

    private void showPopupFilter() {
        try {
            ProgressDialog progressDialog = ProgressDialog.show(parrentActivity, null, null, true, false);
            progressDialog.setContentView(R.layout.layout_popup_book_filter);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setGravity(Gravity.BOTTOM);
            progressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            TextView txtSharing = (TextView) progressDialog.findViewById(R.id.txtSharing);
            TextView txtNotSharing = (TextView) progressDialog.findViewById(R.id.txtNotSharing);
            TextView txtLost = (TextView) progressDialog.findViewById(R.id.txtLost);
            TextView txtCancle = (TextView) progressDialog.findViewById(R.id.txtCancle);
            txtSharing.setOnClickListener(v -> {
                ClientUtils.showToast("Sharing");
                progressDialog.hide();
            });
            txtNotSharing.setOnClickListener(v -> {
                ClientUtils.showToast("NotSharing");
                progressDialog.hide();
            });
            txtLost.setOnClickListener(v -> {
                ClientUtils.showToast("Lost");
                progressDialog.hide();
            });
            txtCancle.setOnClickListener(v -> {
                ClientUtils.showToast("Cancle");
                progressDialog.hide();
            });
            progressDialog.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void prepareData() {
        for (int i = 0; i < 10; i++) {
            BookEntity entity = new BookEntity("Name" + i, "Author" + i);
            listBook.add(entity);
        }
    }

    public void setUserData() {
        BookEntity entity = new BookEntity("Truong", "Thai Binh");
        listBook.add(entity);
        adapterLoanBook.notifyDataSetChanged();
    }
}
