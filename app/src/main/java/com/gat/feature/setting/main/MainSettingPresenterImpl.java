package com.gat.feature.setting.main;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.User;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 5/5/2017.
 */

public class MainSettingPresenterImpl implements MainSettingPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private final Subject<User> subjectLoadUserSuccess;

    public MainSettingPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectLoadUserSuccess = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void loadUserInfo() {
        UseCase<User> loadUser = useCaseFactory.getUser();
        loadUser.executeOn(schedulerFactory.io())
                .executeOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("loadUserInfo SUCCESS : \n\r " + user.toString());
                    subjectLoadUserSuccess.onNext(user);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadUserInfo : _______________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    @Override
    public Observable<User> onUserInfoSuccess() {
        return subjectLoadUserSuccess.subscribeOn(schedulerFactory.main());
    }
}
