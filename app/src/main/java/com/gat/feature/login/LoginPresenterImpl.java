package com.gat.feature.login;

import android.util.Log;

import com.gat.common.util.Strings;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 2/23/17.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private static final String TAG = LoginPresenterImpl.class.getSimpleName();

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    // For get user from login result
    private final Subject<User> loginResultSubject;
    private final Subject<LoginData> loginDataSubject;
    private UseCase<User> loginUseCase;

    // For send email to reset password
    private final Subject<String> emailResetSubject;
    private final Subject<ServerResponse<ResetPasswordResponseData>> emailResetResultSubject;
    private UseCase<ServerResponse<ResetPasswordResponseData>> emailResetUseCase;

    // For verify token
    private final Subject<String> resetTokenSubject;
    private final Subject<ServerResponse<VerifyTokenResponseData>> verifyTokenResultSubject;
    private UseCase<ServerResponse<VerifyTokenResponseData>> verifyTokenUseCase;

    // For change password
    private final Subject<String> passwordSubject;
    private final Subject<ServerResponse<LoginResponseData>> changePasswordResultSubject;
    private UseCase<ServerResponse<LoginResponseData>> changePasswordUseCase;

    private UseCase<LoginData> loginDataUseCase;
    private final Subject<LoginData> localLoginData;

    private UseCase<User> loadLocalUserUseCase;
    private Subject<Integer> loadLocalUserSubject;

    private final Subject<ServerResponse<ResponseData>> errorSubject;

    // Disposable to store and release observale when it done
    private CompositeDisposable loginDisposable;

    private boolean isInitialized = false;

    /**
     * Constructor
     * @param useCaseFactory
     * @param schedulerFactory
     */
    public LoginPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.loginResultSubject = PublishSubject.create();
        this.loginDataSubject = BehaviorSubject.create();

        this.passwordSubject = BehaviorSubject.create();
        this.changePasswordResultSubject = PublishSubject.create();

        this.emailResetSubject = BehaviorSubject.create();
        this.emailResetResultSubject = PublishSubject.create();

        this.resetTokenSubject = BehaviorSubject.create();
        this.verifyTokenResultSubject = PublishSubject.create();

        loadLocalUserSubject = BehaviorSubject.create();

        this.localLoginData = BehaviorSubject.create();

        this.errorSubject = PublishSubject.create();
    }

    /**
     * Create Presenter
     */
    @Override
    public void onCreate() {
        // Start subscribe login data
        init();
        Log.d(TAG, "IsCreated!");
    }

    /**
     * Destroy presenter
     */
    @Override
    public void onDestroy() {
        // Release all disposable to avoid leak
        loginUseCase = UseCases.release(loginUseCase);
        emailResetUseCase = UseCases.release(emailResetUseCase);
        verifyTokenUseCase = UseCases.release(verifyTokenUseCase);
        changePasswordUseCase = UseCases.release(changePasswordUseCase);

        loginDisposable.dispose();

        isInitialized = false;
        Log.d(TAG, "IsDestroyed!");
    }

    @Override
    public void init() {
        if (!isInitialized) {
            loginDisposable = new CompositeDisposable(
                    emailResetSubject.observeOn(schedulerFactory.main())
                            .subscribe(email -> sendRequest(email)),
                    resetTokenSubject.observeOn(schedulerFactory.main())
                            .subscribe(token -> sendToken(token)),
                    passwordSubject.observeOn(schedulerFactory.main())
                            .subscribe(password -> sendPassword(password)),
                    loginDataSubject.observeOn(schedulerFactory.main())
                            .filter(loginData -> checkLoginData(loginData))
                            .subscribe(loginData -> login(loginData)),
                    loadLocalUserSubject.observeOn(schedulerFactory.main())
                            .subscribe(input -> loadLocalUser(input))
            );
            isInitialized = true;
            Log.d(TAG, "IsInitialized");
        }
    }

    @Override
    public Observable<ServerResponse<ResponseData>> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }
    /**
     * Result of login, view will subscribe it to get user
     * @return
     */
    @Override
    public Observable<User> loginResult() {
        getLoginData();
        return loginResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public void login() {
        login(LoginData.EMPTY);
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> resetPasswordResponse() {
        return emailResetResultSubject;
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> verifyResult() {
        return verifyTokenResultSubject;
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> changePasswordResult() {
        return changePasswordResultSubject;
    }

    @Override
    public void sendRequestReset(String email) {
        Log.d(TAG, "email: " + email);
        if (!isInitialized) init();
        emailResetSubject.onNext(email);
    }

    @Override
    public void verifyToken(String token) {
        Log.d(TAG, "token: " + token);
        if (!isInitialized) init();
        resetTokenSubject.onNext(token);
    }

    @Override
    public void changePassword(String password) {
        Log.d(TAG, "password: " + password);
        if (!isInitialized) init();
        passwordSubject.onNext(password);
    }

    @Override
    public void loadLocalUser() {
        loadLocalUserSubject.onNext(1);
    }

    private void loadLocalUser(int input) {
        loadLocalUserUseCase = UseCases.release(loadLocalUserUseCase);

        loadLocalUserUseCase = useCaseFactory.getUser();

        loadLocalUserUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    loginResultSubject.onNext(user);
                })
                .onError(throwable -> {
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> loadLocalUserUseCase = UseCases.release(loadLocalUserUseCase))
                .execute();
    }

    @Override
    public Observable<LoginData> loadLocalLoginData() {
        return localLoginData.observeOn(schedulerFactory.main());
    }

    /**
     * Pass data from view to domain
     * @param loginData
     */
    @Override
    public void setIdentity(LoginData loginData) {
        Log.d(TAG, "SetId " + loginData.type() );
        this.loginDataSubject.onNext(loginData);
    }

    private void sendRequest(String email) {
        Log.d(TAG, "sendRequest " + email);
        emailResetUseCase = UseCases.release(emailResetUseCase);

        UseCase<ServerResponse> emailResetResponse = useCaseFactory.sendRequestResetPassword(email);

        emailResetResponse.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    if (response.code() == ServerResponse.HTTP_CODE.OK) {
                        emailResetResultSubject.onNext(response);
                    } else {
                        errorSubject.onNext(ServerResponse.BAD_RESPONSE);
                    }
                })
                .onError(throwable -> {
                    //throwable.printStackTrace();
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> emailResetUseCase = UseCases.release(emailResetUseCase))
                .execute();
    }

    private void sendToken(String token) {
        Log.d(TAG, "sendToken " + token);

        verifyTokenUseCase = UseCases.release(verifyTokenUseCase);

        UseCase<ServerResponse> verifyTokenResponse = useCaseFactory.verifyResetToken(token);

        verifyTokenResponse.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    if(response.code() == ServerResponse.HTTP_CODE.OK) {
                        verifyTokenResultSubject.onNext(response);
                    } else {
                        errorSubject.onNext(ServerResponse.BAD_RESPONSE);
                    }
                })
                .onError(throwable -> {
                    //throwable.printStackTrace();
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> verifyTokenUseCase = UseCases.release(verifyTokenUseCase))
                .execute();
    }

    private void sendPassword(String password) {
        Log.d(TAG, "sendPassword " + password);

        changePasswordUseCase = UseCases.release(changePasswordUseCase);

        UseCase<ServerResponse> changePasswordResponse = useCaseFactory.resetPassword(password);

        changePasswordResponse.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    if (response.isOk()) {
                        changePasswordResultSubject.onNext(response);
                    } else {
                        errorSubject.onNext(ServerResponse.BAD_RESPONSE);
                    }
                })
                .onError(throwable -> {
                    //throwable.printStackTrace();
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> changePasswordUseCase = UseCases.release(changePasswordUseCase))
                .execute();
    }

    private void login(LoginData loginData) {

        Log.d(TAG, "login " + loginData.type());

        // Release usecase
        loginUseCase = UseCases.release(loginUseCase);

        UseCase<User> loginResult = useCaseFactory.login(loginData);

        loginResult.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    loginResultSubject.onNext(result);
                })
                .onError(throwable -> {
                    if (throwable instanceof LoginException) {
                        errorSubject.onNext(((LoginException)throwable).responseData());
                    } else {
                        errorSubject.onNext(ServerResponse.EXCEPTION);
                    }
                })
                .onStop(() -> loginUseCase = UseCases.release(loginUseCase))
                .execute();
    }



    private void getLoginData() {
        Log.d(TAG, "getLoginData ");

        // Release usecase
        loginDataUseCase = UseCases.release(loginDataUseCase);

        UseCase<LoginData> loginData = useCaseFactory.getLoginData();

        loginData.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    localLoginData.onNext(result);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    //loginResultSubject.onError(/*new LoginException()*/throwable);
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .onStop(() -> loginDataUseCase = UseCases.release(loginDataUseCase))
                .execute();
    }




    private boolean checkLoginEmail(EmailLoginData loginData) {
        return !(Strings.isNullOrEmpty(loginData.email())
                || Strings.isNullOrEmpty(loginData.password())
        );
    }

    private boolean checkLoginBySocial(SocialLoginData loginData) {
        return !Strings.isNullOrEmpty(loginData.socialID());
    }

    private Boolean checkLoginData(LoginData loginData) {
        Boolean ret = true;
        if (loginData.type() == LoginData.Type.EMAIL) {
            ret = checkLoginEmail((EmailLoginData)loginData);
        } else if (loginData.type() == LoginData.Type.FACE
        || loginData.type() == LoginData.Type.GOOGLE
        || loginData.type() == LoginData.Type.TWITTER) {
            ret = checkLoginBySocial((SocialLoginData) loginData);
        }
        return ret;
    }
}
