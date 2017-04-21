package com.gat.feature.book_detail.list_user_sharing_book;

import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;

/**
 * Created by mozaa on 21/04/2017.
 */

public class UserSharingItemAdapter extends ItemAdapter {

    public UserSharingItemAdapter () {
        setReady();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = new UserSharingItemViewHolder(parent, R.layout.item_user_sharing_book);


        return viewHolder;
    }



}
