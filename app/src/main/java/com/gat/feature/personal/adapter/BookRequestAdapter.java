package com.gat.feature.personal.adapter;

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
import com.gat.repository.entity.book.BookRequestEntity;
import com.gat.feature.personal.fragment.FragmentBookRequest;

import java.util.List;

/**
 * Created by root on 07/04/2017.
 */

public class BookRequestAdapter extends RecyclerView.Adapter<BookRequestAdapter.BookRequestViewHolder> {

    private Context context;
    private List<BookRequestEntity> listBookRequest;
    private LayoutInflater inflate;
    private FragmentBookRequest fragment;
    public BookRequestAdapter(Context context, List<BookRequestEntity> list, FragmentBookRequest readingBook) {
        this.context = context;
        this.listBookRequest = list;
        this.fragment = readingBook;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_book_request,null);
        return new BookRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookRequestViewHolder holder, int position) {
            BookRequestEntity entity = listBookRequest.get(position);
            if (entity != null) {
                if (!Strings.isNullOrEmpty(entity.getEditionTitle())) {
                    holder.txtName.setText(entity.getEditionTitle());
                }
                if(!Strings.isNullOrEmpty(entity.getBorrowerName())){
                    holder.txtAuthor.setText(entity.getBorrowerName());
                }
                if(!Strings.isNullOrEmpty(entity.getBorrowerImageId())) {
                    ClientUtils.setImage(holder.imgAvatar, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getBorrowerImageId(), Constance.IMAGE_SIZE_SMALL));
                }
                holder.ratingBar.setNumStars(3);
                if (entity.getRecordType() == 1) {
                    //yeu cau toi ban
                    if(entity.isHeader()){
                        holder.layoutTitle.setVisibility(View.VISIBLE);
                        holder.txtTopTitle.setText("Yêu cầu tới bạn");
                        holder.txtTopNumber.setText("("+fragment.getNumberRequestToYou()+")");
                    }else{
                        holder.layoutTitle.setVisibility(View.GONE);
                    }

                }else {
                    //yeu cau tu ban
                    if(entity.isHeader()){
                        holder.layoutTitle.setVisibility(View.VISIBLE);
                        holder.txtTopTitle.setText("Yêu cầu từ bạn");
                        holder.txtTopNumber.setText("("+fragment.getNumberRequestFromYou()+")");
                    }else{
                        holder.layoutTitle.setVisibility(View.GONE);
                    }
                }
            }
            if(getItemCount() > 9 && position == (getItemCount() -1)){
                fragment.loadMore();
            }
    }

    @Override
    public int getItemCount() {
        return listBookRequest.size();
    }


    public class  BookRequestViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtAuthor;
        ImageView imgAvatar;
        RatingBar ratingBar;
        LinearLayout layoutTitle;
        TextView txtTopTitle,txtTopNumber;
        public BookRequestViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            layoutTitle = (LinearLayout) itemView.findViewById(R.id.layoutTitle);
            txtTopTitle = (TextView) itemView.findViewById(R.id.txtTopTitle);
            txtTopNumber = (TextView) itemView.findViewById(R.id.txtTopNumber);
        }
    }
}
