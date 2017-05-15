package com.gat.feature.suggestion.search.listener;

import com.gat.data.response.BookResponse;

import java.util.List;

/**
 * Created by mozaa on 11/04/2017.
 */

public interface OnSearchBookResult {

    void onSearchBookResult (List<BookResponse> list);
}
