package com.gat.repository.impl;

import com.gat.data.response.ServerResponse;
import com.gat.repository.BookRepository;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.entity.Book;

import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public class BookRepositoryImpl implements BookRepository {

    private final Lazy<BookDataSource> networkDataSourceLazy;
    private final Lazy<BookDataSource> localDataSourceLazy;

    public BookRepositoryImpl(Lazy<BookDataSource> networkDataSourceLazy, Lazy<BookDataSource> localDataSourceLazy){
        this.networkDataSourceLazy = networkDataSourceLazy;
        this.localDataSourceLazy = localDataSourceLazy;
    }

    @Override
    public Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByKeyword(keyword, page, sizeOfPage));
    }

    @Override
    public Observable<Book> searchBookByIsbn(String isbn) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByIsbn(isbn));
    }

    @Override
    public Observable<List<Book>> suggestMostBorrowing() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestMostBorrowing());
    }

    @Override
    public Observable<List<Book>> suggestBooks() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestBooks());
    }

}
