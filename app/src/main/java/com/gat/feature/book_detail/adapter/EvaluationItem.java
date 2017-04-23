package com.gat.feature.book_detail.adapter;

import com.gat.common.adapter.Item;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/20/2017.
 */

@AutoValue
public abstract class EvaluationItem implements Item {

    public static EvaluationItem instance (EvaluationItemResponse evaluationItemResponse) {
        return new AutoValue_EvaluationItem(evaluationItemResponse);
    }

    public abstract EvaluationItemResponse evaluation();
}
