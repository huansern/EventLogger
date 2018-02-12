package com.github.huansern.eventlogger.util;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

public class AppExecutors {

    private final Executor diskIO;

    private final Executor mainThread;

    public AppExecutors(Executor diskIO, Executor mainThread){
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(new DiskIOThreadExecutor(), new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@Nonnull Runnable command){
            mainThreadHandler.post(command);
        }
    }
}
