package com.github.huansern.eventlogger.data.source.local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.github.huansern.eventlogger.data.EventLog;

import javax.annotation.Nonnull;

@Database(entities = {EventLog.class}, version = 1, exportSchema = false)
public abstract class EventLoggerDatabase extends RoomDatabase {

    private static EventLoggerDatabase INSTANCE;

    public abstract EventLogsDao eventLogsDao();

    private static final Object sLock = new Object();

    public static EventLoggerDatabase getInstance(@Nonnull Context context){
        synchronized (sLock) {
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        EventLoggerDatabase.class, "EventLog.db")
                        .build();
            }
            return INSTANCE;
        }
    }

}
