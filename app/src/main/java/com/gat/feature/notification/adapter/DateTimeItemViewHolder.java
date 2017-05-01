package com.gat.feature.notification.adapter;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mozaa on 28/04/2017.
 */

public class DateTimeItemViewHolder extends ItemViewHolder<DateTimeItem> {

    @BindView(R.id.text_view_time_display)
    TextView textViewTimeDisplay;

    public DateTimeItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(DateTimeItem item) {
        super.onBindItem(item);

        textViewTimeDisplay.setText(item.timeDisplay());
    }
}
