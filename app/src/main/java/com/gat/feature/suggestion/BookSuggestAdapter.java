package com.gat.feature.suggestion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.gat.R;
import com.gat.repository.entity.Book;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 3/26/2017.
 */

public class BookSuggestAdapter extends RecyclerView.Adapter<BookSuggestAdapter.BookSuggestHolder> {

    private List<Book> mListBooks;
    private Context mContext;

    public BookSuggestAdapter(Context context, List<Book> listBooks) {
        this.mContext = context;
        this.mListBooks = listBooks;
    }

    @Override
    public BookSuggestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_suggest, parent, false);
        final BookSuggestHolder mViewHolder = new BookSuggestHolder(itemView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(BookSuggestHolder holder, int position) {
        Book item = getItem(position);

        holder.textViewBookName.setText(item.title());
        holder.ratingBar.setRating(item.rating());

        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return mListBooks.isEmpty() ? 0 : mListBooks.size();
    }

    public Book getItem(int position) {
        return mListBooks.get(position);
    }

    //inner class ViewHolder
    public class BookSuggestHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_book_name)
        TextView textViewBookName;
        @BindView(R.id.rating_bar_book)
        RatingBar ratingBar;
        @BindView(R.id.image_view_book_cover)
        ImageView ivBooksCoverList;

        public BookSuggestHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
