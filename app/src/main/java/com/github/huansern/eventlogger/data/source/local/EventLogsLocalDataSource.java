package com.github.huansern.eventlogger.data.source.local;

import android.support.annotation.NonNull;

import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsDataSource;
import com.github.huansern.eventlogger.util.AppExecutors;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;


public class EventLogsLocalDataSource implements EventLogsDataSource {

    private static volatile EventLogsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private EventLogsDao mEventLogsDao;

    private EventLogsLocalDataSource(@Nonnull AppExecutors appExecutors, @NonNull EventLogsDao eventLogsDao) {
        mAppExecutors = appExecutors;
        mEventLogsDao = eventLogsDao;
    }

    public static EventLogsLocalDataSource getInstance(@Nonnull AppExecutors appExecutors, @NonNull EventLogsDao eventLogsDao) {
        if(INSTANCE == null) {
            synchronized (EventLogsLocalDataSource.class){
                if(INSTANCE == null) {
                    INSTANCE = new EventLogsLocalDataSource(appExecutors,eventLogsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void logEvent(@Nullable OnEventLogsInsertedCallback callback, @Nonnull final EventLog eventLog) {
        checkNotNull(eventLog);
        Runnable logEventRunnable = () -> {
            mEventLogsDao.insertEventLog(eventLog);
            if (callback != null) {
                callback.onEventLogsInsertedCallback();
            }
        };
        mAppExecutors.diskIO().execute(logEventRunnable);
    }

    @Override
    public void logEvents(@Nullable OnEventLogsInsertedCallback callback, @Nonnull final EventLog... eventLog) {
        checkNotNull(eventLog);
        Runnable logEventRunnable = () -> {
            mEventLogsDao.insertEventLog(eventLog);
            if (callback != null) {
                callback.onEventLogsInsertedCallback();
            }
        };
        mAppExecutors.diskIO().execute(logEventRunnable);
    }

    @Override
    public void getEventLogs(@Nonnull LoadEventLogsCallback callback, final int count, final long lastEventTime) {
        Runnable getLogsRunnable;

        if(lastEventTime == 0) {
            getLogsRunnable = () -> {
                final List<EventLog> eventLogs = mEventLogsDao.getEventLogs(count);
                mAppExecutors.mainThread().execute(() -> {
                    if(eventLogs.isEmpty()) {
                        callback.onDataNotAvailable();
                    } else {
                        callback.onEventLogsLoaded(eventLogs);
                    }
                });
            };
        } else {
            getLogsRunnable = () -> {
                final List<EventLog> eventLogs = mEventLogsDao.getEventLogs(count, lastEventTime);
                mAppExecutors.mainThread().execute(() -> {
                    if(eventLogs.isEmpty()) {
                        callback.onDataNotAvailable();
                    } else {
                        callback.onEventLogsLoaded(eventLogs);
                    }
                });
            };
        }

        mAppExecutors.diskIO().execute(getLogsRunnable);
    }

    @Override
    public void getNewEventLogs(@Nonnull LoadEventLogsCallback callback, long lastKnownEventTime) {
        checkNotNull(callback);
        Runnable getNewLogsRunnable = () -> {
            final List<EventLog> eventLogs = mEventLogsDao.getNewEventLogs(lastKnownEventTime);
            mAppExecutors.mainThread().execute(() -> {
                if(eventLogs.isEmpty()){
                    callback.onDataNotAvailable();
                } else {
                    callback.onEventLogsLoaded(eventLogs);
                }
            });
        };

        mAppExecutors.diskIO().execute(getNewLogsRunnable);
    }


}
