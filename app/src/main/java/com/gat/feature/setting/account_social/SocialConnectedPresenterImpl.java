package com.gat.feature.setting.account_social;

import android.util.Log;

import com.gat.common.util.MZDebug;
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

public class SocialConnectedPresenterImpl implements SocialConnectedPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private UseCase<ServerResponse> useCaseUnLinkSocial;
    private final Subject<String> subjectUnLinkSuccess;
    private final Subject<String> subjectError;

    public SocialConnectedPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectUnLinkSuccess = PublishSubject.create();
        subjectError = PublishSubject.create();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void unLinkAccount(int type) {
        useCaseUnLinkSocial = useCaseFactory.unlinkSocialAccount(type);
        useCaseUnLinkSocial.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(serverResponse -> {
                    subjectUnLinkSuccess.onNext(serverResponse.message());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: unLinkAccount : ______________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                    subjectError.onNext("Failed");
                })
                .execute();
    }

    @Override
    public Observable<String> onUnLinkAccountSocialSuccess() {
        return subjectUnLinkSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return subjectError.subscribeOn(schedulerFactory.main());
    }
}
