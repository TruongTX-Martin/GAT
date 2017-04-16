package com.gat.feature.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.repository.entity.book.BookReadingEntity;
import com.gat.feature.personal.fragment.FragmentReadingBook;

import java.util.List;

/**
 * Created by root on 07/04/2017.
 */

public class BookReadingAdapter  extends RecyclerView.Adapter<BookReadingAdapter.BookReadingViewHolder> {

    private Context context;
    private List<BookReadingEntity> listBookReading;
    private LayoutInflater inflate;
    private FragmentReadingBook fragment;
    public BookReadingAdapter(Context context, List<BookReadingEntity> list, FragmentReadingBook readingBook) {
        this.context = context;
        this.listBookReading = list;
        this.fragment = readingBook;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookReadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_readingbook,null);
        return new BookReadingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookReadingViewHolder holder, int position) {
            BookReadingEntity entity = listBookReading.get(position);
            if (entity != null) {
                holder.txtName.setText(entity.getTitle());
                holder.txtAuthor.setText(entity.getAuthor());
                if(!Strings.isNullOrEmpty(entity.getEditionImageId())){
                    ClientUtils.setImage(holder.imgAvatar, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getEditionImageId(), Constance.IMAGE_SIZE_SMALL));
                }
                holder.ratingBar.setNumStars(entity.getRateAvg());
            }
            if(getItemCount() > 9 && position == (getItemCount() -1)){
                fragment.loadMore();
            }
    }

    @Override
    public int getItemCount() {
        return listBookReading.size();
    }


    public class  BookReadingViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtAuthor;
        ImageView imgAvatar;
        RatingBar ratingBar;
        public BookReadingViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
