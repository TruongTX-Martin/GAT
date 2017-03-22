package com.gat.feature.search;

import android.support.v7.util.DiffUtil;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.adapter.ItemsComparator;
import com.gat.common.util.Objects;
import com.gat.common.util.Strings;
import com.gat.feature.search.item.BookItem;
import com.gat.feature.search.item.LoadingItem;
import com.gat.repository.entity.Author;
import com.gat.repository.entity.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rey on 2/15/2017.
 */

public class SearchItemBuilder {

    ItemResult defaultItems(){
        Item item = LoadingItem.instance(LoadingItem.Message.DEFAULT, true);
        return ItemResult.instance(Collections.singletonList(item), null);
    }

    private String getAuthorNames(List<Author> authors){
        if(authors.isEmpty())
            return Strings.EMPTY;
        StringBuilder sb = new StringBuilder();
        for(int i = 0, size = authors.size(); i < size; i++) {
            sb.append(authors.get(i).name());
            if(i < size - 1)
                sb.append(", ");
        }
        return sb.toString();
    }

    private String getRatingText(float rating){
        return String.format(Locale.ENGLISH, "%.1f", rating);
    }

    ItemResult addBooks(List<Item> items, List<Book> books, boolean clearOldItems, boolean showLoadMore){
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>(oldSize + books.size());

        if(!clearOldItems)
            for(Item item : items)
                if(!(item instanceof LoadingItem))
                    newItems.add(item);

        for(Book book : books)
            newItems.add(BookItem.instance(book, getAuthorNames(book.authors()), getRatingText(book.rating())));

        if(newItems.isEmpty())
            newItems.add(LoadingItem.instance(LoadingItem.Message.EMPTY, true));
        else if(showLoadMore)
            newItems.add(LoadingItem.instance(LoadingItem.Message.LOADING, false));

        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }

    ItemResult showLoading(List<Item> items, boolean clearOldItems, boolean addIfNeed, @LoadingItem.Message int error){
        int oldSize = items.size();
        List<Item> newItems = new ArrayList<>(oldSize + 1);

        int index = -1;
        if(!clearOldItems) {
            for (int i = 0; i < oldSize; i++) {
                Item item = items.get(i);
                if (item instanceof LoadingItem)
                    index = i;
                else
                    newItems.add(item);
            }
        }
        if(index < 0 && addIfNeed)
            index = newItems.size();

        if(index >= 0)
            newItems.add(index, LoadingItem.instance(error, newItems.isEmpty()));

        return ItemResult.instance(newItems, DiffUtil.calculateDiff(new Comparator(items, newItems)));
    }

    private class Comparator extends ItemsComparator{

        public Comparator(List<Item> oldItems, List<Item> newItems) {
            super(oldItems, newItems);
        }

        @Override
        protected boolean compareItem(Item oldItem, Item newItem) {
            if(oldItem instanceof LoadingItem && newItem instanceof LoadingItem)
                return ((LoadingItem)oldItem).fullHeight() == ((LoadingItem)newItem).fullHeight();
            if(oldItem instanceof BookItem && newItem instanceof BookItem)
                return Objects.equals(((BookItem)oldItem).book().id(), ((BookItem)newItem).book().id());
            return super.compareItem(oldItem, newItem);
        }

    }
}
