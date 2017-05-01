package com.gat.feature.register.update.category;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gat.R;
import com.gat.repository.entity.BookCategory;

/**
 * Created by ducbtsn on 3/5/17.
 */

public class CategoryAdapter extends BaseAdapter {
    private final Context context;
    private final BookCategory[] categories;

    public CategoryAdapter(Context context, BookCategory[] categories) {
        this.context = context;
        this.categories = categories;
    }
    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position < getCount() ? position : 0];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        BookCategory category = categories[position];
        Log.d("GetView", "Category" + category.categoryId() + ":" +category.name());

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.category_layout, null);
        }

        // 3
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_cate_name);
        ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        // 4
        Resources r = context.getResources();
        Drawable[] layers = new Drawable[2];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            layers[0] = r.getDrawable(category.coverId(), context.getTheme());
            layers[1] = r.getDrawable(category.favor()? R.drawable.category_transparent_layer : R.drawable.category_cover_overlay, context.getTheme());
        } else{
            layers[0] = ContextCompat.getDrawable(context, category.coverId());
            layers[1] = ContextCompat.getDrawable(context, category.favor()? R.drawable.category_transparent_layer : R.drawable.category_cover_overlay);
        }

        LayerDrawable layerDrawable = new LayerDrawable(layers);
        imageView.setImageDrawable(layerDrawable);
        imageView.setAlpha(category.favor() ? (float)1.0:(float)0.2);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageViewFavorite.setVisibility(category.favor() ? View.VISIBLE : View.INVISIBLE);
        nameTextView.setText(category.name());

        Log.d("GetView", "Category" + category.categoryId() + ":end");
        return convertView;
    }
}
