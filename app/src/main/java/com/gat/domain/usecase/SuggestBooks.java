package com.gat.domain.usecase;

import com.gat.repository.BookRepository;
import com.gat.repository.entity.Book;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mozaa on 30/03/2017.
 */

public class SuggestBooks extends UseCase<List<Book>>  {

    private final BookRepository repository;

    public SuggestBooks(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<List<Book>> createObservable() {
        return this.repository.suggestBooks();
    }
}
