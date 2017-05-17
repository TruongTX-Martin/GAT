package com.gat.feature.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.customview.MZRatingBar;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.main.MainActivity;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.personaluser.PersonalUserScreen;
import com.gat.repository.entity.book.BookSharingEntity;
import com.gat.feature.personal.fragment.FragmentBookSharing;
import com.gat.feature.personal.fragment.OnSwipeTouchListener;

import java.util.List;

/**
 * Created by root on 16/04/2017.
 */

public class BookSharingAdapter extends RecyclerView.Adapter<BookSharingAdapter.BookSharingViewHolder> {

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
        View view = inflate.inflate(R.layout.layout_item_loanbook_plus, null);
        return new BookSharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookSharingViewHolder holder, int position) {
        BookSharingEntity entity = list.get(position);
        if (entity != null) {
            holder.layoutDelete.setVisibility(View.GONE);
            holder.layoutLeft.setVisibility(View.VISIBLE);
            if (entity.getSharingStatus() == 0) {
                //not sharing
                holder.imgExtend.setVisibility(View.GONE);
                holder.mySwitch.setVisibility(View.VISIBLE);
                holder.txtShared.setVisibility(View.VISIBLE);
                holder.mySwitch.setChecked(false);
            } else if (entity.getSharingStatus() == 1) {
                //sharing
                holder.imgExtend.setVisibility(View.GONE);
                holder.mySwitch.setVisibility(View.VISIBLE);
                holder.txtShared.setVisibility(View.VISIBLE);
                holder.mySwitch.setChecked(true);
            } else if (entity.getSharingStatus() == 2) {
                holder.imgExtend.setVisibility(View.VISIBLE);
                holder.mySwitch.setVisibility(View.GONE);
                holder.txtShared.setVisibility(View.GONE);
            } else {
                holder.imgExtend.setVisibility(View.GONE);
                holder.mySwitch.setVisibility(View.GONE);
                holder.txtShared.setVisibility(View.GONE);
            }
            if (!Strings.isNullOrEmpty(entity.getTitle())) {
                holder.txtTitle.setText(entity.getTitle());
            }
            if (!Strings.isNullOrEmpty(entity.getAuthor())) {
                holder.txtAuthor.setText(entity.getAuthor());
            }
            if (!Strings.isNullOrEmpty(entity.getBorrowingUserName())) {
                holder.layoutBorrowFrom.setVisibility(View.VISIBLE);
                holder.txtBorrowName.setText(Html.fromHtml(ClientUtils.formatColor("Người mượn ", "#000000") + ClientUtils.formatColor(entity.getBorrowingUserName(), "#5396b9")), TextView.BufferType.SPANNABLE);
            } else {
                holder.layoutBorrowFrom.setVisibility(View.GONE);
            }
            holder.ratingBar.setRating((float) entity.getRateAvg());
            holder.txtRating.setText(entity.getRateAvg() + "");
            if (!Strings.isNullOrEmpty(entity.getImageId())) {
                ClientUtils.setImage(context, holder.imgBook, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getImageId(), Constance.IMAGE_SIZE_SMALL));
            }
            if (entity.getSharingStatus() == 2 || entity.getSharingStatus() == 1) {
                if (entity.isHeader()) {
                    holder.layoutTitle.setVisibility(View.VISIBLE);
                    holder.txtTopTitle.setVisibility(View.VISIBLE);
                    holder.txtTopNumber.setVisibility(View.VISIBLE);
                    holder.txtTopTitle.setText("Sách cho mượn");
                    holder.txtTopNumber.setText("(" + fragmentBookSharing.getNumberSharing() + ")");
                } else {
                    holder.layoutTitle.setVisibility(View.GONE);
                }

            } else if (entity.getSharingStatus() == 0) {
                if (entity.isHeader()) {
                    holder.layoutTitle.setVisibility(View.VISIBLE);
                    holder.layoutTitle.setVisibility(View.VISIBLE);
                    holder.txtTopNumber.setVisibility(View.VISIBLE);
                    holder.txtTopTitle.setText("Sách không cho mượn");
                    holder.txtTopNumber.setText("(" + fragmentBookSharing.getNumberNotSharing() + ")");
                } else {
                    holder.layoutTitle.setVisibility(View.GONE);
                }
            } else if (entity.getSharingStatus() == 3) {
                if (entity.isHeader()) {
                    holder.layoutTitle.setVisibility(View.VISIBLE);
                    holder.txtTopTitle.setVisibility(View.VISIBLE);
                    holder.txtTopNumber.setVisibility(View.VISIBLE);
                    holder.txtTopTitle.setText("Sách thất lạc");
                    holder.txtTopNumber.setText("(" + fragmentBookSharing.getNumberLost() + ")");
                } else {
                    holder.layoutTitle.setVisibility(View.GONE);
                }
            }

            holder.txtBorrowName.setOnClickListener(v -> MainActivity.start(context, PersonalUserActivity.class, PersonalUserScreen.instance(entity.getBorrowingUserId())));

        }
        holder.mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fragmentBookSharing.changeStatusBook(entity, position);
        });
        holder.imgBook.setOnClickListener(v -> {
            int borrowingRecordId = entity.getBorrowingRecordId();
            int sharingStatus = entity.getSharingStatus();
            if (sharingStatus == 1) {
                //sharing

            } else if (sharingStatus == 2) {
                //borrowing
                Intent intent = new Intent(MainActivity.instance, BookDetailSenderActivity.class);
                intent.putExtra("BorrowingRecordId", borrowingRecordId);
                MainActivity.instance.startActivity(intent);
            }
        });
        if (getItemCount() > 9 && position == (getItemCount() - 1)) {
            fragmentBookSharing.loadMore();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BookSharingViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAuthor, txtBorrowFrom, txtShared;
        ImageView imgBook, imgExtend;
        MZRatingBar ratingBar;
        Switch mySwitch;
        RelativeLayout viewParrent, layoutDelete,layoutLeft,rootView;
        LinearLayout layoutTitle;
        TextView txtTopTitle, txtTopNumber, txtBorrowName, txtRating;
        LinearLayout layoutBorrowFrom;

        public BookSharingViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.txtName);
            txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
            txtShared = (TextView) view.findViewById(R.id.txtShared);
            imgBook = (ImageView) view.findViewById(R.id.imgAvatar);
            imgExtend = (ImageView) view.findViewById(R.id.imgExtend);
            ratingBar = (MZRatingBar) view.findViewById(R.id.ratingBar);
            mySwitch = (Switch) view.findViewById(R.id.mySwitch);
            viewParrent = (RelativeLayout) view.findViewById(R.id.rootView);
            layoutDelete = (RelativeLayout) view.findViewById(R.id.layoutDelete);
            layoutLeft = (RelativeLayout) view.findViewById(R.id.layoutLeft);
            rootView = (RelativeLayout) view.findViewById(R.id.rootView);
            layoutTitle = (LinearLayout) view.findViewById(R.id.layoutTitle);
            txtTopTitle = (TextView) view.findViewById(R.id.txtTopTitle);
            txtTopNumber = (TextView) view.findViewById(R.id.txtTopNumber);
            txtBorrowFrom = (TextView) view.findViewById(R.id.txtBorrowFrom);
            txtBorrowName = (TextView) view.findViewById(R.id.txtBorrowName);
            txtRating = (TextView) view.findViewById(R.id.txtRating);
            layoutBorrowFrom = (LinearLayout) view.findViewById(R.id.layoutBorrowFrom);
        }
    }
}
