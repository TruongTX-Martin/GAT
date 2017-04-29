package com.gat.feature.suggestion.search;

import android.util.Log;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/9/2017.
 */

public class SuggestSearchPresenterImpl implements SuggestSearchPresenter {

    private static final int SIZE_OF_PAGE = 10;
    private int mPageAuthor;
    private int mPageBook;
    private int mPageUser;
    private boolean isCanLoadHistoryBook = true;
    private boolean isCanLoadHistoryAuthor = true;
    private boolean isCanLoadHistoryUser = true;
    private String mKeyword = "";
    private int mUserId = 0;

    private DataResultListResponse<BookResponse> dataSearchBookByTitle;
    private DataResultListResponse<BookResponse> dataSearchBookByAuthor;
    private DataResultListResponse<UserResponse> dataSearchUserByName;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    // use case history search
    private UseCase<List<String>> useCaseHistorySearchBook;
    private final Subject<List<String>> resultHistorySearchBookSubject;

    private UseCase<List<String>> useCaseHistorySearchAuthor;
    private final Subject<List<String>> resultHistorySearchAuthorSubject;

    private UseCase<List<String>> useCaseHistorySearchUser;
    private final Subject<List<String>> resultHistorySearchUserSubject;

    // use case search
    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByTitle;
    private final Subject<List<BookResponse>> resultSearchBookByTitleSubject;

    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByAuthor;
    private final Subject<List<BookResponse>> resultSearchBookByAuthorSubject;

    private UseCase<DataResultListResponse<UserResponse>> useCaseSearchUserByName;
    private final Subject<List<UserResponse>> resultSearchUserByNameSubject;

    // use case error
    private final Subject<String> errorSubject;

    public SuggestSearchPresenterImpl(UseCaseFactory useCaseFactory,
                                      SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultHistorySearchBookSubject = PublishSubject.create();
        resultHistorySearchAuthorSubject = PublishSubject.create();
        resultHistorySearchUserSubject = PublishSubject.create();
        resultSearchBookByTitleSubject = PublishSubject.create();
        resultSearchBookByAuthorSubject = PublishSubject.create();
        resultSearchUserByNameSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
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
    public void loadHistorySearchBook() {
        if ( ! isCanLoadHistoryBook) {
            MZDebug.e("____________________________________________ loadHistorySearchBook = false");
            return;
        }
        // get cached user data
        UseCase<User> loadLocalUser = useCaseFactory.getUser();
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    if (user.isValid()) {
                        doLoadHistorySearchBook();
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook : authentication _______________E: \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    private void doLoadHistorySearchBook () {
        useCaseHistorySearchBook = useCaseFactory.getBooksSearchedKeyword();
        useCaseHistorySearchBook.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryBook = false;
                    resultHistorySearchBookSubject.onNext(list);
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ____________________________________E: "
                            + Log.getStackTraceString(throwable));
                }).execute();
    }


    @Override
    public Observable<List<String>> onLoadHistorySearchBookSuccess() {
        return resultHistorySearchBookSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void searchBookWithTitle(String book_title) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageBook = 1;
        mKeyword = book_title;

        // get current user -> user id # 0
        // mUserId = ? éo biết lấy ở đâu

        doSearchBookWithTitle (mKeyword, mUserId, mPageBook, SIZE_OF_PAGE);
    }

    @Override
    public void loadMoreBookWithTitle() {
        // load more -> check listBook.size() < total result
        if (dataSearchBookByTitle.getTotalResult() <= (mPageBook * SIZE_OF_PAGE)) {
            return;
        }

        // thi page +1 va load tiep
        mPageBook += 1;

        // load more
        doSearchBookWithTitle (mKeyword, mUserId, mPageBook, SIZE_OF_PAGE);
    }

