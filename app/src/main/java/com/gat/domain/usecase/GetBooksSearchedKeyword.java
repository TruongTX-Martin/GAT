package com.gat.domain.usecase;

import com.gat.data.response.impl.Keyword;
import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class GetBooksSearchedKeyword extends UseCase<List<Keyword>>  {

    private final BookRepository bookRepository;

    public GetBooksSearchedKeyword(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    protected Observable<List<Keyword>> createObservable() {
        return bookRepository.getBooksSearchedKeyword();
    }
}
