package com.gat.feature.book_detail.list_user_sharing_book;

import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.data.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mozaa on 21/04/2017.
 */

public class UserSharingItemAdapter extends ItemAdapter {

    private int userId;
    private OnButtonBorrowClickListener listener;

    public UserSharingItemAdapter (int userId, OnButtonBorrowClickListener listener) {
        setReady();
        this.userId = userId;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = new UserSharingItemViewHolder(parent, R.layout.item_user_sharing_book, userId, listener);

        return viewHolder;
    }


    @Override
    public boolean setItem(List<Item> items) {
        return super.setItem(items);
    }

    public void setItems (List<UserResponse> list) {
        List<Item> newList = new ArrayList<>();

        for (UserResponse user : list) {
            newList.add(UserSharingItem.instance(user));
        }
        setItem(newList);
    }


}
