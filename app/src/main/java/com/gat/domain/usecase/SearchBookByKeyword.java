package com.gat.domain.usecase;

import com.gat.repository.BookRepository;
import com.gat.repository.entity.Book;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public class SearchBookByKeyword extends UseCase<List<Book>> {

    private final BookRepository repository;
    private final String keyword;
    private final int page;
    private final int sizeOfPage;

    public SearchBookByKeyword(BookRepository repository, String keyword, int page, int sizeOfPage){
        this.repository = repository;
        this.keyword = keyword;
        this.page = page;
        this.sizeOfPage = sizeOfPage;
    }

    @Override
    protected Observable<List<Book>> createObservable() {
        return repository.searchBookByKeyword(keyword, page, sizeOfPage);
    }
}
