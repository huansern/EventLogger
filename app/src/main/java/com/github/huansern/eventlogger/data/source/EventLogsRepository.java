package com.github.huansern.eventlogger.data.source;


import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.github.huansern.eventlogger.data.EventLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventLogsRepository implements EventLogsDataSource {

    private volatile static EventLogsRepository INSTANCE;

    private final EventLogsDataSource mEventLogsLocalDataSource;

    public final ObservableInt mNewEventLogsAvailable = new ObservableInt(0);

    private EventLogsRepository(@NonNull EventLogsDataSource eventLogsLocalDataSource) {
        mEventLogsLocalDataSource = eventLogsLocalDataSource;
    }

    public static EventLogsRepository getInstance(EventLogsDataSource eventLogsLocalDataSource) {
        if(INSTANCE == null) {
            synchronized (EventLogsRepository.class) {
                if(INSTANCE == null) {
                    INSTANCE = new EventLogsRepository(eventLogsLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void logEvent(@Nullable OnEventLogsInsertedCallback callback, @Nonnull EventLog eventLog) {
        checkNotNull(eventLog);
        mEventLogsLocalDataSource.logEvent(() -> mNewEventLogsAvailable.set(mNewEventLogsAvailable.get() + 1), eventLog);
    }

    @Override
    public void logEvents(@Nullable OnEventLogsInsertedCallback callback, @Nonnull EventLog... eventLog) {
        checkNotNull(eventLog);
        mEventLogsLocalDataSource.logEvents(() -> mNewEventLogsAvailable.set(mNewEventLogsAvailable.get() + 1), eventLog);
    }

    @Override
    public void getEventLogs(@Nonnull LoadEventLogsCallback callback, int count, long lastEventTime) {
        checkNotNull(callback);
        mEventLogsLocalDataSource.getEventLogs(callback, count, lastEventTime);
    }

    @Override
    public void getNewEventLogs(@Nonnull LoadEventLogsCallback callback, long lastKnownEventTime) {
        checkNotNull(callback);
        mEventLogsLocalDataSource.getNewEventLogs(callback, lastKnownEventTime);
    }
}
