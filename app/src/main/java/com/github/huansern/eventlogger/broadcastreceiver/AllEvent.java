package com.github.huansern.eventlogger.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;
import com.github.huansern.eventlogger.service.AppUsageService;

import java.util.Objects;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;
import static android.content.Intent.ACTION_MEDIA_BAD_REMOVAL;
import static android.content.Intent.ACTION_MEDIA_EJECT;
import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_REMOVED;
import static android.content.Intent.ACTION_MEDIA_SHARED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_USER_PRESENT;
import static com.google.common.base.Preconditions.checkNotNull;

public class AllEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long time = System.currentTimeMillis();

        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(context);

        String category, action = intent.getAction(), desc;

        checkNotNull(action);

        switch (action) {
            case ACTION_SCREEN_ON:
                desc = "Screen turned on.";
                category = context.getString(R.string.event_category_display);
                save(context, R.string.shared_prefs_last_screen_on_time, time);
                break;
            case ACTION_SCREEN_OFF:
                desc = "Screen turned off.";
                category = context.getString(R.string.event_category_display);
                determineSessionStartTime(context, time);
                break;
            case ACTION_USER_PRESENT:
                desc = "Device unlocked.";
                category = context.getString(R.string.event_category_lock);
                save(context, R.string.shared_prefs_last_user_present_time, time);
                break;
            case ACTION_POWER_CONNECTED:
                desc = "External power connected.";
                category = context.getString(R.string.event_category_power);
                break;
            case ACTION_POWER_DISCONNECTED:
                desc = "External power unplugged.";
                category = context.getString(R.string.event_category_power);
                break;
            case ACTION_BATTERY_LOW:
                desc = "Battery low.";
                category = context.getString(R.string.event_category_power);
                break;
            case ACTION_BATTERY_OKAY:
                desc = "Battery okay after being low.";
                category = context.getString(R.string.event_category_power);
                break;
            case ACTION_MEDIA_MOUNTED:
                desc = "External media(SD card) mounted.";
                category = context.getString(R.string.event_category_storage);
                break;
            case ACTION_MEDIA_SHARED:
                desc = "External media(SD card) being shared as USB mass storage.";
                category = context.getString(R.string.event_category_storage);
                break;
            case ACTION_MEDIA_EJECT:
                desc = "External media(SD card) is being ejected.";
                category = context.getString(R.string.event_category_storage);
                break;
            case ACTION_MEDIA_UNMOUNTED:
                desc = "External media(SD card) unmounted.";
                category = context.getString(R.string.event_category_storage);
                break;
            case ACTION_MEDIA_REMOVED:
                desc = "External media(SD card) has been removed.";
                category = context.getString(R.string.event_category_storage);
                break;
            case ACTION_MEDIA_BAD_REMOVAL:
                desc = "External media(SD card) is being removed without unmounted first.";
                category = context.getString(R.string.event_category_storage);
                break;
            default:
                return;
        }

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvent(null, eventLog);

        //Session started log
        if(action.equals(ACTION_USER_PRESENT)) {
            time += 10;
            category = context.getString(R.string.event_category_session);
            action = context.getString(R.string.event_action_session_start);
            desc = context.getString(R.string.event_action_session_start_label);

            eventLog = new EventLog(time, category, action, desc);
            eventLogsRepository.logEvents(null, eventLog);
        }
    }

    private void save(Context context, int type, long time) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(type), time);
        editor.apply();
    }

    private void determineSessionStartTime(Context context, long currentTime) {

        long sessionStartTime;

        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        long lastScreenOnTime, lastUserPresentTime, lastServiceStartUpTime;
        lastScreenOnTime = preferences.getLong(context.getString(R.string.shared_prefs_last_screen_on_time), -1);
        lastUserPresentTime = preferences.getLong(context.getString(R.string.shared_prefs_last_user_present_time), -1);
        lastServiceStartUpTime = preferences.getLong(context.getString(R.string.shared_prefs_last_service_start_up_time), -1);

        if(lastScreenOnTime == -1 || lastUserPresentTime == -1 || lastServiceStartUpTime == -1){
            return;
        }

        if(lastServiceStartUpTime > lastScreenOnTime && lastServiceStartUpTime > lastUserPresentTime) {
            // unlock < screen on < service
            // screen on < unlock < service
            sessionStartTime = lastServiceStartUpTime;
        } else if(lastServiceStartUpTime < lastScreenOnTime && lastServiceStartUpTime < lastUserPresentTime) {
            if(lastUserPresentTime > lastScreenOnTime) {
                // service < screen on < unlock
                sessionStartTime = lastUserPresentTime;
            } else {
                // service < unlock < screen on
                return;
            }
        } else {
            if(lastUserPresentTime > lastScreenOnTime) {
                // screen on < service < unlock
                sessionStartTime =lastUserPresentTime;
            } else {
                // unlock < service < screen on
                return;
            }
        }

        Intent appUsageService = new Intent(context, AppUsageService.class);
        appUsageService.putExtra(AppUsageService.startTime, sessionStartTime);
        appUsageService.putExtra(AppUsageService.endTime, currentTime);
        context.startService(appUsageService);
    }
}
