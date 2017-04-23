package com.gat.feature.book_detail.adapter;

import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;

/**
 * Created by mryit on 4/20/2017.
 */

public class EvaluationItemAdapter extends ItemAdapter {

    public EvaluationItemAdapter() {
        setReady();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = null;

        viewHolder = new EvaluationItemViewHolder(parent, R.layout.item_comment_evaluation);

        return viewHolder;
    }

}
