package com.gat.feature.personal;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.UserInfo;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class PersonalPresenterImpl implements PersonalPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;

    private CompositeDisposable compositeDisposable;
    private final Subject<Data> personalResultSubject;
    private final Subject<String> personalDataSubject;

    private final Subject<ServerResponse<ResponseData>> errorSubject;
    private UseCase<Data> getPersonalUsecase;

    public PersonalPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        this.errorSubject = PublishSubject.create();
        personalResultSubject = PublishSubject.create();
        personalDataSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                personalDataSubject.observeOn(schedulerFactory.main()).subscribe(this::getPersonalData)
        );
        personalDataSubject.onNext("");
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }

    @Override
    public Observable<Data> getResponse() {
        return personalResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }
    private void getPersonalData(String input) {
        getPersonalUsecase = UseCases.release(getPersonalUsecase);
        getPersonalUsecase = useCaseFactory.getUserInfo();
        getPersonalUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    personalResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    errorSubject.onError(throwable);
                })
                .onStop(
                        () -> getPersonalUsecase = UseCases.release(getPersonalUsecase)
                )
                .execute();
    }
}
