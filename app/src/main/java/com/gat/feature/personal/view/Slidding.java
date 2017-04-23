package com.gat.feature.personal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by truongtechno on 01/04/2017.
 */

public class Slidding extends LinearLayout {
    private Paint innerPaint, borderPaint;

    public Slidding(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Slidding(Context context) {
        super(context);
        init();
    }

    private void init() {
        innerPaint = new Paint();
        innerPaint.setARGB(225, 75, 75, 75); //gray
        innerPaint.setAntiAlias(true);
        borderPaint = new Paint();
        borderPaint.setARGB(255, 255, 255, 255);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
    }

    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
        super.dispatchDraw(canvas);
    }

}
