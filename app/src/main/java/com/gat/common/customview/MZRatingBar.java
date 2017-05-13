package com.gat.common.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.RatingBar;

/**
 * Created by mryit on 5/12/2017.
 */

public class MZRatingBar extends RatingBar {

    public MZRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public MZRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MZRatingBar(Context context) {
        super(context);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LayerDrawable stars = (LayerDrawable) getProgressDrawable();

        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

    }
}
