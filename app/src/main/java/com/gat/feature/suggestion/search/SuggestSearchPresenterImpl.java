package com.gat.feature.suggestion.search;

import android.util.Log;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import java.util.List;
import io.reactivex.Observable;
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

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    // use case history search
    private UseCase<List<Keyword>> useCaseHistorySearchBook;
    private final Subject<List<Keyword>> resultHistorySearchBookSubject;

    private UseCase<List<Keyword>> useCaseHistorySearchAuthor;
    private final Subject<List<Keyword>> resultHistorySearchAuthorSubject;

    private UseCase<List<Keyword>> useCaseHistorySearchUser;
    private final Subject<List<Keyword>> resultHistorySearchUserSubject;

    // use case search
    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByTitle;
    private final Subject<List<BookResponse>> resultSearchBookByTitleSubject;
    private final Subject<List<BookResponse>> resultLoadMoreBookByTitleSubject;
    private final Subject<Boolean> subjectCanLoadMoreBookByTitle;

    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByAuthor;
    private final Subject<List<BookResponse>> resultSearchBookByAuthorSubject;
    private final Subject<List<BookResponse>> resultLoadMoreBookByAuthorSubject;
    private final Subject<Boolean> subjectCanLoadMoreBookByAuthor;

    private UseCase<DataResultListResponse<UserResponse>> useCaseSearchUserByName;
    private final Subject<List<UserResponse>> resultSearchUserByNameSubject;
    private final Subject<List<UserResponse>> resultLoadMoreUserByNameSubject;
    private final Subject<Boolean> subjectCanLoadMoreUserByName;

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
        subjectCanLoadMoreBookByTitle = PublishSubject.create();
        subjectCanLoadMoreBookByAuthor = PublishSubject.create();
        subjectCanLoadMoreUserByName = PublishSubject.create();
        resultLoadMoreBookByTitleSubject = PublishSubject.create();
        resultLoadMoreBookByAuthorSubject = PublishSubject.create();
        resultLoadMoreUserByNameSubject = PublishSubject.create();
    }

    @Override
    public void onCreate() {
        mPageAuthor = 1;
        mPageBook = 1;
        mPageUser = 1;

        UseCase<User> useCaseGetUser = useCaseFactory.getUser();
        useCaseGetUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                   mUserId = user.userId();
                })
                .onError(throwable -> {
                    mUserId = -1;
                }).execute();
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
    public Observable<List<Keyword>> onLoadHistorySearchBookSuccess() {
        return resultHistorySearchBookSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void searchBookWithTitle(String book_title) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageBook = 1;
        mKeyword = book_title;

        MZDebug.w("doSearchBookWithTitle, user id: " + mUserId);
        useCaseSearchBookByTitle = useCaseFactory.searchBookByTitle(mKeyword, mUserId, mPageBook, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchBookByTitleSubject.onNext(data.getResultInfo()); // pass response to view
                    if (data.getTotalResult() > mPageBook * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreBookByTitle.onNext(true);
                    }
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }

    @Override
    public void loadMoreBookWithTitle() {
        // thi page +1 va load tiep
        mPageBook += 1;

        useCaseSearchBookByTitle = useCaseFactory.searchBookByTitle(mKeyword, mUserId, mPageBook, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultLoadMoreBookByTitleSubject.onNext(data.getResultInfo()); // pass response to view
                    if (data.getTotalResult() > mPageBook * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreBookByTitle.onNext(true);
                    }
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }


    @Override
    public Observable<List<BookResponse>> onSearchBookWithTitleSuccess() {
        return resultSearchBookByTitleSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<List<BookResponse>> onLoadMoreBookWithTitleSuccess() {
        return resultLoadMoreBookByTitleSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreBookWithTitle() {
        return subjectCanLoadMoreBookByTitle.subscribeOn(schedulerFactory.main());
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
    public Observable<List<Keyword>> onLoadHistorySearchAuthorSuccess() {
        return resultHistorySearchAuthorSubject.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void searchBookWithAuthor(String author) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageAuthor = 1;
        mKeyword = author;

        useCaseSearchBookByAuthor = useCaseFactory.searchBookByAuthor(mKeyword, mUserId, mPageAuthor, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchBookByAuthorSubject.onNext(data.getResultInfo());

                    if (data.getTotalResult() > mPageAuthor * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreBookByAuthor.onNext(true);
                    }
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: searchBookWithAuthor _________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }

    @Override
    public void loadMoreBookWithAuthor() {

        // tang page len 1
        mPageAuthor += 1;

        useCaseSearchBookByAuthor = useCaseFactory.searchBookByAuthor(mKeyword, mUserId, mPageAuthor, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultLoadMoreBookByAuthorSubject.onNext(data.getResultInfo());

                    if (data.getTotalResult() > mPageAuthor * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreBookByAuthor.onNext(true);
                    }
                })
                .onError( throwable -> {
                    MZDebug.e("ERROR: searchBookWithAuthor _________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }


    @Override
    public Observable<List<BookResponse>> onSearchBookWithAuthorSuccess() {
        return resultSearchBookByAuthorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<List<BookResponse>> onLoadMoreBookWithAuthorSuccess() {
        return resultLoadMoreBookByAuthorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreBookWithAuthor() {
        return subjectCanLoadMoreBookByAuthor.subscribeOn(schedulerFactory.main());
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
    public Observable<List<Keyword>> onLoadHistorySearchUserSuccess() {
        return resultHistorySearchUserSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void searchUserWithName(String name) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageUser = 1;
        mKeyword = name;

        useCaseSearchUserByName = useCaseFactory.searchUser(mKeyword, mPageUser, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchUserByNameSubject.onNext(data.getResultInfo());

                    if (data.getTotalResult() > mPageUser * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreUserByName.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchUserWithName ___________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }

    @Override
    public void loadMoreUserWithName() {
        mPageUser += 1;

        useCaseSearchUserByName = useCaseFactory.searchUser(mKeyword, mPageUser, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultLoadMoreUserByNameSubject.onNext(data.getResultInfo());

                    if (data.getTotalResult() > mPageUser * SIZE_OF_PAGE ) {
                        subjectCanLoadMoreUserByName.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchUserWithName ___________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    errorSubject.onNext("Không thể tìm kiếm, có lỗi xảy ra.");
                })
                .execute();
    }

    @Override
    public Observable<List<UserResponse>> onSearchUserWithNameSuccess() {
        return resultSearchUserByNameSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<List<UserResponse>> onLoadMoreUserWithNameSuccess() {
        return resultLoadMoreUserByNameSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreUserWithName() {
        return subjectCanLoadMoreUserByName.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }
}
