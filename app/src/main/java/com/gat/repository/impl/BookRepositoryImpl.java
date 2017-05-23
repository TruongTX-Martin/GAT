package com.gat.repository.impl;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.BorrowResponse;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.repository.BookRepository;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.datasource.UserDataSource;
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
    private final Lazy<UserDataSource> localUserDataSourceLazy;

    public BookRepositoryImpl(Lazy<BookDataSource> networkDataSourceLazy, Lazy<BookDataSource> localDataSourceLazy,
                              Lazy<UserDataSource> localUserDataSourceLazy){
        this.networkDataSourceLazy = networkDataSourceLazy;
        this.localDataSourceLazy = localDataSourceLazy;
        this.localUserDataSourceLazy = localUserDataSourceLazy;
    }

    @Override
    public Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByKeyword(keyword, page, sizeOfPage));
    }

    @Override
    public Observable<Integer> searchBookByIsbn(String isbn) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByIsbn(isbn));
    }

    @Override
    public Observable<List<BookResponse>> suggestMostBorrowing() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestMostBorrowing());
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksWithoutLogin() {
        return Observable.defer(() -> networkDataSourceLazy.get().suggestBooksWithoutLogin());
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksAfterLogin() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestBooksAfterLogin());
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByTitle(String title, int page, int sizeOfPage) {
        return Observable.defer(()-> networkDataSourceLazy.get().searchBookByTitle(title, page, sizeOfPage));
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByAuthor(String author, int page, int sizeOfPage) {
        return Observable.defer(()->networkDataSourceLazy.get().searchBookByAuthor(author, page, sizeOfPage));
    }

    @Override
    public Observable<DataResultListResponse> searchBookByTitleTotal(String title, int userId) {
        return Observable.defer(()->networkDataSourceLazy.get().searchBookByTitleTotal(title, userId));
    }

    @Override
    public Observable<DataResultListResponse> searchBookByAuthorTotal(String author, int userId) {
        return Observable.defer(()->networkDataSourceLazy.get().searchBookByAuthorTotal(author, userId));
    }

    @Override
    public Observable<List<Keyword>> getBooksSearchedKeyword() {
        return Observable.defer(()->networkDataSourceLazy.get().getBooksSearchedKeyword());
    }

    @Override
    public Observable<List<Keyword>> getAuthorsSearchedKeyword() {
        return Observable.defer(()->networkDataSourceLazy.get().getAuthorsSearchedKeyword());
    }

    @Override
    public Observable<BookInfo> getBookInfo(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookInfo(editionId));
    }

    @Override
    public Observable<List<EvaluationItemResponse>> getBookEditionEvaluation(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookEditionEvaluation(editionId));
    }

    @Override
    public Observable<BookReadingInfo> getReadingStatus(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getReadingStatus(editionId));
    }

    @Override
    public Observable<EvaluationItemResponse> getBookEvaluationByUser(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookEvaluationByUser(editionId));
    }

    @Override
    public Observable<List<UserResponse>> getEditionSharingUser(int editionId) {
        return Observable.defer(()-> localUserDataSourceLazy.get().loadUser())
                .flatMap(user -> {

                    Float latitude = null;
                    Float longitude = null;

                    if (user.usuallyLocation() != null && !user.usuallyLocation().isEmpty()) {

                    }

                    return networkDataSourceLazy.get().getEditionSharingUser(editionId, user.userId(), latitude, longitude);
        });
    }

    @Override
    public Observable<ServerResponse> postComment(int editionId, int value, String review, boolean spoiler, Integer evaluationId, Integer readingId, int bookId) {
        return Observable.defer( ()->networkDataSourceLazy.get().postComment(editionId, value, review, spoiler, evaluationId, readingId, bookId));
    }

    @Override
    public Observable<BookInstanceInfo> getSelfInstanceInfo(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getSelfInstanceInfo(editionId));
    }

    @Override
    public Observable<ServerResponse> selfAddInstance(int editionId, int sharingStatus, int numberOfBook, int bookId, Integer readingId) {
        return Observable.defer( ()->networkDataSourceLazy.get().selfAddInstance(editionId, sharingStatus, numberOfBook, bookId, readingId));
    }

    @Override
    public Observable<ServerResponse> selfUpdateReadingStatus(int editionId, int readingStatus, Integer readingId, int bookId) {
        return Observable.defer( ()->networkDataSourceLazy.get().selfUpdateReadingStatus(editionId, readingStatus, readingId, bookId));
    }

    @Override
    public Observable<BorrowResponse> requestBorrow(int editionId, int ownerId) {
        return Observable.defer( ()->networkDataSourceLazy.get().requestBorrow(editionId, ownerId));
    }

}
