package com.gat.feature.notification.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.feature.notification.NotifyType;

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

        textViewTimeAgo.setText(item.notifyEntity().beginTime());
        showMessageContentByType(item.notifyEntity());
    }

    private void showMessageContentByType (NotifyEntity item) {
        itemView.setTag(item);
        textViewMessageDetail.setVisibility(View.GONE);
        switch (item.notificationType()) {
            case NotifyType.MESSAGE_UNREAD:
                textViewUserName.setText("GAT Message");
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.you_have_count_message_read), item.referId()));
                break;

            case NotifyType.MESSAGE_FROM:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(mContext.getResources().getString(R.string.message_to_you));
                textViewMessageDetail.setVisibility(View.VISIBLE);
                textViewMessageDetail.setText(item.targetName());
                break;

            case NotifyType.BORROW_REQUEST:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.someone_request_borrow), item.targetName()));

                break;

            case NotifyType.BORROW_UNLUCKY:
                textViewUserName.setVisibility(View.GONE);
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.borrow_unlucky), item.targetName()));

                break;

            case NotifyType.BORROW_ACCEPT:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.borrow_accept), item.targetName()));

                break;

            case NotifyType.BORROW_NEEDED:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_borrow_needed   ), item.targetName()));

                break;

            case NotifyType.BORROW_REFUSE:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_borrow_refuse   ), item.targetName()));

                break;

            case NotifyType.BORROW_LOST:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_lost_book), item.targetName()));

                break;

            case NotifyType.BORROW_YOUR_TOTAL:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_your_total), item.referId()));

                break;

            case NotifyType.BORROW_FROM_ANOTHER:
                textViewUserName.setText("GAT");
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_total_request), item.referId(), item.targetName()));
                break;

            case NotifyType.BORROW_CANCEL:
                textViewUserName.setText(item.sourceName());
                textViewMessage.setText(String.format(mContext.getResources().getString(R.string.notice_borrow_cancel), item.targetName()));
                break;
        }
    }
}
