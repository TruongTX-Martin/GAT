package com.gat.feature.personaluser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.feature.personal.fragment.FragmentReadingBook;
import com.gat.feature.personaluser.fragment.FragmentBookUserReading;
import com.gat.repository.entity.book.BookReadingEntity;

import java.util.List;

/**
 * Created by root on 07/04/2017.
 */

public class BookUserReadingAdapter extends RecyclerView.Adapter<BookUserReadingAdapter.BookReadingViewHolder> {

    private Context context;
    private List<BookReadingEntity> listBookReading;
    private LayoutInflater inflate;
    private FragmentBookUserReading fragment;
    public BookUserReadingAdapter(Context context, List<BookReadingEntity> list, FragmentBookUserReading readingBook) {
        this.context = context;
        this.listBookReading = list;
        this.fragment = readingBook;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookReadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_book_userreading,null);
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
                holder.ratingBar.setNumStars((int)entity.getRateAvg());
                holder.txtRating.setText(entity.getRateAvg() + "");
                if(!Strings.isNullOrEmpty(entity.getBorrowFromUserName())) {
                    holder.layoutBorrowFrom.setVisibility(View.VISIBLE);
                    holder.txtBorrowName.setText(entity.getBorrowFromUserName());
                }else{
                    holder.layoutBorrowFrom.setVisibility(View.GONE);
                }
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
        TextView txtName,txtAuthor,txtRating,txtBorrowFrom,txtBorrowName;
        ImageView imgAvatar;
        RatingBar ratingBar;
        LinearLayout layoutBorrowFrom;
        public BookReadingViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            txtRating = (TextView) itemView.findViewById(R.id.txtRating);
            txtBorrowFrom = (TextView) itemView.findViewById(R.id.txtBorrowFrom);
            txtBorrowName = (TextView) itemView.findViewById(R.id.txtBorrowName);
            layoutBorrowFrom = (LinearLayout) itemView.findViewById(R.id.layoutBorrowFrom);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
