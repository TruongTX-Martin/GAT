package com.gat.feature.setting.change_password;

import android.text.TextUtils;

import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mozaa on 05/05/2017.
 */

public class ChangePasswordPresenterImpl implements ChangePasswordPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;


    private UseCase<ServerResponse> useCaseChangePassword;
    private final Subject<String> subjectSuccess;
    private final Subject<String> subjectFailed;
    private final Subject<String> subjectUnAuthorization;
    private final Subject<Boolean> subjectWhichShowOrHideProgress;

    public ChangePasswordPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectSuccess = PublishSubject.create();
        subjectFailed = PublishSubject.create();
        subjectWhichShowOrHideProgress = PublishSubject.create();
        subjectUnAuthorization = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        useCaseChangePassword = UseCases.release(useCaseChangePassword);
    }

    @Override
    public void requestChangePassword(String oldPassword, String newPassword, String confirmPassword) {

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            subjectFailed.onNext("Bạn cần nhập đủ thông tin");
            return;
        }
        if ( ! TextUtils.equals(newPassword, confirmPassword)) {
            subjectFailed.onNext("Mật khẩu nhập lại không khớp");
            return;
        }

        subjectWhichShowOrHideProgress.onNext(true);
        useCaseChangePassword = useCaseFactory.changeOldPassword(oldPassword, newPassword);
        useCaseChangePassword.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectWhichShowOrHideProgress.onNext(true);
                    subjectSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    subjectWhichShowOrHideProgress.onNext(false);

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else if (throwable instanceof CommonException){
                        subjectFailed.onNext( throwable.getMessage() );
                    } else {
                        subjectFailed.onNext( ServerResponse.EXCEPTION.message() );
                    }

                })
                .execute();
    }

    @Override
    public Observable<String> onChangePasswordSuccess() {
        return subjectSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectUnAuthorization.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onChangePasswordFailed() {
        return subjectFailed.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onShowOrHideProgress() {
        return subjectWhichShowOrHideProgress.subscribeOn(schedulerFactory.main());
    }


}
