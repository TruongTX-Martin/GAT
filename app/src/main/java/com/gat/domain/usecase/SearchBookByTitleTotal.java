package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 5/21/2017.
 */

public class SearchBookByTitleTotal extends UseCase<DataResultListResponse> {

    private final BookRepository bookRepository;
    private final String title;
    private final int userId;

    public SearchBookByTitleTotal(BookRepository bookRepository, String title, int userId) {
        this.bookRepository = bookRepository;
        this.title = title;
        this.userId = userId;
    }

    @Override
    protected Observable<DataResultListResponse> createObservable() {
        return bookRepository.searchBookByAuthorTotal(title, userId);
    }
}
