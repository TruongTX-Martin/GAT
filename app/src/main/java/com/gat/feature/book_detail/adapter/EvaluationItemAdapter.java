package com.gat.feature.book_detail.adapter;

import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemAdapter;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.util.MZDebug;
import com.gat.data.response.impl.EvaluationItemResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mryit on 4/20/2017.
 */

public class EvaluationItemAdapter extends ItemAdapter {

    public EvaluationItemAdapter() {
        setReady();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder = null;

        viewHolder = new EvaluationItemViewHolder(parent, R.layout.item_comment_evaluation);

        return viewHolder;
    }

    public void setItems (List<EvaluationItemResponse> list) {
        List<Item> newList = new ArrayList<>();

        EvaluationItem evaluationItem;
        for (int i=0, z=list.size(); i<z; i++) {
            MZDebug.w("Evaluation = " + list.get(i).toString());
            evaluationItem = EvaluationItem.instance(list.get(i));
            newList.add(evaluationItem);
        }
        MZDebug.e("List comment size = " + newList.size());

        if (items == null) {
            items = new ArrayList<>();
            items.addAll(newList);
        } else {
            items.clear();
            items.addAll(newList);
        }
        notifyDataSetChanged();
    }


    public void updateItem (EvaluationItemResponse item) {
        MZDebug.w("Adapter : updateItem");

        if (items == null || items.isEmpty()) { // insert
            List<Item> newList = new ArrayList<>();
            newList.add(EvaluationItem.instance(item));

            items = newList;
        } else { // update
            EvaluationItem evaluationItem;
            for (int i=0, z=items.size(); i<z; i++) {
                evaluationItem = (EvaluationItem) items.get(i);
                if (evaluationItem.evaluation().getUserId() == item.getUserId()) {
                    // tìm thấy bình luận trong list
                    evaluationItem.evaluation().setReview(item.getReview());
                    evaluationItem.evaluation().setValue(item.getValue());
                    notifyItemChanged(i);
                    return; // tìm thấy return luôn,
                }
            }
            // chạy hết 1 vòng không tìm thấy bình luận trong list, add vào
            EvaluationItem newComment = EvaluationItem.instance(item);
            items.add(0, newComment);
            notifyDataSetChanged();
        }
    }


    public float rateAvg () {
        float avg = 0;
        float totalRate = 0;

        EvaluationItem evaluationItem;
        for (int i=0, z=items.size(); i<z; i++) {
            evaluationItem = (EvaluationItem) items.get(i);
            totalRate += evaluationItem.evaluation().getValue();
        }

        avg = totalRate/items.size();
        DecimalFormat df = new DecimalFormat("#.0");

        return Float.valueOf(df.format(avg));
    }

}
