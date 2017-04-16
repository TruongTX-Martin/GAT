package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/15/2017.
 */

public class SuggestBooksAfterLogin extends UseCase<List<BookResponse>>  {

    private final BookRepository repository;

    public SuggestBooksAfterLogin(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<List<BookResponse>> createObservable() {
        return this.repository.suggestBooksAfterLogin();
    }

}
