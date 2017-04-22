package com.gat.common.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gat.dependency.AppModule;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class ClientUtils {

    public final static String SIZE_DEFAULT = "o";
    public final static String SIZE_THUMBNAIL = "t";
    public final static String SIZE_SMALL = "s";
    public final static String SIZE_LARGE = "q";

    public static Context context;


    public static void showToast(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static String getUrlImage(String image, String size) {
        return Constance.BASE_URL_IMAGE + "common/get_image/" + image + "?size=" + size;
    }

    public static void setImage(ImageView image, int drawble, String url) {
        if (image != null) {
            Glide.with(context).load(url).placeholder(drawble).error(drawble).dontAnimate().into(image);
        }
    }

    public static void setImage(ImageView image, String url) {
        if (image != null) {
            Glide.with(context).load(url).dontAnimate().into(image);
        }
    }

}
