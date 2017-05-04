package com.gat.feature.personaluser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.feature.personaluser.fragment.FragmentBookUserSharing;
import com.gat.repository.entity.book.BookSharingEntity;

import java.util.List;

/**
 * Created by root on 16/04/2017.
 */

public class BookUserSharingAdapter extends RecyclerView.Adapter<BookUserSharingAdapter.BookSharingViewHolder> {

    private List<BookSharingEntity> list;
    private Context context;
    private FragmentBookUserSharing fragmentBookSharing;
    private LayoutInflater inflate;
    private IRecyclerViewItemClickListener itemClickListener;

    public BookUserSharingAdapter(List<BookSharingEntity> list, Context context, FragmentBookUserSharing bookSharing) {
        this.list = list;
        this.context = context;
        this.fragmentBookSharing = bookSharing;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookSharingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_book_usersharing, null);
        return new BookSharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookSharingViewHolder holder, int position) {
        BookSharingEntity entity = list.get(position);
        if (entity != null) {
            if (!Strings.isNullOrEmpty(entity.getTitle())) {
                holder.txtTitle.setText(entity.getTitle());
            }
            if (!Strings.isNullOrEmpty(entity.getAuthor())) {
                holder.txtAuthor.setText(entity.getAuthor());
            }
            holder.ratingBar.setNumStars((int) entity.getRateAvg());
            holder.txtRating.setText(entity.getRateAvg() +"");
            if (!Strings.isNullOrEmpty(entity.getImageId())) {
                ClientUtils.setImage(holder.imgBook, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getImageId(), Constance.IMAGE_SIZE_SMALL));
            }
            if (entity.getAvailableStatus() == 1 && entity.getRequestingStatus() == 0) {
                holder.btnBorrow.setVisibility(View.VISIBLE);
                holder.btnWaitBorrow.setVisibility(View.GONE);
                holder.imgExtend.setVisibility(View.INVISIBLE);
            } else if (entity.getAvailableStatus() == 0 && entity.getRequestingStatus() == 0) {
                holder.btnWaitBorrow.setVisibility(View.VISIBLE);
                holder.btnBorrow.setVisibility(View.GONE);
                holder.imgExtend.setVisibility(View.INVISIBLE);
                holder.txtStatus.setVisibility(View.VISIBLE);
            } else if (entity.getAvailableStatus() == 1 && entity.getRequestingStatus() == 1) {
                holder.txtWaitAgreed.setVisibility(View.VISIBLE);
                holder.imgExtend.setVisibility(View.VISIBLE);
                holder.btnBorrow.setVisibility(View.GONE);
                holder.btnWaitBorrow.setVisibility(View.GONE);
            }
        }
        if (getItemCount() > 9 && position == (getItemCount() - 1)) {
            fragmentBookSharing.loadMore();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BookSharingViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAuthor, txtStatus, txtShared, txtWaitAgreed,txtRating;
        ImageView imgBook, imgExtend;
        RatingBar ratingBar;
        Button btnBorrow, btnWaitBorrow;

        public BookSharingViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.txtName);
            txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtShared = (TextView) view.findViewById(R.id.txtShared);
            imgBook = (ImageView) view.findViewById(R.id.imgAvatar);
            imgExtend = (ImageView) view.findViewById(R.id.imgExtend);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            txtWaitAgreed = (TextView) view.findViewById(R.id.txtWaitAgreed);
            txtRating = (TextView) view.findViewById(R.id.txtRating);
            btnBorrow = (Button) view.findViewById(R.id.btnBorrow);
            btnWaitBorrow = (Button) view.findViewById(R.id.btnWaitBorrow);
        }
    }
}
