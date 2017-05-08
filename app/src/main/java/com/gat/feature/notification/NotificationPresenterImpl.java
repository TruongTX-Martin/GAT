package com.gat.feature.notification;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/27/2017.
 */

public class NotificationPresenterImpl implements NotificationPresenter {

    private static final int PER_PAGE = 10;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private int mCurrentPage;
    private int mTotalResult;

    private UseCase<DataResultListResponse<NotifyEntity>> useCaseNotifications;
    private final Subject<List<NotifyEntity>> subjectLoadNotificationsSuccess;


    public NotificationPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectLoadNotificationsSuccess = PublishSubject.create();
    }


    @Override
    public void onCreate() {
        mCurrentPage = 1;
        mTotalResult = 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void loadUserNotification() {
        MZDebug.w("_______________________________________________ loadUserNotification ");

        if (mTotalResult > PER_PAGE) {
            if (mCurrentPage * PER_PAGE >= mTotalResult) {
                return;
            }
        }

        useCaseNotifications = useCaseFactory.getUserNotification(mCurrentPage, PER_PAGE);
        useCaseNotifications.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    MZDebug.w("______________________ loadUserNotification SUCCESS, size: " +
                            data.getResultInfo().size());
                    mCurrentPage ++;
                    mTotalResult = data.getNotifyTotal();
                    subjectLoadNotificationsSuccess.onNext(data.getResultInfo());
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadUserNotification _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                })
                .execute();
    }

    @Override
    public Observable<List<NotifyEntity>> onLoadUserNotificationSuccess() {
        return subjectLoadNotificationsSuccess.subscribeOn(schedulerFactory.main());
    }
}
