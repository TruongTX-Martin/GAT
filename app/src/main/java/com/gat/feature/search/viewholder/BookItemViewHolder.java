package com.gat.feature.search.viewholder;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.search.item.BookItem;
import com.gat.repository.entity.Book;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rey on 2/15/2017.
 */

public class BookItemViewHolder extends ItemViewHolder<BookItem> {

    @BindView(R.id.book_tv_title)
    TextView titleView;

    @BindView(R.id.book_tv_author)
    TextView authorView;

    @BindView(R.id.book_tv_rating)
    TextView ratingView;

    @BindView(R.id.book_iv_cover)
    ImageView coverView;

    public BookItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(BookItem item) {
        super.onBindItem(item);
        Book book = item.book();
        titleView.setText(book.title());
        authorView.setText(item.authorNames());
        ratingView.setText(item.ratingText());
        coverView.setImageResource(R.mipmap.ic_launcher);
    }
}
