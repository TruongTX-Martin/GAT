package com.gat.feature.message.viewholder;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.Strings;
import com.gat.feature.message.item.GroupItem;
import com.gat.repository.entity.Group;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GroupMessageViewHolder extends ItemViewHolder<GroupItem> {
    private Group group;
    @BindView(R.id.message_from)
    TextView name;

    @BindView(R.id.message_text)
    TextView text;

    @BindView(R.id.message_time)
    TextView time;

    @BindView(R.id.user_image)
    CircleImageView userImage;

    public GroupMessageViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(GroupItem item) {
        super.onBindItem(item);
        group = item.group();
        //Map<String, Group.Member> members = group.getMembers();
        //Group.Member member = members.get("");
        name.setText("TODO");
        text.setText(group.lastMessage());
        time.setText("TODO"/*group.timeStamp()*/);
        userImage.setImageResource(R.drawable.steve_job);
    }

    public String getGroupId() {
        if (group != null) {
            return group.groupId();
        } else {
            return Strings.EMPTY;
        }
    }
}
