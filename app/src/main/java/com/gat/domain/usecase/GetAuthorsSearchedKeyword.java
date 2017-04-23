package com.gat.domain.usecase;

import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class GetAuthorsSearchedKeyword extends UseCase<List<String>>  {

    private final BookRepository bookRepository;

    public GetAuthorsSearchedKeyword(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    protected Observable<List<String>> createObservable() {
        return bookRepository.getAuthorsSearchedKeyword();
    }

}
