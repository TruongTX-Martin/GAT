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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gat.R;
import com.gat.data.response.BookResponse;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 3/26/2017.
 */

public class BookSuggestAdapter extends RecyclerView.Adapter<BookSuggestAdapter.BookSuggestHolder> {

    private List<BookResponse> mListBookSuggest;
    private Context mContext;

    public BookSuggestAdapter(Context context, List<BookResponse> listBookSuggest) {
        this.mContext = context;
        this.mListBookSuggest = listBookSuggest;
    }

    @Override
    public BookSuggestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_suggest, parent, false);
        final BookSuggestHolder mViewHolder = new BookSuggestHolder(itemView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(BookSuggestHolder holder, int position) {
        BookResponse item = getItem(position);

        holder.textViewBookName.setText(item.getTitle());
        holder.ratingBar.setRating(item.getRateAvg());
        Glide.with(mContext).
                load("http://gatbook-api-v1.azurewebsites.net/api/common/get_image/"
                        + item.getImageId() + "?size=t").listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.ivBooksCoverList);

        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public int getItemCount() {
        if (null == this.mListBookSuggest)
            return 0;
        return this.mListBookSuggest.size();
    }

    public BookResponse getItem(int position) {
        return mListBookSuggest.get(position);
    }

    //inner class ViewHolder
    protected class BookSuggestHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_book_name)
        TextView textViewBookName;
        @BindView(R.id.rating_bar_book)
        RatingBar ratingBar;
        @BindView(R.id.image_view_book_cover)
        ImageView ivBooksCoverList;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        public BookSuggestHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
