package com.github.huansern.eventlogger.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.github.huansern.eventlogger.Injection;
import com.google.common.base.Objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nonnull;


@Entity(tableName = "EventLogs")
public final class EventLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int mId;

    @ColumnInfo(name = "EventTime")
    private final long mTime;

    @ColumnInfo(name = "EventCategory")
    private final String mCategory;

    @Nonnull
    @ColumnInfo(name = "EventAction")
    private final String mAction;

    @NonNull
    @ColumnInfo(name = "EventDescription")
    private final String mDesc;

    @Ignore
    public EventLog(long time, @Nonnull String category, @Nonnull String action, @NonNull String desc){
        mTime = time;
        mCategory = category;
        mAction = action;
        mDesc = desc;
    }

    public EventLog(int id, long time, @Nonnull String category, @Nonnull String action, @NonNull String desc){
        this(time,category,action,desc);
        mId = id;
    }

    public int getId(){
        return mId;
    }

    public long getTime(){
        return mTime;
    }

    @Nonnull
    public String getTimeText(){
        return DateFormat.getTimeInstance().format(new Date(mTime));
    }

    @Nonnull
    public String getDateText() {
        return DateFormat.getDateInstance().format(new Date(mTime));
    }

    @Nonnull
    public Date getDate() {
        return new Date(mTime);
    }

    @Nonnull
    public String getCategory() {
        return mCategory;
    }

    @Nonnull
    public String getAction() {
        return mAction;
    }

    @NonNull
    public String getDesc(){
        return mDesc;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        EventLog e = (EventLog) o;
        return mId == e.mId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mDesc);
    }

    @Override
    public String toString() {
        return "Event logged at " + DateFormat.getTimeInstance().format(new Date(mTime))
                + " with following message: " + mDesc;
    }
}
