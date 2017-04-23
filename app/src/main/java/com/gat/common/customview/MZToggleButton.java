package com.gat.common.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;

/**
 * Created by mryit on 4/16/2017.
 */

public class MZToggleButton extends CompoundButton {

    public MZToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MZToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MZToggleButton(Context context) {
        super(context);
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(listener);

    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return super.isChecked();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || !isClickable()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                // move icon
                break;

            case MotionEvent.ACTION_UP:
                setPressed(false);
                // check checked state -> move icon to left, or right
                break;

        }

        return super.onTouchEvent(event);
    }
}
