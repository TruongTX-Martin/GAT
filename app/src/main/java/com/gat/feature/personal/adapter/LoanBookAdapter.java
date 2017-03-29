package com.gat.feature.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gat.R;
import com.gat.feature.personal.entity.BookEntity;

import java.util.List;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class LoanBookAdapter extends BaseAdapter {

    private List<BookEntity> list;
    private Context context;

    public LoanBookAdapter(List<BookEntity> list, Context context) {
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
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtAuthor = (TextView) convertView.findViewById(R.id.txtAuthor);
        BookEntity entity = (BookEntity) getItem(position);
        if (entity != null){
            txtName.setText(entity.getName());
            txtAuthor.setText(entity.getAuthor());
        }
        return convertView;
    }
}
