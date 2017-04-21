package com.gat.feature.book_detail.list_user_sharing_book;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mozaa on 21/04/2017.
 */

public class UserSharingItemViewHolder extends ItemViewHolder<UserSharingItem> {

    @BindView(R.id.image_view_avatar)
    ImageView imageViewAvatar;

    @BindView(R.id.button_book_state)
    Button buttonBookState;

    @BindView(R.id.text_view_wait_for_accept)
    TextView textViewWaitForAccept;

    @BindView(R.id.text_view_user_name)
    TextView textViewUserName;

    @BindView(R.id.text_view_user_address)
    TextView textViewUserAddress;

    @BindView(R.id.text_view_book_in_sharing)
    TextView textViewBookInSharing;


    public UserSharingItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(parent, itemView);
    }

    @Override
    public void onBindItem(UserSharingItem item) {
        super.onBindItem(item);

        textViewUserName.setText(item.user().getName());
        textViewUserAddress.setText(item.user().getAddress());

    }
}
