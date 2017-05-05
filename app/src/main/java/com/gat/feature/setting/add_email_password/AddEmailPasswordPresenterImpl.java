package com.gat.feature.setting.add_email_password;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

/**
 * Created by mryit on 5/5/2017.
 */

public class AddEmailPasswordPresenterImpl implements AddEmailPasswordPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    public AddEmailPasswordPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
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
