package com.gat.common.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    public static void navigationToView (FragmentActivity activity, Fragment _fragment,
                                            int id_fragmentLayout, int animEnter, int animExit) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(animEnter, animExit);
        transaction.replace(id_fragmentLayout, _fragment);
        //transaction.addToBackStack(null);
//        for(int i = 0; i < manager.getBackStackEntryCount(); ++i) {
//            manager.popBackStack();
//        }
        transaction.commit();
    }


}
