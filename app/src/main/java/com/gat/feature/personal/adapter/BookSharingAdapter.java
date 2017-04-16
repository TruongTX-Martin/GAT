package com.gat.feature.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.repository.entity.book.BookSharingEntity;
import com.gat.feature.personal.fragment.FragmentBookSharing;
import com.gat.feature.personal.fragment.OnSwipeTouchListener;

import java.util.List;

/**
 * Created by root on 16/04/2017.
 */

public class BookSharingAdapter extends  RecyclerView.Adapter<BookSharingAdapter.BookSharingViewHolder>{

    private List<BookSharingEntity> list;
    private Context context;
    private FragmentBookSharing fragmentBookSharing;
    private LayoutInflater inflate;

    public BookSharingAdapter(List<BookSharingEntity> list, Context context, FragmentBookSharing bookSharing) {
        this.list = list;
        this.context = context;
        this.fragmentBookSharing = bookSharing;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookSharingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_loanbook,null);
        return new BookSharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookSharingViewHolder holder, int position) {
        BookSharingEntity entity = list.get(position);
        if (entity != null) {
            if (entity.getSharingStatus() == 2 || entity.getSharingStatus() == 1) {
                holder.imgExtend.setVisibility(View.VISIBLE);
                holder.mySwitch.setVisibility(View.GONE);
                holder.txtShared.setVisibility(View.GONE);
            } else if (entity.getSharingStatus() == 0) {
                holder.imgExtend.setVisibility(View.GONE);
                holder.mySwitch.setVisibility(View.VISIBLE);
                holder.txtShared.setVisibility(View.VISIBLE);
            } else {
                holder.imgExtend.setVisibility(View.GONE);
                holder.mySwitch.setVisibility(View.GONE);
                holder.txtShared.setVisibility(View.GONE);
            }
            if(!Strings.isNullOrEmpty(entity.getTitle())){
                holder.txtTitle.setText(entity.getTitle());
            }
            if(!Strings.isNullOrEmpty(entity.getAuthor())){
                holder.txtAuthor.setText(entity.getAuthor());
            }
            if(!Strings.isNullOrEmpty(entity.getBorrowingUserName())){
                holder.txtBorrowFrom.setText("Người mượn:"+entity.getBorrowingUserName());
            }
            holder.ratingBar.setNumStars(entity.getRateAvg());
            if (!Strings.isNullOrEmpty(entity.getImageId())) {
                ClientUtils.setImage(holder.imgBook, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getImageId(), Constance.IMAGE_SIZE_SMALL));
            }
        }
        holder.mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fragmentBookSharing.changeStatusBook(entity,position);
        });
        if(getItemCount() > 9 && position == (getItemCount() -1)){
            fragmentBookSharing.loadMore();
        }
        holder.viewParrent.setOnTouchListener(new OnSwipeTouchListener(context){
            public void onSwipeRight() {
                holder.layoutDelete.setVisibility(View.GONE);
            }

            public void onSwipeLeft() {
                holder.layoutDelete.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BookSharingViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtAuthor,txtBorrowFrom,txtShared;
        ImageView imgBook,imgExtend;
        RatingBar ratingBar;
        Switch mySwitch;
        RelativeLayout viewParrent,layoutDelete;
        public BookSharingViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.txtName);
            txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
            txtBorrowFrom = (TextView) view.findViewById(R.id.txtBorrowFrom);
            txtShared = (TextView) view.findViewById(R.id.txtShared);
            imgBook = (ImageView) view.findViewById(R.id.imgAvatar);
            imgExtend = (ImageView) view.findViewById(R.id.imgExtend);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            mySwitch = (Switch) view.findViewById(R.id.mySwitch);
            viewParrent = (RelativeLayout) view.findViewById(R.id.rootView);
            layoutDelete = (RelativeLayout) view.findViewById(R.id.layoutDelete);
        }
    }
}
