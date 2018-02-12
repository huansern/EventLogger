package com.github.huansern.eventlogger.service;


import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.annotation.Nonnull;

public class AppUsageService extends IntentService {

    public static final String startTime = "startTime";
    public static final String endTime = "endTime";

    public AppUsageService() {
        super("AppUsageService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent == null) {
            return;
        }

        long start = intent.getLongExtra(startTime, 0),
                end = intent.getLongExtra(endTime, 0);

        if(start == 0 || end == 0) {
            return;
        }

        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(this);

        ArrayList<AppUsage> appUsages = extractUsageEvent(start, end);
        if(appUsages.isEmpty()) {
            return;
        }

        ArrayList<EventLog> eventLogs = prepareLog(appUsages);
        if(eventLogs.isEmpty()) {
            return;
        }

        //Better be safe then sorry? How long the eventLogs size can be? Should I split the array and insert by batch?
        eventLogsRepository.logEvents(null, eventLogs.toArray(new EventLog[eventLogs.size()]));

        //Session ended log
        long time = end - 10;
        String category = this.getString(R.string.event_category_session);
        String action = this.getString(R.string.event_action_session_end);
        String desc = this.getString(R.string.event_action_session_end_label);

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvents(null, eventLog);
    }

    private ArrayList<AppUsage> extractUsageEvent(long start, long end) {
        ArrayList<AppUsage> appUsages = new ArrayList<>();

        @SuppressLint("WrongConstant")
        UsageStatsManager statsManager = (UsageStatsManager) this.getSystemService("usagestats");

        if(statsManager == null) {
            return appUsages;
        }

        UsageEvents usageEvents = statsManager.queryEvents(start, end);
        UsageEvents.Event event = new UsageEvents.Event();

        AppUsage appUsage = new AppUsage("com.github.huansern.NoSuchActivity", 0);
        boolean firstEvent = true;

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND || event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if(firstEvent) {
                    firstEvent = false;
                    if(event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        appUsages.add(appUsage);
                        appUsage = new AppUsage(event.getPackageName(), start + 20);
                        appUsage.setLatest(event.getTimeStamp());
                        continue;
                    }
                }
                if(appUsage.packageName.equals(event.getPackageName())){
                    appUsage.setLatest(event.getTimeStamp());
                } else {
                    appUsages.add(appUsage);
                    appUsage = new AppUsage(event.getPackageName(), event.getTimeStamp());
                }
            }
        }
        appUsages.add(appUsage);

        return appUsages;
    }
    
    private String getApplicationName(@Nonnull String packageName) {
        final PackageManager manager = this.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = manager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) ( ai != null ? manager.getApplicationLabel(ai) : "An unknown app" );
    }
    
    private ArrayList<EventLog> prepareLog(@Nonnull ArrayList<AppUsage> appUsages) {
        ArrayList<EventLog> eventLogs = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#");

        for (AppUsage appUsage: appUsages) {
            if(appUsage.getDuration() != 0) {
                String desc = getApplicationName(appUsage.packageName) + " has been used for " + durationToText(appUsage.getDuration());

                eventLogs.add(new EventLog(appUsage.start, this.getString(R.string.event_category_app), this.getString(R.string.event_action_app), desc));
            }
        }

        return eventLogs;
    }

    private String durationToText(long duration) {
        //reduce duration to seconds
        int millis = (int) (duration % 1000);
        duration = (duration - millis) / 1000;

        //reduce duration to minutes
        int seconds = (int) (duration % 60);
        duration = (duration - seconds) / 60;

        //reduce duration to hours
        int minutes = (int) (duration % 60);
        duration = (duration - minutes) / 60;

        //reduce duration to days
        int hours = (int) (duration % 24);
        duration = (duration - hours) / 24;

        int days = (int) duration;

        StringBuilder text = new StringBuilder();

        if(days > 0) {
            text.append(days).append("d ").append(hours).append("h ")
                    .append(minutes).append("m ").append(seconds).append("s");
        } else if(hours > 0) {
            text.append(hours).append("h ").append(minutes).append("m ")
                    .append(seconds).append("s");
        } else if(minutes > 0) {
            text.append(minutes).append("m ").append(seconds).append("s");
        } else if(seconds > 1) {
            text.append(seconds).append("seconds");
        } else if(seconds == 1){
            text.append(seconds).append("second");
        } else {
            text.append(millis).append("ms");
        }

        return text.toString();
    }

    private class AppUsage {
        String packageName;
        long start, end = 0;

        AppUsage (String name, long time) {
            packageName = name;
            start = time;
        }

        private void setLatest(long time) {
            end = time;
        }

        private long getDuration() {
            if(end == 0) {
                return 0;
            } else {
                return (end - start);// / 1000;
            }
        }
    }
}
