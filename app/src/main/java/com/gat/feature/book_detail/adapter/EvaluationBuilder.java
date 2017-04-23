package com.gat.feature.book_detail.adapter;

import com.gat.common.adapter.Item;
import com.gat.data.response.impl.EvaluationItemResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mryit on 4/20/2017.
 */

public class EvaluationBuilder {

    public static List<Item> transformListEvaluation (List<EvaluationItemResponse> list) {
        List<Item> newList = new ArrayList<>();

        for (EvaluationItemResponse evaluation : list) {
            newList.add(EvaluationItem.instance(evaluation));
        }

        return newList;
    }

}
