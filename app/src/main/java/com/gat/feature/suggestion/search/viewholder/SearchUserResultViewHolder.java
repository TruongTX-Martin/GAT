package com.gat.feature.suggestion.search.viewholder;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.feature.suggestion.search.item.SearchUserResultItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/15/2017.
 */

public class SearchUserResultViewHolder extends ItemViewHolder<SearchUserResultItem> {

    @BindView(R.id.image_view_user)
    ImageView imageViewUser;

    @BindView(R.id.text_view_user_name)
    TextView textViewUserName;

    @BindView(R.id.text_view_address)
    TextView textViewAddress;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public SearchUserResultViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(SearchUserResultItem item) {
        super.onBindItem(item);

        textViewUserName.setText(item.userResponse().getName());
        textViewAddress.setText(item.userResponse().getAddress());

        if ( ! TextUtils.isEmpty(item.userResponse().getImageId())) {
            ClientUtils.setImage(mContext, imageViewUser, R.drawable.default_user_icon, ClientUtils.getUrlImage(item.userResponse().getImageId(), ClientUtils.SIZE_SMALL));
        }

    }
}
