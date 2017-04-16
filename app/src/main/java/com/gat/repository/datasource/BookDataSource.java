package com.gat.repository.datasource;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.repository.entity.Book;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public interface BookDataSource {

    Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage);

    Observable<Book> searchBookByIsbn(String isbn);

    Observable<List<BookResponse>> suggestMostBorrowing();

    Observable<List<BookResponse>> suggestBooksWithoutLogin();

    Observable<List<BookResponse>> suggestBooksAfterLogin();

    Observable<DataResultListResponse<BookResponse>> searchBookByTitle
            (String title, long userId, int page, int sizeOfPage);

    Observable<DataResultListResponse<BookResponse>> searchBookByAuthor
            (String author, long userId, int page, int sizeOfPage);

    Observable<List<String>> getBooksSearchedKeyword();

    Observable<List<String>> getAuthorsSearchedKeyword();
}
