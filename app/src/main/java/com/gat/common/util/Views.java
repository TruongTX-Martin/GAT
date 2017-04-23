package com.gat.common.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Rey on 2/16/2017.
 */
public class Views {

    public static void hideKeyboard(Context context){
        if(context == null || !(context instanceof Activity))
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = ((Activity)context).getCurrentFocus();
        if (view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
