package com.gat.feature.register.update.category;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final BookCategory category = categories[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.category_layout, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_cate_name);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        // 4
        imageView.setImageResource(category.coverId());
        nameTextView.setText(category.name());
        imageViewFavorite.setVisibility(category.favor() ? View.VISIBLE : View.GONE);

        Resources r = context.getResources();
        Drawable[] layers = new Drawable[2];
        layers[0] = r.getDrawable(category.coverId(), context.getTheme());
        layers[1] = category.favor() ? new ColorDrawable(Color.TRANSPARENT) : r.getDrawable(R.drawable.category_cover_overlay, context.getTheme());
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        imageView.setImageDrawable(layerDrawable);
        imageView.setAlpha(category.favor() ? (float)1.0:(float)0.2);

        return convertView;
    }
}
