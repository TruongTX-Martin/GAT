package com.gat.feature.notification.adapter;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.notification.item.NotificationItem;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mozaa on 28/04/2017.
 */

public class NotificationItemViewHolder extends ItemViewHolder<NotificationItem> {

    @BindView(R.id.image_view_avatar)
    ImageView imageViewAvatar;

    @BindView(R.id.text_view_time_ago)
    TextView textViewTimeAgo;

    @BindView(R.id.text_view_user_name)
    TextView textViewUserName;

    @BindView(R.id.text_view_message)
    TextView textViewMessage;

    @BindView(R.id.text_view_message_detail)
    TextView textViewMessageDetail;

    public NotificationItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(NotificationItem item) {
        super.onBindItem(item);

        textViewTimeAgo.setText(item.notifyEntity().modifyTime());
        textViewUserName.setText(item.notifyEntity().sourceName());
        textViewMessage.setText(item.notifyEntity().targetName());
    }
}
