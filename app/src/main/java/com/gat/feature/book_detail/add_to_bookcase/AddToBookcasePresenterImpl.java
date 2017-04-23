package com.gat.feature.book_detail.add_to_bookcase;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/22/2017.
 */

public class AddToBookcasePresenterImpl implements AddToBookcasePresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<BookInstanceInfo> bookInstanceInfoUseCase;
    private final Subject<BookInstanceInfo> subjectBookTotalInstance;

    private UseCase<ServerResponse> addBookInstanceUseCase;
    private final Subject<String> subjectAddBookInstance;

    private int mEditionId;

    public AddToBookcasePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectBookTotalInstance = PublishSubject.create();
        subjectAddBookInstance = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setEditionId(int editionId) {
        mEditionId = editionId;
    }

    @Override
    public void getBookTotalInstance() {
        bookInstanceInfoUseCase = useCaseFactory.getSelfInstanceInfo(mEditionId);
        bookInstanceInfoUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(bookInstanceInfo -> {
                    subjectBookTotalInstance.onNext(bookInstanceInfo);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: getBookTotalInstance _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                }).execute();

    }

    @Override
    public Observable<BookInstanceInfo> onGetBookTotalInstanceSuccess() {
        return subjectBookTotalInstance.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void addBookInstance(int sharingStatus, int numberOfBook) {
        addBookInstanceUseCase = useCaseFactory.selfAddInstance(mEditionId, sharingStatus, numberOfBook);
        addBookInstanceUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectAddBookInstance.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: addBookInstance ______________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<String> onAddBookInstanceSuccess() {
        return subjectAddBookInstance.subscribeOn(schedulerFactory.main());
    }


}
