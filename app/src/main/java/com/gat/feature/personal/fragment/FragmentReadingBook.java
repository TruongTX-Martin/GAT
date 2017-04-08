package com.gat.feature.personal.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gat.R;
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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerReading);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookReadingAdapter(context,listBookReading);
        recyclerView.setAdapter(adapter);
        return rootView;
    }
}
