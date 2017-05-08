package com.gat.feature.suggestion.search.viewholder;

import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.suggestion.search.SuggestSearchActivity;
import com.gat.feature.suggestion.search.item.SearchHistoryItem;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/15/2017.
 */

public class SearchHistoryViewHolder extends ItemViewHolder<SearchHistoryItem> {

    @BindView(R.id.image_view_icon)
    ImageView imageViewIcon;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    private int mPageType = 0;

    public SearchHistoryViewHolder(ViewGroup parent, @LayoutRes int layoutId, int page_type) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
        mPageType = page_type;
    }

    @Override
    public void onBindItem(SearchHistoryItem item) {
        super.onBindItem(item);

        textViewName.setText(item.keyword().getKeyword());
        switch (mPageType) {
            case SuggestSearchActivity.TAB_POS.TAB_BOOK:
                imageViewIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search_history_book));
                break;
            case SuggestSearchActivity.TAB_POS.TAB_AUTHOR:
                imageViewIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search_history_author));
                break;
            case SuggestSearchActivity.TAB_POS.TAB_USER:
                imageViewIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search_history_user));
                break;
        }
    }
}
