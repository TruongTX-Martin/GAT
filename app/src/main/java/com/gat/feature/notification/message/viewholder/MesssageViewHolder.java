package com.gat.feature.notification.message.viewholder;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.notification.message.item.MessageItem;
import com.gat.repository.entity.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ducbtsn on 3/30/17.
 */

public class MesssageViewHolder extends ItemViewHolder<MessageItem> {
    @BindView(R.id.message_from)
    TextView name;

    @BindView(R.id.message_text)
    TextView text;

    @BindView(R.id.message_time)
    TextView time;

    @BindView(R.id.user_image)
    CircleImageView userImage;

    public MesssageViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(MessageItem item) {
        super.onBindItem(item);
        Message message = item.message();
        name.setText(message.name());
        text.setText(message.message());
        time.setText(message.time());
    }
}
