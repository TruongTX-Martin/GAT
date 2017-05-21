package com.gat.feature.suggestion.search;

import android.util.Log;
import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
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
    private final Subject<DataResultListResponse<BookResponse>> resultSearchBookByTitleSubject;
    private final Subject<DataResultListResponse<BookResponse>> resultLoadMoreBookByTitleSubject;
    private final Subject<Boolean> subjectCanLoadMoreBookByTitle;

    private UseCase<DataResultListResponse> useCaseSearchBookByTitleTotalResult;
    private final Subject<Integer> subjectBookByTitleTotalResult;

    private UseCase<DataResultListResponse<BookResponse>> useCaseSearchBookByAuthor;
    private final Subject<DataResultListResponse<BookResponse>> resultSearchBookByAuthorSubject;
    private final Subject<DataResultListResponse<BookResponse>> resultLoadMoreBookByAuthorSubject;
    private final Subject<Boolean> subjectCanLoadMoreBookByAuthor;

    private UseCase<DataResultListResponse> useCaseSearchBookByAuthorTotalResult;
    private final Subject<Integer> subjectBookByAuthorTotalResult;

    private UseCase<DataResultListResponse<UserResponse>> useCaseSearchUserByName;
    private final Subject<DataResultListResponse<UserResponse>> resultSearchUserByNameSubject;
    private final Subject<DataResultListResponse<UserResponse>> resultLoadMoreUserByNameSubject;
    private final Subject<Boolean> subjectCanLoadMoreUserByName;

    private UseCase<DataResultListResponse> useCaseSearchUserByNameTotalResult;
    private final Subject<Integer> subjectUserByNameTotalResult;

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
        subjectBookByTitleTotalResult = PublishSubject.create();
        subjectBookByAuthorTotalResult = PublishSubject.create();
        subjectUserByNameTotalResult = PublishSubject.create();
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
    public void onDestroy() {
    }


    @Override
    public void loadHistorySearchBook() {
        if (!isCanLoadHistoryBook) {
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

    private void doLoadHistorySearchBook() {
        useCaseHistorySearchBook = useCaseFactory.getBooksSearchedKeyword();
        useCaseHistorySearchBook.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryBook = false;
                    resultHistorySearchBookSubject.onNext(list);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ____________________________________E: "
                            + Log.getStackTraceString(throwable));
                }).execute();
    }


    @Override
    public Observable<List<Keyword>> onLoadHistorySearchBookSuccess() {
        return resultHistorySearchBookSubject.subscribeOn(schedulerFactory.main());
    }

    private int totalResultBookWithTitle = 0; // use for load more

    @Override
    public void searchBookWithTitle(String book_title) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageBook = 1;
        mKeyword = book_title;
        totalResultBookWithTitle = 0;

        useCaseSearchBookByTitle = useCaseFactory.searchBookByTitle(mKeyword, mPageBook, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchBookByTitleSubject.onNext(data); // pass response to view
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchBookWithTitle __________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();

        useCaseSearchBookByTitleTotalResult = useCaseFactory.searchBookByTitleTotal(book_title, mUserId)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    subjectBookByTitleTotalResult.onNext(data.getTotalResult());

                    totalResultBookWithTitle = data.getTotalResult();
                    if (totalResultBookWithTitle > mPageBook * SIZE_OF_PAGE) {
                        subjectCanLoadMoreBookByTitle.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: useCaseSearchBookByTitleTotalResult __________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectBookByTitleTotalResult.onNext(-1); // sẽ hiển thị text mặc định, ko có số
                })
                .execute();
    }

    @Override
    public void loadMoreBookWithTitle() {
        // thi page +1 va load tiep
        mPageBook += 1;

        useCaseSearchBookByTitle = useCaseFactory.searchBookByTitle(mKeyword, mPageBook, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultLoadMoreBookByTitleSubject.onNext(data); // pass response to view

                    if (totalResultBookWithTitle > mPageBook * SIZE_OF_PAGE) {
                        subjectCanLoadMoreBookByTitle.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchBook ________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();
    }


    @Override
    public Observable<DataResultListResponse<BookResponse>> onSearchBookWithTitleSuccess() {
        return resultSearchBookByTitleSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> onLoadMoreBookWithTitleSuccess() {
        return resultLoadMoreBookByTitleSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreBookWithTitle() {
        return subjectCanLoadMoreBookByTitle.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Integer> onSearchBookWithTitleTotalResult() {
        return subjectBookByTitleTotalResult.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void loadHistorySearchAuthor() {
        if (!isCanLoadHistoryAuthor) {
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

    private void doLoadHistorySearchAuthor() {
        useCaseHistorySearchAuthor = useCaseFactory.getAuthorsSearchedKeyword();
        useCaseHistorySearchAuthor.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryAuthor = false;
                    resultHistorySearchAuthorSubject.onNext(list);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadHistorySearchAuthor ______________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                }).execute();
    }

    @Override
    public Observable<List<Keyword>> onLoadHistorySearchAuthorSuccess() {
        return resultHistorySearchAuthorSubject.subscribeOn(schedulerFactory.main());
    }


    private int totalResultBookWithAuthor = 0; // use for load more

    @Override
    public void searchBookWithAuthor(String author) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageAuthor = 1;
        mKeyword = author;
        totalResultBookWithAuthor = 0;

        useCaseSearchBookByAuthor = useCaseFactory.searchBookByAuthor(mKeyword, mPageAuthor, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchBookByAuthorSubject.onNext(data);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchBookWithAuthor _________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();

        useCaseSearchBookByAuthorTotalResult = useCaseFactory.searchBookByTitleTotal(author, mUserId)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    subjectBookByAuthorTotalResult.onNext(data.getTotalResult());

                    totalResultBookWithAuthor = data.getTotalResult();
                    if (totalResultBookWithAuthor > mPageAuthor * SIZE_OF_PAGE) {
                        subjectCanLoadMoreBookByAuthor.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: useCaseSearchBookByTitleTotalResult __________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectBookByAuthorTotalResult.onNext(-1); // sẽ hiển thị text mặc định, ko có số
                })
                .execute();
    }

    @Override
    public void loadMoreBookWithAuthor() {

        // tang page len 1
        mPageAuthor += 1;

        useCaseSearchBookByAuthor = useCaseFactory.searchBookByAuthor(mKeyword, mPageAuthor, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultLoadMoreBookByAuthorSubject.onNext(data);

                    if (totalResultBookWithAuthor > mPageAuthor * SIZE_OF_PAGE) {
                        subjectCanLoadMoreBookByAuthor.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchBookWithAuthor _________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();
    }


    @Override
    public Observable<DataResultListResponse<BookResponse>> onSearchBookWithAuthorSuccess() {
        return resultSearchBookByAuthorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> onLoadMoreBookWithAuthorSuccess() {
        return resultLoadMoreBookByAuthorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreBookWithAuthor() {
        return subjectCanLoadMoreBookByAuthor.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Integer> onSearchBookWithAuthorTotalResult() {
        return subjectBookByAuthorTotalResult.subscribeOn(schedulerFactory.main());
    }


    @Override
    public void loadHistorySearchUser() {
        if (!isCanLoadHistoryUser) {
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

    private void doLoadHistorySearchUser() {
        useCaseHistorySearchUser = useCaseFactory.getUsersSearchedKeyword();
        useCaseHistorySearchUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(list -> {
                    isCanLoadHistoryUser = false;
                    resultHistorySearchUserSubject.onNext(list);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: doLoadHistorySearchUser : ____________________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                }).execute();
    }

    @Override
    public Observable<List<Keyword>> onLoadHistorySearchUserSuccess() {
        return resultHistorySearchUserSubject.subscribeOn(schedulerFactory.main());
    }

    private int totalResultUserWithName = 0; // use for load more

    @Override
    public void searchUserWithName(String name) {
        // user press 'search' -> page = 1 && keyword = new keyword
        mPageUser = 1;
        mKeyword = name;
        totalResultUserWithName = 0;

        useCaseSearchUserByName = useCaseFactory.searchUser(mKeyword, mPageUser, SIZE_OF_PAGE)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultSearchUserByNameSubject.onNext(data);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchUserWithName ___________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();


        useCaseSearchUserByNameTotalResult = useCaseFactory.searchBookByTitleTotal(name, mUserId)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    subjectUserByNameTotalResult.onNext(data.getTotalResult());

                    totalResultUserWithName = data.getTotalResult();
                    if (totalResultUserWithName > mPageUser * SIZE_OF_PAGE) {
                        subjectCanLoadMoreUserByName.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: useCaseSearchBookByTitleTotalResult __________________E: \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectUserByNameTotalResult.onNext(-1); // sẽ hiển thị text mặc định, ko có số
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
                    resultLoadMoreUserByNameSubject.onNext(data);

                    if (totalResultUserWithName > mPageUser * SIZE_OF_PAGE) {
                        subjectCanLoadMoreUserByName.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: searchUserWithName ___________________________________E: \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof CommonException) {
                        errorSubject.onNext(throwable.getMessage());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                    }
                })
                .execute();
    }

    @Override
    public Observable<DataResultListResponse<UserResponse>> onSearchUserWithNameSuccess() {
        return resultSearchUserByNameSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<DataResultListResponse<UserResponse>> onLoadMoreUserWithNameSuccess() {
        return resultLoadMoreUserByNameSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMoreUserWithName() {
        return subjectCanLoadMoreUserByName.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Integer> onSearchUserWithNameTotalResult() {
        return subjectUserByNameTotalResult.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }
}
