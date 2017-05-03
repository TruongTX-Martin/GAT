package com.gat.feature.suggestion.search.listener;

import com.gat.data.response.impl.Keyword;

import java.util.List;

/**
 * Created by mryit on 4/13/2017.
 */

public interface OnLoadHistorySuccess {

    void onLoadHistoryResult (List<Keyword> list);

}
