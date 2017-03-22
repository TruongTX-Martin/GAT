package com.gat.feature.register.update.location;

import android.util.Log;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/19/17.
 */

public class AddLocationPresenterImpl implements AddLocationPresenter {
    private final String TAG = AddLocationPresenterImpl.class.getSimpleName();

    // For update location
    private final Subject<ServerResponse> updateResultSubject;

    private final Subject<UserAddressData> updateLocationDataSubject;

    private UseCase<ServerResponse> updateLocationUseCase;

    private final Subject<ServerResponse<ResponseData>> errorSubject;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private CompositeDisposable compositeDisposable;

    public AddLocationPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        this.updateResultSubject = PublishSubject.create();
        this.updateLocationDataSubject = BehaviorSubject.create();

        this.errorSubject = PublishSubject.create();
    }
    @Override
    public void setLocation(UserAddressData location) {
        updateLocationDataSubject.onNext(location);
    }

    @Override
    public Observable<ServerResponse> updateResult() {
        return updateResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                updateLocationDataSubject.observeOn(schedulerFactory.main())
                        .subscribe(this::updateLocation)
        );
    }

    @Override
    public void onDestroy() {
        updateLocationUseCase = UseCases.release(updateLocationUseCase);
        compositeDisposable.dispose();
    }

    private void updateLocation(UserAddressData userAddressData) {
        Log.d(TAG, "updateLocation ");

        updateLocationUseCase = UseCases.release(updateLocationUseCase);

        updateLocationUseCase = useCaseFactory.updateLocation(userAddressData.address(), userAddressData.location());

        updateLocationUseCase.executeOn(schedulerFactory.io())
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
                .onStop(() -> updateLocationUseCase = UseCases.release(updateLocationUseCase))
                .execute();
    }
}
