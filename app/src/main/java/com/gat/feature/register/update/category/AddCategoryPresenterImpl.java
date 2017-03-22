package com.gat.feature.register.update.category;

import android.util.Log;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/19/17.
 */

public class AddCategoryPresenterImpl implements AddCategoryPresenter {
    private final String TAG = AddCategoryPresenterImpl.class.getSimpleName();
    // For update location
    private final Subject<ServerResponse> updateResultSubject;

    private final Subject<List<Integer>> updateCategoryDataSubject;

    private UseCase<ServerResponse> updateCategoryUseCase;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;


    private final Subject<ServerResponse<ResponseData>> errorSubject;

    private Disposable disposable;

    public AddCategoryPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.updateResultSubject = PublishSubject.create();
        this.updateCategoryDataSubject = BehaviorSubject.create();

        this.errorSubject = PublishSubject.create();
    }

    @Override
    public void setCategories(List<Integer> categories) {
        Log.d(TAG, "setCategories:"+Integer.toString(categories.size()));
        updateCategoryDataSubject.onNext(categories);
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse> updateResult() {
        return updateResultSubject.observeOn(schedulerFactory.main());
    }

    private void updateCategories(List<Integer> categories) {
        Log.d(TAG, "updateCategories ");

        updateCategoryUseCase = UseCases.release(updateCategoryUseCase);

        updateCategoryUseCase = useCaseFactory.updateCategories(categories);

        updateCategoryUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    if (response.isOk())
                        updateResultSubject.onNext(response);
                    else
                        errorSubject.onNext(new ServerResponse<ResponseData>(response.message(), response.code(), ResponseData.NO_RESPONSE));
                })
                .onError(throwable -> {
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> updateCategoryUseCase = UseCases.release(updateCategoryUseCase))
                .execute();
    }

    @Override
    public void onCreate() {
        disposable = updateCategoryDataSubject.observeOn(schedulerFactory.main())
                .subscribe(this::updateCategories);
    }

    @Override
    public void onDestroy() {
        updateCategoryUseCase = UseCases.release(updateCategoryUseCase);
        disposable.dispose();
    }
}
