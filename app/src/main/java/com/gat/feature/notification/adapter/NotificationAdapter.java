package com.gat.feature.notification.adapter;

import android.support.annotation.IntDef;
import android.view.ViewGroup;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mozaa on 28/04/2017.
 */

public class NotificationAdapter extends ItemAdapter{

    @IntDef({Type.LOADING, Type.TIME_DISPLAY, Type.NOTIFY_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int TIME_DISPLAY  = 2;
        int NOTIFY_ITEM = 3;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = null;



        return viewHolder;
    }



}
