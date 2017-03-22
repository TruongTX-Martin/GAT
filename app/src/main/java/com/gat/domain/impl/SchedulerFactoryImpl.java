package com.gat.domain.impl;

import com.gat.domain.SchedulerFactory;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rey on 3/17/2016.
 */
public class SchedulerFactoryImpl implements SchedulerFactory {

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    public Scheduler main() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler single() {
        return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(r, "worker")));
    }
}
