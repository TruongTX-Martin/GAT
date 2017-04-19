package com.gat.feature.editinfo;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by root on 18/04/2017.
 */

public class EditInfoPresenterImpl implements EditInfoPresenter {

    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;


    //edit info
    private CompositeDisposable editinfoDisposable;
    private final Subject<Data> editinfoResultSubject;
    private final Subject<EditInfoInput> editinfoInputSubject;
    private final Subject<ServerResponse<ResponseData>> editinfoError;
    private UseCase<Data> editinfoUsecase;

    public EditInfoPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory factory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = factory;

        //edit info
        this.editinfoError = PublishSubject.create();
        editinfoResultSubject = PublishSubject.create();
        editinfoInputSubject = BehaviorSubject.create();
    }

    @Override
    public void onCreate() {
        editinfoDisposable = new CompositeDisposable(
                editinfoInputSubject.observeOn(schedulerFactory.main()).subscribe(this::editInfo)
        );
    }


    private void editInfo(EditInfoInput input){
        editinfoUsecase = UseCases.release(editinfoUsecase);
        editinfoUsecase = useCaseFactory.updateInfo(input);
        editinfoUsecase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    editinfoResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    editinfoError.onError(throwable);
                })
                .onStop(
                        () -> editinfoUsecase = UseCases.release(editinfoUsecase)
                )
                .execute();
    }
    @Override
    public void onDestroy() {
        editinfoDisposable.dispose();
    }

    @Override
    public void requestEditInfo(EditInfoInput input) {
        editinfoInputSubject.onNext(input);
    }

    @Override
    public Observable<Data> getResponseEditInfo() {
        return editinfoResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorEditInfo() {
        return editinfoError.observeOn(schedulerFactory.main());
    }
}
