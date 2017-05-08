package com.gat.feature.register;

import android.util.Log;

import com.gat.common.util.Strings;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.user.UserAddressData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 2/26/17.
 */

public class RegisterPresenterImpl implements RegisterPresenter {
    private static final String TAG = "RegisterPresenterDebug";

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    // For get user from login result
    private final Subject<User> registerResultSubject;
    // For pass data from domain to view
    private final Subject<LoginData> registerDataSubject;
    // Use case that start the work
    private UseCase<User> registerUseCase;


    private final Subject<String> errorSubject;

    // Disposable to store and release observale when it done
    private CompositeDisposable registerDisposable;

    /**
     * Constructor
     * @param useCaseFactory
     * @param schedulerFactory
     */
    public RegisterPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.registerResultSubject = PublishSubject.create();
        this.registerDataSubject = BehaviorSubject.create();

        this.errorSubject = PublishSubject.create();
    }

    @Override
    public Observable<User> getResponse() {
        return registerResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public void setIdentity(LoginData registerData) {
        Log.d(TAG, "SetId " + registerData.type() );
        registerDataSubject.onNext(registerData);
    }

    @Override
    public void onCreate() {
        registerDisposable = new CompositeDisposable(
                registerDataSubject.observeOn(schedulerFactory.main())
                    .filter(this::checkRegisterData)
                    .subscribe(this::register)
        );
    }


    @Override
    public void onDestroy() {
        // Release all disposable to avoid leak
        registerUseCase = UseCases.release(registerUseCase);
        registerDisposable.dispose();

    }


    @Override
    public Observable<String> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }

    private void register(LoginData registerData) {
        Log.d(TAG, "register " + registerData.type());

        registerUseCase = UseCases.release(registerUseCase);

        registerUseCase = useCaseFactory.register(registerData);

        registerUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    registerResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    if (throwable instanceof CommonException)
                        errorSubject.onNext(((CommonException)throwable).getMessage());
                    else
                        errorSubject.onNext(ServerResponse.EXCEPTION.message());
                })
                .onStop(() -> registerUseCase = UseCases.release(registerUseCase))
                .execute();
    }

    private boolean checkRegisterByEmail(EmailLoginData registerData) {
        return !(Strings.isNullOrEmpty(registerData.email())
                || Strings.isNullOrEmpty(registerData.password())
                || Strings.isNullOrEmpty(registerData.name())
        );
    }

    private boolean checkRegisterBySocial(SocialLoginData registerData) {
        return !(Strings.isNullOrEmpty(registerData.socialID())
                || Strings.isNullOrEmpty(registerData.name())

        );
    }

    private Boolean checkRegisterData(LoginData registerData) {
        Boolean ret = true;
        if (registerData.type() == LoginData.Type.EMAIL) {
            ret = checkRegisterByEmail((EmailLoginData)registerData);
        } else if (registerData.type() == LoginData.Type.FACE
                || registerData.type() == LoginData.Type.GOOGLE
                || registerData.type() == LoginData.Type.TWITTER) {
            ret = checkRegisterBySocial((SocialLoginData) registerData);
        }
        return ret;
    }
}
