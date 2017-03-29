package com.gat.feature.personal.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gat.R;
import com.gat.feature.personal.adapter.LoanBookAdapter;
import com.gat.feature.personal.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class FragmentLoanBook extends Fragment {

    private ListView lvBookLoan;
    private LoanBookAdapter adapterLoanBook;
    private List<BookEntity> listBook = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        lvBookLoan = (ListView) rootView.findViewById(R.id.lvLoanBook);
        prepareData();
        adapterLoanBook = new LoanBookAdapter(listBook,getActivity().getApplicationContext());
        lvBookLoan.setAdapter(adapterLoanBook);
        return rootView;
    }
    private void prepareData(){
        for(int i=0; i< 10; i++) {
            BookEntity entity = new BookEntity("Name" + i,"Author" +i);
            listBook.add(entity);
        }
    }
}
