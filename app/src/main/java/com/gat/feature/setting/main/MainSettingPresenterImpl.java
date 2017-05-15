package com.gat.feature.setting.main;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.setting.SocialType;
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

    private UseCase<ServerResponse> useCaseLinkSocialAccount;
    private final Subject<String> subjectFacebookSuccess;
    private final Subject<String> subjectGoogleSuccess;
    private final Subject<String> subjectTwitterSuccess;
    private final Subject<Boolean> subjectSignOut;

    private final Subject<String> subjectOnError;

    public MainSettingPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectLoadUserSuccess = PublishSubject.create();
        subjectFacebookSuccess = PublishSubject.create();
        subjectGoogleSuccess = PublishSubject.create();
        subjectTwitterSuccess = PublishSubject.create();
        subjectSignOut = PublishSubject.create();
        subjectOnError = PublishSubject.create();
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
                .returnOn(schedulerFactory.main())
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

    @Override
    public void requestConnectSocial(String social_id, String social_name, int type) {
        useCaseLinkSocialAccount = useCaseFactory.linkSocialAccount(social_id, social_name, type);
        useCaseLinkSocialAccount.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {

                    switch (type) {
                        case SocialType.FACEBOOK:
                            subjectFacebookSuccess.onNext(social_name);
                            break;
                        case SocialType.GOOGLE:
                            subjectGoogleSuccess.onNext(social_name);
                            break;
                        case SocialType.TWITTER:
                            subjectTwitterSuccess.onNext(social_name);
                            break;
                    }

                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: requestConnectSocial : _______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectOnError.onNext("Kết nối thất bại");
                })
                .execute();
    }

    @Override
    public Observable<String> onConnectFacebookSuccess() {
        return subjectFacebookSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onConnectGoogleSuccess() {
        return subjectGoogleSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onConnectTwitterSuccess() {
        return subjectTwitterSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void requestSignOut() {
        useCaseFactory.signOut().executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    subjectSignOut.onNext(result);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: requestSignOut : _____________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectSignOut.onNext(false);
                })
                .execute();
    }

    @Override
    public Observable<Boolean> onSignOutSuccess() {
        return subjectSignOut.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return subjectOnError.subscribeOn(schedulerFactory.main());
    }
}
