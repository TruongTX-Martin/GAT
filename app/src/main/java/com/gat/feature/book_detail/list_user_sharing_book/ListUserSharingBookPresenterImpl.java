package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

/**
 * Created by mozaa on 21/04/2017.
 */

public class ListUserSharingBookPresenterImpl implements ListUserSharingBookPresenter {


    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    public ListUserSharingBookPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }


}
