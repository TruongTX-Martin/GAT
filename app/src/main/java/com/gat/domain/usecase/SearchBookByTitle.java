package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class SearchBookByTitle extends UseCase<DataResultListResponse<BookResponse>> {

    private final BookRepository bookRepository;
    private final String title;
    private final int page;
    private final int sizeOfPage;


    public SearchBookByTitle (BookRepository repository, String title, int page, int sizeOfPage) {
        bookRepository = repository;
        this.title = title;
        this.page = page;
        this.sizeOfPage = sizeOfPage;
    }

    @Override
    protected Observable<DataResultListResponse<BookResponse>> createObservable() {
        return bookRepository.searchBookByTitle(title, page, sizeOfPage);
    }

}
