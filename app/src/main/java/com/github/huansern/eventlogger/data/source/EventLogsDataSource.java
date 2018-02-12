package com.github.huansern.eventlogger.data.source;


import com.github.huansern.eventlogger.data.EventLog;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EventLogsDataSource {

    interface OnEventLogsInsertedCallback {
        void onEventLogsInsertedCallback();
    }

    interface LoadEventLogsCallback {

        void onEventLogsLoaded(List<EventLog> eventLogs);

        void onDataNotAvailable();
    }

    void logEvent(@Nullable OnEventLogsInsertedCallback callback, @Nonnull EventLog eventLog);

    void logEvents(@Nullable OnEventLogsInsertedCallback callback, @Nonnull EventLog... eventLog);

    void getEventLogs(@Nonnull LoadEventLogsCallback callback, int count, long lastEventTime);

    void getNewEventLogs(@Nonnull LoadEventLogsCallback callback, long lastKnownEventTime);

    //void refreshEventLogs();

    //void deleteAllEventLogs();

    //void deleteEventLogsOlderThan(long eventTime);

    //void deleteEventLogsByEventType(int eventType);

    //void purgeEventLogsAfterRow(int index);
}
