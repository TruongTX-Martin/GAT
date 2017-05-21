package com.gat.feature.suggestion.viewholder;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.feature.suggestion.item.BookItem;
import com.gat.repository.entity.Book;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mozaa on 30/03/2017.
 */

public class BookItemViewHolder  extends ItemViewHolder<BookItem> {

    @BindView(R.id.text_view_book_name)
    TextView textViewBookName;
    @BindView(R.id.rating_bar_book)
    RatingBar ratingBar;
    @BindView(R.id.image_view_book_cover)
    ImageView ivBooksCoverList;

    public BookItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(BookItem item) {
        super.onBindItem(item);
        Book book = item.book();

        textViewBookName.setText(book.title());
        ratingBar.setRating(book.rating());
        ivBooksCoverList.setImageResource(R.mipmap.ic_launcher);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

    }
}
