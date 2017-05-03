package com.gat.feature.suggestion.search.item;

import com.gat.common.adapter.Item;
import com.gat.data.response.BookResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mryit on 4/15/2017.
 */

public class SearchBuilder {

    public static List<Item> transformListHistory (List<Keyword> list) {
        List<Item> newList = new ArrayList<>();

        for (Keyword keyword : list) {
            newList.add(SearchHistoryItem.instance(keyword));
        }

        return newList;
    }

    public static List<Item> transformListBook (List<BookResponse> list) {
        List<Item> newList = new ArrayList<>();

        for (BookResponse book : list) {
            newList.add(SearchBookResultItem.instance(book));
        }

        return newList;
    }


    public static List<Item> transformListUser (List<UserResponse> list) {
        List<Item> newList = new ArrayList<>();

        for (UserResponse user : list) {
            newList.add(SearchUserResultItem.instance(user));
        }

        return newList;
    }
}
