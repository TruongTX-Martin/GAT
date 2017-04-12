package com.gat.feature.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.feature.personal.entity.BookReadingEntity;

import java.util.List;

/**
 * Created by root on 07/04/2017.
 */

public class BookReadingAdapter  extends RecyclerView.Adapter<BookReadingAdapter.BookReadingViewHolder> {

    private Context context;
    private List<BookReadingEntity> listBookReading;
    private LayoutInflater inflate;
    public BookReadingAdapter(Context context, List<BookReadingEntity> list) {
        this.context = context;
        this.listBookReading = list;
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
            }
            if(position == (getItemCount() -1)){
                ClientUtils.showToast("Load more");
            }
    }

    @Override
    public int getItemCount() {
        return listBookReading.size();
    }


    public class  BookReadingViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtAuthor;
        public BookReadingViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
        }
    }
}
