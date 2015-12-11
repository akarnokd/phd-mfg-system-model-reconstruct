package com.github.akarnokd.msmr.generator;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.github.akarnokd.msmr.model.*;
import com.github.akarnokd.msmr.model.Employee.ProductState;
import com.github.akarnokd.msmr.model.Machine.*;

import rx.Scheduler;
import rx.schedulers.*;

public final class Simulate {
    final Model model;
    final TestScheduler scheduler;
    final Scheduler.Worker worker;
    final TimeUnit unit = TimeUnit.SECONDS;
    
    static final long IN_STATION_WALK = 30;
    static final long CROSS_STATION_WALK = 60;
    
    public Simulate(Model model) {
        this.model = Objects.requireNonNull(model);
        this.scheduler = Schedulers.test();
        this.worker = scheduler.createWorker();
    }
    
    public void dispatch() {
        long now = worker.now() / 1000;
    }
    
}