    private void doSearchBookWithTitle (String keyword, int user_id, int page, int size_of_page) {
        useCaseSearchBookByTitle = useCaseFactory.searchBookByTitle(keyword, user_id, page, size_of_page)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    dataSearchBookByTitle = data; // use data to check load more
                    resultSearchBookByTitleSubject.onNext(data.getResultInfo()); // pass response to view
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Error - searchBookWithTitle");
                })
                .execute();
    }


    @Override
    public Observable<List<BookResponse>> onSearchBookWithTitleSuccess() {
        return resultSearchBookByTitleSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void loadHistorySearchAuthor() {
        if ( ! isCanLoadHistoryAuthor) {
            MZDebug.e("__________________________________________ loadHistorySearchAuthor = false");
            return;
        }
        // get cached user data
        UseCase<User> loadLocalUser = useCaseFactory.getUser();
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("local login: " + user.isValid());
                    if (user.isValid()) {
                        doLoadHistorySearchAuthor();
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchAuthor : authentication _____________E: \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();

    }

    private void doLoadHistorySearchAuthor () {
        useCaseHistorySearchAuthor = useCaseFactory.getAuthorsSearchedKeyword();
        useCaseHistorySearchAuthor.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryAuthor = false;
                    resultHistorySearchAuthorSubject.onNext(list);
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchAuthor ______________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                }).execute();
    }


    @Override
    public Observable<List<String>> onLoadHistorySearchAuthorSuccess() {
        return resultHistorySearchAuthorSubject.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void searchBookWithAuthor(String author) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageAuthor = 1;
        mKeyword = author;

        // get current user -> user id # 0
        // mUserId = ? éo biết lấy ở đâu

        doSearchBookWithAuthor();
    }

    @Override
    public void loadMoreBookWithAuthor() {

        // load more -> check listBook.size() < total result
        if (dataSearchBookByTitle.getTotalResult() <= (mPageBook * SIZE_OF_PAGE)) {
            return;
        }

        // tang page len 1
        mPageAuthor += 1;

        doSearchBookWithAuthor();
    }

    private void doSearchBookWithAuthor () {
        useCaseSearchBookByAuthor = useCaseFactory.searchBookByAuthor(mKeyword, mUserId, mPageAuthor, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    dataSearchBookByAuthor = data; // use data to process load more
                    resultSearchBookByAuthorSubject.onNext(data.getResultInfo());
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: searchBookWithAuthor _________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<BookResponse>> onSearchBookWithAuthorSuccess() {
        return resultSearchBookByAuthorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void loadHistorySearchUser() {
        if ( ! isCanLoadHistoryUser) {
            MZDebug.e("_____________________________________________ isCanLoadHistoryUser = false");
            return;
        }
        // get cached user data
        UseCase<User> loadLocalUser = useCaseFactory.getUser();
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("local login: " + user.isValid());
                    if (user.isValid()) {
                        doLoadHistorySearchUser();
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: doLoadHistorySearchUser : authentication _____________E: \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    private void doLoadHistorySearchUser () {
        useCaseHistorySearchUser = useCaseFactory.getUsersSearchedKeyword();
        useCaseHistorySearchUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryUser = false;
                    resultHistorySearchUserSubject.onNext(list);
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: doLoadHistorySearchUser : ____________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                }).execute();
    }

    @Override
    public Observable<List<String>> onLoadHistorySearchUserSuccess() {
        return resultHistorySearchUserSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void searchUserWithName(String name) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageUser = 1;
        mKeyword = name;

        // get current user -> user id # 0
        // userId = ?

        doSearchUserWithName();
    }

    @Override
    public void loadMoreUserWithName() {

        // load more -> check listBook.size() < total result
        if (dataSearchBookByTitle.getTotalResult() <= (mPageBook * SIZE_OF_PAGE)) {
            return;
        }

        mPageUser += 1;

        doSearchUserWithName();
    }

    private void doSearchUserWithName () {

        useCaseSearchUserByName = useCaseFactory.searchUser(mKeyword, mPageUser, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    dataSearchUserByName = data;
                    resultSearchUserByNameSubject.onNext(data.getResultInfo());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchUserWithName ___________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<List<UserResponse>> onSearchUserWithNameSuccess() {
        return resultSearchUserByNameSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }
}
