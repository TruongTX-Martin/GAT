package com.gat.feature.suggestion.search;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchPresenterImpl implements SuggestSearchPresenter {

    private static final int COUNT_PER_PAGE = 10;
    private int mPageAuthor;
    private int mPageBook;
    private int mPageUser;
    private DataResultListResponse<BookResponse> dataSearchBookByTitle;
    private DataResultListResponse<BookResponse> dataSearchBookByAuthor;
    private DataResultListResponse<UserResponse> dataSearchUserByName;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByTitle;
    private final Subject<List<BookResponse>> resultSearchBookByTitleSubject;

    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByAuthor;
    private final Subject<List<BookResponse>> resultSearchBookByAuthorSubject;

    private UseCase<DataResultListResponse<UserResponse>> useCaseSearchUserByName;
    private final Subject<List<UserResponse>> resultSearchUserByNameSubject;

    private final Subject<String> errorSubject;

    public SuggestSearchPresenterImpl(UseCaseFactory useCaseFactory,
                                      SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultSearchBookByTitleSubject = PublishSubject.create();;
        resultSearchBookByAuthorSubject = PublishSubject.create();;
        resultSearchUserByNameSubject = PublishSubject.create();;
        errorSubject = PublishSubject.create();;
    }

    @Override
    public void onCreate() {
        mPageAuthor = 1;
        mPageBook = 1;
        mPageUser = 1;
    }

    @Override
    public void onDestroy() {}


    @Override
    public void searchBookWithTitle(String book_title) {
        // search lai 1 cai thi page tro ve 1
        mPageBook = 1;
    }

    @Override
    public void loadMoreBookWithTitle(String book_title) {
        // load more -> check listBook.size() < total result
        // thi page +1 va load tiep
        // if can not load more -> return list empty

    }

    @Override
    public Observable<List<BookResponse>> onSearchBookWithTitleSuccess() {
        return null;
    }

    @Override
    public void searchBookWithAuthor(String author) {

    }

    @Override
    public void loadMoreBookWithAuthor(String author) {

    }

    @Override
    public Observable<List<BookResponse>> onSearchBookWithAuthorSuccess() {
        return null;
    }

    @Override
    public void searchUserWithName(String name) {

    }

    @Override
    public void loadMoreUserWithName(String name) {

    }

    @Override
    public Observable<List<UserResponse>> onSearchUserWithNameSuccess() {
        return null;
    }
}

