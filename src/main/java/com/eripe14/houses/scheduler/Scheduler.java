package com.eripe14.houses.scheduler;

import panda.std.reactive.Completable;

import java.time.Duration;
import java.util.function.Supplier;

public interface Scheduler {

    void sync(Runnable task);

    void async(Runnable task);

    void laterSync(Runnable task, Duration delay);

    void laterAsync(Runnable task, Duration delay);

    void timerSync(Runnable task, Duration delay, Duration period);

    void timerAsync(Runnable task, Duration delay, Duration period);

    <T> Completable<T> completeSync(Supplier<T> task);

    <T> Completable<T> completeAsync(Supplier<T> task);

}