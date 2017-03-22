package com.gat.feature.search.viewholder;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.search.item.LoadingItem;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rey on 2/15/2017.
 */

public class LoadingItemViewHolder extends ItemViewHolder<LoadingItem>{

    @BindView(R.id.loading_pw)
    ProgressWheel progressWheel;

    @BindView(R.id.loading_fl_error)
    FrameLayout errorLayout;

    @BindView(R.id.loading_tv_msg)
    TextView messageView;

    public LoadingItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(LoadingItem item) {
        super.onBindItem(item);

        if(item.message() == LoadingItem.Message.LOADING){
            progressWheel.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
        else{
            progressWheel.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);

            switch (item.message()) {
                case LoadingItem.Message.DEFAULT:
                    messageView.setText("Please enter some keyword.");
                    break;
                case LoadingItem.Message.EMPTY:
                    messageView.setText("Not found any book.");
                    break;
                case LoadingItem.Message.ERROR:
                    messageView.setText("There is an error.\nPlease try again.");
                    break;
            }
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)itemView.getLayoutParams();
        params.height = item.fullHeight() ? RecyclerView.LayoutParams.MATCH_PARENT : RecyclerView.LayoutParams.WRAP_CONTENT;
        itemView.setLayoutParams(params);
    }
}
