package com.gat.feature.message.viewholder;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
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

    public MessageViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(MessageItem item) {
        super.onBindItem(item);
        Message message = item.message();
        text.setText(message.getMessage());
        userImage.setImageResource(R.drawable.steve_job);
    }
}
