package com.gat.feature.suggestion.viewholder;

import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;

/**
 * Created by mozaa on 30/03/2017.
 */

public class BookSuggestAdapter extends ItemAdapter {

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookItemViewHolder(parent, R.layout.item_book_suggest);
    }

}
