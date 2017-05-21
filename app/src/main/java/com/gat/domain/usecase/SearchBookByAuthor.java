package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.repository.BookRepository;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class SearchBookByAuthor extends UseCase<DataResultListResponse<BookResponse>> {

    private final BookRepository bookRepository;
    private final String author;
    private final int page;
    private final int sizeOfPage;


    public SearchBookByAuthor(BookRepository bookRepository, String author, int page, int sizeOfPage) {
        this.bookRepository = bookRepository;
        this.author = author;
        this.page = page;
        this.sizeOfPage = sizeOfPage;
    }

    @Override
    protected Observable<DataResultListResponse<BookResponse>> createObservable() {
        return bookRepository.searchBookByAuthor(author, page, sizeOfPage);
    }
}
