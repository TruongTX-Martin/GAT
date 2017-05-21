package com.gat.feature.editinfo;

import android.util.Log;

import com.gat.common.util.Strings;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.InterestCategory;

import java.util.List;

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
    private final Subject<String> editinfoResultSubject;
    private final Subject<EditInfoInput> editinfoInputSubject;
    private final Subject<ServerResponse<ResponseData>> editinfoError;
    private UseCase<String> editinfoUsecase;
    private UseCase<ServerResponse> updateCategoryUseCase;
    private UseCase<ServerResponse> updateLocationUseCase;

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
                    UserAddressData userAddressData = input.getAddressData();
                    List<Integer> categories = input.getCategories();
                    if (userAddressData != null && !Strings.isNullOrEmpty(userAddressData.address()))
                        updateLocation(input);
                    else if (categories != null && !categories.isEmpty()) {
                        updateCategories(input);
                    } else {
                        editinfoResultSubject.onNext(response);
                    }
                })
                .onError(throwable -> {
                    if (throwable instanceof LoginException)
                        editinfoError.onNext(ServerResponse.TOKEN_CHANGED);
                    else
                        editinfoError.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(
                        () -> editinfoUsecase = UseCases.release(editinfoUsecase)
                )
                .execute();
    }

    private void updateLocation(EditInfoInput infoInput) {
        UserAddressData userAddressData = infoInput.getAddressData();
        updateLocationUseCase = UseCases.release(updateLocationUseCase);

        updateLocationUseCase = useCaseFactory.updateLocation(userAddressData.address(), userAddressData.location());

        updateLocationUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    List<Integer> categories = infoInput.getCategories();
                    if (categories != null && !categories.isEmpty()) {
                        updateCategories(infoInput);
                    } else {
                        editinfoResultSubject.onNext(response.message());
                    }
                })
                .onError(throwable -> {
                    if (throwable instanceof LoginException)
                        editinfoError.onNext((ServerResponse.TOKEN_CHANGED));
                    else
                        editinfoError.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> updateLocationUseCase = UseCases.release(updateLocationUseCase))
                .execute();
    }

    private void updateCategories(EditInfoInput infoInput) {
        List<Integer> categories = infoInput.getCategories();
        updateCategoryUseCase = UseCases.release(updateCategoryUseCase);

        updateCategoryUseCase = useCaseFactory.updateCategories(categories);

        updateCategoryUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    editinfoResultSubject.onNext(response.message());
                })
                .onError(throwable -> {
                    if (throwable instanceof LoginException)
                        editinfoError.onNext((ServerResponse.TOKEN_CHANGED));
                    else
                        editinfoError.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> updateCategoryUseCase = UseCases.release(updateCategoryUseCase))
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
    public Observable<String> getResponseEditInfo() {
        return editinfoResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onErrorEditInfo() {
        return editinfoError.observeOn(schedulerFactory.main());
    }
}
