package com.gat.feature.personaluser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.feature.personaluser.PersonalUserActivity;

/**
 * Created by root on 20/04/2017.
 */

public class FragmentBookUserSharing extends Fragment {

    private View rootView;
    private PersonalUserActivity parrentActivity;


    public void setParrentActivity(PersonalUserActivity parrentActivity) {
        this.parrentActivity = parrentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.layout_fragment_book_loan, container, false);
        return rootView;
    }
}
