package com.gat.feature.notification.adapter;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.adapter.impl.LoadingItem;
import com.gat.common.adapter.impl.LoadingItemViewHolder;
import com.gat.common.util.DateTimeUtil;
import com.gat.data.response.impl.NotifyEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mozaa on 28/04/2017.
 */

public class NotificationAdapter extends ItemAdapter {

    @IntDef({Type.LOADING, Type.TIME_DISPLAY, Type.NOTIFY_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        int LOADING = 1;
        int TIME_DISPLAY = 2;
        int NOTIFY_ITEM = 3;
    }

    private OnItemNotifyClickListener listener;

    public NotificationAdapter() {
        setReady();
    }

    public void setOnItemNotifyClick(OnItemNotifyClickListener listener) {
        this.listener = listener;
    }

    @Override
    public
    @Type
    int getItemViewType(int position) {
        Item item = getItemAt(position);

        if (item instanceof LoadingItem) {
            return Type.LOADING;
        } else if (item instanceof DateTimeItem) {
            return Type.TIME_DISPLAY;
        } else if (item instanceof NotificationItem) {
            return Type.NOTIFY_ITEM;
        }

        throw new IllegalArgumentException("NotificationAdapter: Not support item " + item);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = null;

        switch (viewType) {
            case Type.LOADING:
                viewHolder = new LoadingItemViewHolder(parent, R.layout.search_item_loading);
                break;

            case Type.TIME_DISPLAY:
                viewHolder = new DateTimeItemViewHolder(parent, R.layout.item_header_date_time_display);
                break;

            case Type.NOTIFY_ITEM:
                viewHolder = new NotificationItemViewHolder(parent, R.layout.item_notification, items);
                viewHolder.itemView.setOnClickListener(view -> {
                    // Tag NotifyEntity đã được gán trong NotificationItemViewHolder
                    NotifyEntity item = (NotifyEntity) view.getTag();
                    listener.onItemNotifyClick(item);
                });
                break;
        }

        return viewHolder;
    }


    public boolean setItems(List<NotifyEntity> list) {

        try {
            if (list == null || list.isEmpty()) {
                return false;
            }

            List<Item> listItems = new ArrayList<>();
            NotificationItem item;
            DateTimeItem timeHeader;

            // add first item
            String timeToday = DateTimeUtil.transformDate(list.get(0).beginTime());
            timeHeader = DateTimeItem.instance(timeToday);
            listItems.add(timeHeader);

            item = NotificationItem.instance(list.get(0));
            listItems.add(item);

            // add next item to end of list.
            for (int i = 1, z = list.size(); i < z; i++) {

                String string = DateTimeUtil.transformDate(list.get(i).beginTime());
                String stringPrev = DateTimeUtil.transformDate(list.get(i - 1).beginTime());

                if (!string.equals(stringPrev)) {
                    timeHeader = DateTimeItem.instance(DateTimeUtil.transformDate(list.get(i).beginTime()));
                    listItems.add(timeHeader);
                }

                item = NotificationItem.instance(list.get(i));
                listItems.add(item);
            }

            setItem(listItems);
            notifyDataSetChanged();
        } catch (Exception e) {
        }

        return true;
    }

    public interface OnItemNotifyClickListener {
        void onItemNotifyClick(NotifyEntity item);
    }


}
