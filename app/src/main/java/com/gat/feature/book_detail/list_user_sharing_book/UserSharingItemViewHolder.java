package com.gat.feature.book_detail.list_user_sharing_book;

import android.support.annotation.LayoutRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.data.response.UserResponse;

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

    @BindView(R.id.text_view_book_state)
    TextView textViewBookState;

    private int mUserId;
    private OnButtonBorrowClickListener buttonBorrowClickListener;

    public UserSharingItemViewHolder(ViewGroup parent, @LayoutRes int layoutId, int userId, OnButtonBorrowClickListener listener) {
        super(parent, layoutId);
        mUserId = userId;
        buttonBorrowClickListener = listener;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(UserSharingItem item) {
        super.onBindItem(item);

        itemView.setTag(0);
        executeTypeOfRow(item.user());
        executeRow();
        buttonBookState.setOnClickListener(v -> {
            buttonBorrowClickListener.onButtonBorrowClick(getAdapterPosition(), item.user());
        });
    }

    private void executeTypeOfRow(UserResponse user) {

        if (user.getUserId() == mUserId) {
            itemView.setTag(UserSharingType.IS_ME);
            return;
        }

        if (user.getAvailableStatus() == 1 && user.getRequestingStatus() == 0) {
            itemView.setTag(UserSharingType.CAN_BORROW);
            return;
        }

        if (user.getAvailableStatus() == 0 && user.getRequestingStatus() == 0) {
            itemView.setTag(UserSharingType.WAIT_FOR_BORROW);
            return;
        }

        if (user.getAvailableStatus() == 1 && user.getRequestingStatus() == 1) {
            switch (user.getRecordStatus()) {
                case 0:
                    itemView.setTag(UserSharingType.WAIT_FOR_ACCEPT);
                    return;

                case 2:
                    itemView.setTag(UserSharingType.WAIT_FOR_CONNECT);
                    return;

                case 3:
                    itemView.setTag(UserSharingType.IS_BORROWING);
                    return;
            }
        }

        if (user.getAvailableStatus() == 0 && user.getRequestingStatus() == 1
                && user.getRecordStatus() == 1) {
            itemView.setTag(UserSharingType.BORROW_IN_QUEUE);
        }
    }

    public void executeRow () {
        textViewUserName.setText(item.user().getName());
        textViewUserAddress.setText(item.user().getAddress());

        int typeOfRow = (int) itemView.getTag();
        switch (typeOfRow) {
            case UserSharingType.IS_ME:
                textViewWaitForAccept.setVisibility(View.GONE);
                buttonBookState.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.GONE);
                break;

            case UserSharingType.CAN_BORROW:
                textViewWaitForAccept.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.GONE);
                buttonBookState.setVisibility(View.VISIBLE);
                buttonBookState.setText(itemView.getContext().getResources().getString(R.string.borrow_book));
                buttonBookState.setBackground(ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.bg_rounded_cool_blue, null));
                break;

            case UserSharingType.WAIT_FOR_BORROW:
                textViewWaitForAccept.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.VISIBLE);
                textViewBookState.setText(itemView.getContext().getResources().getString(R.string.book_in_sharing));
                buttonBookState.setVisibility(View.VISIBLE);
                buttonBookState.setText(itemView.getContext().getResources().getString(R.string.wait_to_borrow));
                buttonBookState.setBackground(ResourcesCompat.getDrawable(itemView.getContext().getResources(), R.drawable.bg_rounded_green, null));
                break;

            case UserSharingType.WAIT_FOR_ACCEPT:
                buttonBookState.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.GONE);
                textViewWaitForAccept.setVisibility(View.VISIBLE);
                textViewWaitForAccept.setText(itemView.getContext().getResources().getString(R.string.wait_for_accept_borrow));
                break;

            case UserSharingType.WAIT_FOR_CONNECT:
                buttonBookState.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.GONE);
                textViewWaitForAccept.setVisibility(View.VISIBLE);
                textViewWaitForAccept.setText(itemView.getContext().getResources().getString(R.string.is_connecting));
                break;

            case UserSharingType.IS_BORROWING:
                buttonBookState.setVisibility(View.GONE);
                textViewBookState.setVisibility(View.GONE);
                textViewWaitForAccept.setVisibility(View.VISIBLE);
                textViewWaitForAccept.setText(itemView.getContext().getResources().getString(R.string.is_borrowing));
                break;

            case UserSharingType.BORROW_IN_QUEUE:
                textViewBookState.setVisibility(View.VISIBLE);
                textViewBookState.setText(itemView.getContext().getResources().getString(R.string.book_in_sharing));
                buttonBookState.setVisibility(View.GONE);
                textViewWaitForAccept.setVisibility(View.VISIBLE);
                textViewWaitForAccept.setText(itemView.getContext().getResources().getString(R.string.wait_your_turn));
                break;
        }
    }


}
