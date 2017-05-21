package com.gat.domain.usecase;

import com.gat.data.response.DataResultListResponse;
import com.gat.repository.BookRepository;
import io.reactivex.Observable;

/**
 * Created by mryit on 5/21/2017.
 */

public class SearchBookByAuthorTotal extends UseCase<DataResultListResponse>  {

    private final BookRepository bookRepository;
    private final String author;
    private final int userId;

    public SearchBookByAuthorTotal(BookRepository bookRepository, String author, int userId) {
        this.bookRepository = bookRepository;
        this.author = author;
        this.userId = userId;
    }


    @Override
    protected Observable<DataResultListResponse> createObservable() {
        return bookRepository.searchBookByAuthorTotal(author, userId);
    }


}
