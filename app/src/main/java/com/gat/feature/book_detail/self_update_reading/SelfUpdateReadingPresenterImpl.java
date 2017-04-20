package com.gat.feature.book_detail.self_update_reading;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

/**
 * Created by mryit on 4/20/2017.
 */

public class SelfUpdateReadingPresenterImpl implements SelfUpdateReadingPresenter {


    UseCaseFactory useCaseFactory;
    SchedulerFactory schedulerFactory;

    public SelfUpdateReadingPresenterImpl(UseCaseFactory useCaseFactory,
                                          SchedulerFactory schedulerFactory) {

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
