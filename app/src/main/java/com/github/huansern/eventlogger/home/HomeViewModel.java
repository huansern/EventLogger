package com.github.huansern.eventlogger.home;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsDataSource;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

import java.util.List;

import javax.annotation.Nonnull;

public class HomeViewModel extends AndroidViewModel {

    public ObservableInt scrollToTopButtonVisibility = new ObservableInt(View.GONE);

    public ObservableBoolean dataLoadingProgress = new ObservableBoolean(false);

    // TODO This two doesn't have to be an observable
    ObservableBoolean dataLoading = new ObservableBoolean(false);

    ObservableBoolean endOfList = new ObservableBoolean(false);

    ObservableField<String> dateHeaderText = new ObservableField<>("");

    private final EventLogsRepository mEventLogsRepository;

    ObservableList<EventLog> eventLogsData = new ObservableArrayList<>();

    private long mFirstItemEventTime = 0;

    private long mLastItemEventTime = 0;

    private static final int mInitialEventLogsCount = 50;

    private static final int mEventLogsPerLoad = 25;

    public HomeViewModel(@NonNull Application application, @Nonnull EventLogsRepository eventLogsRepository) {
        super(application);
        mEventLogsRepository = eventLogsRepository;
        mEventLogsRepository.mNewEventLogsAvailable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //loadNewEventLogs();
            }
        });
    }

    public void start() {
        loadEventLogs(true);
    }

    void loadNew() {
        loadNewEventLogs();
    }

    void loadMore() {
        loadEventLogs(false);
    }

    private void loadEventLogs(boolean forceUpdate) {
        //if data is still loading then be patient
        if(dataLoading.get()) {
            return;
        }

        if(forceUpdate) {
            endOfList.set(false);
        }

        if(endOfList.get()) {
            return;
        }

        dataLoading.set(true);

        if(forceUpdate) {
            dataLoadingProgress.set(true);
            mFirstItemEventTime = 0;
            mLastItemEventTime = 0;
        }

        int loadCount = mEventLogsPerLoad;

        if(mLastItemEventTime == 0) {
            loadCount = mInitialEventLogsCount;
        }

        mEventLogsRepository.getEventLogs(new EventLogsDataSource.LoadEventLogsCallback() {
            @Override
            public void onEventLogsLoaded(final List<EventLog> eventLogs) {

                if(forceUpdate) {
                    eventLogsData.clear();
                }

                if(eventLogsData.isEmpty()) {
                    mFirstItemEventTime = eventLogs.get(0).getTime();
                    dateHeaderText.set(eventLogs.get(0).getDateText());
                }

                mLastItemEventTime = eventLogs.get(eventLogs.size() - 1).getTime();

                eventLogsData.addAll(eventLogs);

                dataLoading.set(false);

                if(forceUpdate) {
                    dataLoadingProgress.set(false);
                }

            }

            @Override
            public void onDataNotAvailable() {
                endOfList.set(true);
                dataLoading.set(false);
                if(forceUpdate) {
                    dataLoadingProgress.set(false);
                }
            }
        }, loadCount, mLastItemEventTime);
    }

    //Once used to load only the new event log without refresh the whole list
    //@Deprecated
    private void loadNewEventLogs() {

        mEventLogsRepository.getNewEventLogs(new EventLogsDataSource.LoadEventLogsCallback() {
            @Override
            public void onEventLogsLoaded(List<EventLog> eventLogs) {

                mFirstItemEventTime = eventLogs.get(0).getId();

                eventLogsData.addAll(0, eventLogs);

                mLastItemEventTime = eventLogsData.get(eventLogsData.size() - 1).getId();

            }

            @Override
            public void onDataNotAvailable() {

            }
        }, mFirstItemEventTime);

    }

}
