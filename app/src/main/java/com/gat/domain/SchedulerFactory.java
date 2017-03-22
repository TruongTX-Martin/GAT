package com.gat.domain;


import io.reactivex.Scheduler;

/**
 * Created by Rey on 3/17/2016.
 */
public interface SchedulerFactory {

    Scheduler io();

    Scheduler computation();

    Scheduler main();

    Scheduler single();
}
