package com.github.huansern.eventlogger;


import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.github.huansern.eventlogger.data.source.EventLogsRepository;
import com.github.huansern.eventlogger.home.HomeViewModel;

import javax.annotation.Nonnull;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final EventLogsRepository mEventLogsRepository;

    private ViewModelFactory(Application application, EventLogsRepository eventLogsRepository) {
        mApplication = application;
        mEventLogsRepository = eventLogsRepository;
    }

    public static ViewModelFactory getInstance(@Nonnull Application application) {
        if(INSTANCE == null){
            synchronized (ViewModelFactory.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application, Injection.provideEventLogsRepository(application));
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(HomeViewModel.class)){
            //noinspection unchecked
            return (T) new HomeViewModel(mApplication, mEventLogsRepository);
        }
        throw new IllegalArgumentException("Unknown model class: " + modelClass.getName());
    }
}
