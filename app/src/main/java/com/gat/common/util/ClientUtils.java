package com.gat.common.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class ClientUtils {

    public static Context context;

    public static boolean validate(String input) {
        if (input == null || input.length() == 0 || input.equals("null")) {
            return false;
        }
        return true;
    }

    public static void showToast(String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
}
