package com.github.huansern.eventlogger.data.source.local;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.github.huansern.eventlogger.data.EventLog;

import java.util.List;

@Dao
public interface EventLogsDao {

    @Query("SELECT * FROM EventLogs ORDER BY EventTime DESC LIMIT :count")
    List<EventLog> getEventLogs(int count);

    @Query("SELECT * FROM EventLogs WHERE EventTime < :lastEventTime ORDER BY EventTime DESC LIMIT :count")
    List<EventLog> getEventLogs(int count, long lastEventTime);

    @Query("SELECT * FROM EventLogs WHERE EventTime > :lastKnownEventTime ORDER BY EventTime DESC")
    List<EventLog> getNewEventLogs(long lastKnownEventTime);

    @Query("SELECT * FROM EventLogs WHERE EventCategory = :eventCategory ORDER BY EventTime DESC LIMIT :count")
    List<EventLog> getEventLogsByEventType(int count, int eventCategory);

    @Query("SELECT * FROM EventLogs WHERE EventCategory = :eventCategory AND EventTime < :lastEventTime ORDER BY EventTime DESC LIMIT :count")
    List<EventLog> getEventLogsByEventType(int count, long lastEventTime, int eventCategory);

    @Insert()
    void insertEventLog(EventLog... eventLog);

    @Query("DELETE FROM EventLogs")
    void deleteAllEventLogs();

    @Query("DELETE FROM EventLogs WHERE EventTime < :eventTime")
    void deleteEventLogsOlderThan(long eventTime);

    @Query("DELETE FROM EventLogs WHERE EventCategory = :eventCategory")
    void deleteEventLogsByEventCategory(int eventCategory);

    @Query("DELETE FROM EventLogs WHERE ID < (SELECT ID FROM EventLogs LIMIT 1 OFFSET :index)")
    void purgeEventLogsAfterRow(int index);
}
