package com.gat.feature.message.viewholder;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Constance;
import com.gat.feature.message.item.MessageItem;
import com.gat.repository.entity.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MessageViewHolder extends ItemViewHolder<MessageItem> {
    @BindView(R.id.message_text)
    TextView text;

    @BindView(R.id.messenger_image)
    CircleImageView userImage;

    @BindView(R.id.message_date)
    TextView textDate;

    @BindView(R.id.admin_line)
    View adminLine;

    private final boolean isRight;
    public MessageViewHolder(ViewGroup parent, @LayoutRes int layoutId, boolean isRight) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
        this.isRight = isRight;
    }

    @Override
    public void onBindItem(MessageItem item) {
        super.onBindItem(item);
        Message message = item.message();
        text.setText(message.message());
        if (isRight || !item.displayImage()) {
            userImage.setVisibility(View.INVISIBLE);
        } else {
            if (CommonCheck.isAdmin((int)message.userId())) {
                userImage.setImageResource(R.drawable.gat_app_icon);
            } else {
                String url = ClientUtils.getUrlImage(message.imageId(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(userImage, R.drawable.gat_app_icon, url);
            }
        }
        if (item.displayDate()) {
            textDate.setText(CommonCheck.getDate(item.message().timeStamp()));
        } else {
            textDate.setVisibility(View.GONE);
        }
        if (!item.displayBottomLine()) {
            adminLine.setVisibility(View.GONE);
        }
    }
}
