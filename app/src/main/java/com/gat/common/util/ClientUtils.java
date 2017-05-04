package com.gat.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.gat.dependency.AppModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class ClientUtils {

    public final static String SIZE_DEFAULT = "o";
    public final static String SIZE_THUMBNAIL = "t";
    public final static String SIZE_SMALL = "s";
    public final static String SIZE_LARGE = "q";

    public static Context context;
    private static final String DEFAULT_IMAGE = "33328625223";       // TODO default image path


    public static void showToast(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static String getUrlImage(String image, String size) {
        return Constance.BASE_URL_IMAGE + "common/get_image/" + (Strings.isNullOrEmpty(image) ? DEFAULT_IMAGE : image)+ "?size=" + size;
    }

    public static void setImage(ImageView image, int drawble, String url) {
        if (image != null) {
            Glide.with(context).load(url).placeholder(drawble).error(drawble).dontAnimate().into(image);
        }
    }

    public static void setImage(ImageView image, String url) {
        if(image != null){
            Glide.with(context).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).fitCenter().into(image);
        }
    }

    public static String formatColor(String input,String color){
        return "<font color=\""+ color +"\">" + input + "</font>";
    }
    public static String getDateFromString(String input){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatBack = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = (Date) format.parse(input);
            String dateConvert = formatBack.format(date);
            return dateConvert;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap loadBitmap(ImageView imageView, String url, OnBitmapLoaded onBitmapLoaded){
        Bitmap bitmap = null;
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(imageView.getWidth(), imageView.getHeight()) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(resource);
                        onBitmapLoaded.onBitmapLoaded(resource);
                    }
                });

        return bitmap;
    }


    public interface OnBitmapLoaded {
        void onBitmapLoaded (Bitmap bitmap);
    }

    public static @Nullable Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String imageEncode64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

}
