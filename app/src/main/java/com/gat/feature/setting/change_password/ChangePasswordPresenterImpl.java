package com.gat.feature.setting.change_password;

import android.text.TextUtils;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
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

    public ChangePasswordPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectSuccess = PublishSubject.create();
        subjectFailed = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void requestChangePassword(String oldPassword, String newPassword, String confirmPassword) {

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            subjectFailed.onNext("Bạn cần nhập đủ thông tin");
            return;
        }
        if (TextUtils.equals(newPassword, confirmPassword)) {
            subjectFailed.onNext("Mật khẩu nhập lại không khớp");
            return;
        }

        useCaseChangePassword = useCaseFactory.changeOldPassword(oldPassword, newPassword);
        useCaseChangePassword.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    subjectFailed.onNext("Failed");
                })
                .execute();
    }

    @Override
    public Observable<String> onChangePasswordSuccess() {
        return subjectSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onChangePasswordFailed() {
        return subjectFailed.subscribeOn(schedulerFactory.main());
    }


}
