package com.github.huansern.eventlogger;


import android.content.Context;
import android.support.annotation.NonNull;

import com.github.huansern.eventlogger.data.source.EventLogsRepository;
import com.github.huansern.eventlogger.data.source.local.EventLoggerDatabase;
import com.github.huansern.eventlogger.data.source.local.EventLogsLocalDataSource;
import com.github.huansern.eventlogger.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Injection {

    private Injection() {}

    public static EventLogsRepository provideEventLogsRepository(@NonNull Context context) {
        checkNotNull(context);
        EventLoggerDatabase database = EventLoggerDatabase.getInstance(context);
        return EventLogsRepository.getInstance(EventLogsLocalDataSource.getInstance(new AppExecutors(),database.eventLogsDao()));
    }

}
