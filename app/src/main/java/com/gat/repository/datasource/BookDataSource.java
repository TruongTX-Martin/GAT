package com.gat.repository.datasource;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
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

    Observable<BookInfo> getBookInfo (int editionId);

    Observable<List<EvaluationItemResponse>> getBookEditionEvaluation (int editionId);

    Observable<BookReadingInfo> getReadingStatus(int editionId);

    Observable<EvaluationItemResponse> getBookEvaluationByUser (int editionId);

    Observable<List<UserResponse>> getEditionSharingUser (int editionId);

    Observable<ServerResponse> postComment(int editionId, int value, String review, boolean spoiler);

    Observable <BookInstanceInfo> getSelfInstanceInfo (int editionId);

    Observable<ServerResponse> selfAddInstance (int editionId, int sharingStatus, String numberOfBook);

    Observable<ServerResponse> selfUpdateReadingStatus (int editionId, int readingStatus);


}
