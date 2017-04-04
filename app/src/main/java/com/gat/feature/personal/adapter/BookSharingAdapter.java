package com.gat.feature.personal.adapter;

import android.content.Context;
import android.support.annotation.Dimension;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.feature.personal.entity.BookEntity;

import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class BookSharingAdapter extends BaseAdapter {

    private List<BookEntity> list;
    private Context context;

    public BookSharingAdapter(List<BookEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.layout_item_loanbook, null);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtAuthor = (TextView) convertView.findViewById(R.id.txtAuthor);
        TextView txtBorrowFrom = (TextView) convertView.findViewById(R.id.txtBorrowFrom);
        TextView txtShared = (TextView) convertView.findViewById(R.id.txtShared);
        ImageView imgBook = (ImageView) convertView.findViewById(R.id.imgAvatar);
        ImageView imgExtend = (ImageView) convertView.findViewById(R.id.imgExtend);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        Switch mySwitch = (Switch) convertView.findViewById(R.id.mySwitch);
        BookEntity entity = (BookEntity) getItem(position);
        if (entity != null){
            if(entity.getSharingStatus() == 2 || entity.getSharingStatus() == 1){
                imgExtend.setVisibility(View.VISIBLE);
                mySwitch.setVisibility(View.GONE);
                txtShared.setVisibility(View.GONE);
            }else if(entity.getSharingStatus() == 0){
                imgExtend.setVisibility(View.GONE);
                mySwitch.setVisibility(View.VISIBLE);
                txtShared.setVisibility(View.GONE);
            }else{
                imgExtend.setVisibility(View.GONE);
                mySwitch.setVisibility(View.GONE);
                txtShared.setVisibility(View.GONE);
            }
            txtTitle.setText(entity.getTitle());
            txtAuthor.setText(entity.getAuthor());
            txtBorrowFrom.setText(entity.getBorrowingUserName());
            ratingBar.setRating(entity.getRateCount());
            if(!Strings.isNullOrEmpty(entity.getImageId())) {
                ClientUtils.setImage(imgBook,R.drawable.ic_book_default,ClientUtils.getUrlImage(entity.getImageId(), Constance.IMAGE_SIZE_SMALL));
            }
        }
        return convertView;
    }
}
