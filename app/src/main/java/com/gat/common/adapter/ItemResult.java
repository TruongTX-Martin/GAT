package com.gat.common.adapter;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

/**
 * Created by Rey on 2/14/2017.
 */
@AutoValue
public abstract class ItemResult {

    public static final ItemResult EMPTY = instance(Collections.emptyList(), null);

    public static ItemResult instance(List<Item> items, @Nullable DiffUtil.DiffResult diffResult){
        return new AutoValue_ItemResult(items, diffResult);
    }

    public abstract List<Item> items();
    public abstract @Nullable DiffUtil.DiffResult diffResult();

}
