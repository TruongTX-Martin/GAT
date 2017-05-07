package com.gat.feature.setting.add_email_password;

import android.text.TextUtils;

import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.FirebasePassword;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 5/5/2017.
 */

public class AddEmailPasswordPresenterImpl implements AddEmailPasswordPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<ServerResponse<FirebasePassword>> useCaseAddEmailPassword;
    private final Subject<String> subjectSuccess;
    private final Subject<String> subjectFailed;
    private final Subject subjectEmailEmpty;
    private final Subject subjectPasswordEmpty;

    public AddEmailPasswordPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectSuccess = PublishSubject.create();
        subjectFailed = PublishSubject.create();
        subjectEmailEmpty = PublishSubject.create();
        subjectPasswordEmpty = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void requestAddEmailPassword(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            subjectEmailEmpty.onComplete();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            subjectPasswordEmpty.onComplete();
            return;
        }

        useCaseAddEmailPassword = useCaseFactory.addEmailPassword(email, password);
        useCaseAddEmailPassword.executeOn(schedulerFactory.io())
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
    public Observable<String> onSuccess() {
        return subjectSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onFailed() {
        return subjectFailed.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable onEmailEmpty() {
        return subjectEmailEmpty.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable onPasswordEmpty() {
        return subjectPasswordEmpty.subscribeOn(schedulerFactory.main());
    }


}
