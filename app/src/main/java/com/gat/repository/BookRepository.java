package com.gat.repository;

import com.gat.data.response.BookResponse;
import com.gat.repository.entity.Book;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by Rey on 2/13/2017.
 */

public interface BookRepository {

    Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage);
    Observable<Book> searchBookByIsbn(String isbn);
    Observable<List<BookResponse>> suggestMostBorrowing();
    Observable<List<BookResponse>> suggestBooksWithoutLogin();
    Observable<List<BookResponse>> suggestBooksAfterLogin();

}
