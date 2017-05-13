package com.gat.feature.suggestion.nearby_user.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.ClientUtils;
import com.gat.repository.entity.UserNearByDistance;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 5/13/2017.
 */

public class UserNearByDistanceViewHolder extends ItemViewHolder<UserNearByDistanceItem> {

    @BindView(R.id.iv_user_avatar)
    ImageView imageViewUserAvatar;

    @BindView(R.id.tv_distance)
    TextView textViewDistance;

    @BindView(R.id.tv_full_name)
    TextView textViewFullName;

    @BindView(R.id.tv_lend_count)
    TextView textViewSharingCount;

    @BindView(R.id.tv_reading_count)
    TextView textViewReadingCount;

    @BindView(R.id.view_divider)
    View viewDivider;

    public UserNearByDistanceViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(UserNearByDistanceItem item) {
        super.onBindItem(item);

        UserNearByDistance user = item.user();
        if ( null != user.getImageId() && ! user.getImageId().isEmpty()) {
            ClientUtils.setImage(imageViewUserAvatar, R.drawable.default_user_icon, ClientUtils.getUrlImage(user.getImageId(), ClientUtils.SIZE_SMALL));
        }

        textViewDistance.setText(String.valueOf(user.getDistance()));
        textViewFullName.setText(user.getName());
        textViewSharingCount.setText(String.valueOf(user.getSharingCount()));
        textViewReadingCount.setText(String.valueOf(user.getReadCount()));
    }
}
