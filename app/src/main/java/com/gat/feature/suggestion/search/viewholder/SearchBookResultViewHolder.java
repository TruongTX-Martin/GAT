package com.gat.feature.suggestion.search.viewholder;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
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
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.ClientUtils;
import com.gat.feature.suggestion.search.item.SearchBookResultItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/15/2017.
 */

public class SearchBookResultViewHolder extends ItemViewHolder<SearchBookResultItem> {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.image_view_book_cover)
    ImageView imageViewBookCover;

    @BindView(R.id.text_view_book_name)
    TextView textViewBookName;

    @BindView(R.id.text_view_book_author)
    TextView textViewBookAuthor;

    @BindView(R.id.rating_bar_book)
    RatingBar ratingBarBook;

    @BindView(R.id.text_view_rating_average)
    TextView textViewRatingAverage;

    @BindView(R.id.text_view_count_comment)
    TextView textViewCountComment;

    public SearchBookResultViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(SearchBookResultItem item) {
        super.onBindItem(item);

        textViewBookName.setText(item.bookResponse().getTitle());
        textViewBookAuthor.setText(item.bookResponse().getAuthor());
        ratingBarBook.setRating(item.bookResponse().getRateAvg());
        textViewRatingAverage.setText(String.valueOf(item.bookResponse().getRateAvg()));
        textViewCountComment.setText(String.valueOf(item.bookResponse().getReviewCount()));

        if ( ! TextUtils.isEmpty(item.bookResponse().getImageId())) {
            ClientUtils.setImage(imageViewBookCover, R.drawable.default_book_cover,
                    ClientUtils.getUrlImage(item.bookResponse().getImageId(), ClientUtils.SIZE_SMALL));
        }

    }
}
