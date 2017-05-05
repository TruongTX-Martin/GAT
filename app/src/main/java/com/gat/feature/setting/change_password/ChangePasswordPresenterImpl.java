package com.gat.feature.setting.change_password;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

/**
 * Created by mozaa on 05/05/2017.
 */

public class ChangePasswordPresenterImpl implements ChangePasswordPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    public ChangePasswordPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
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
