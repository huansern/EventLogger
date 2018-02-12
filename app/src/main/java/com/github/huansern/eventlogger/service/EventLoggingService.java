package com.github.huansern.eventlogger.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.broadcastreceiver.AllEvent;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

public class EventLoggingService extends Service {

    private static final String CHANNEL_ID = "EventLoggerService";
    private static final int NOTIFICATION_ID = 1;

    private static boolean serviceStarted = false;

    private BroadcastReceiver mAllEvent = new AllEvent();
    private EventLogsRepository eventLogsRepository;

    public static void start(Context context) {
        if(!serviceStarted) {
            context.startService(new Intent(context,EventLoggingService.class));
            serviceStarted = true;
            save(context, R.string.shared_prefs_last_service_start_up_time, System.currentTimeMillis());
        }
    }

    public static void stop(Context context) {
        if(serviceStarted) {
            context.stopService(new Intent(context, EventLoggingService.class));
            serviceStarted = false;
        }
    }

    public static boolean getStatus() {
        return serviceStarted;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        createNotification();
        eventLogsRepository = Injection.provideEventLogsRepository(this);
        logServiceStarted();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logServiceStopped();
        unregisterBroadcastReceiver();
        removeNotification();
        eventLogsRepository = null;
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Event Logger", NotificationManager.IMPORTANCE_NONE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_security)
                .setContentTitle("Event Logger")
                .setContentText("Monitoring device activity")
                .setOngoing(true)
                .setAutoCancel(false)
                .setWhen(0)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        startForeground(NOTIFICATION_ID, notification);

    }

    private void removeNotification() {

        stopForeground(true);

    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        this.registerReceiver(mAllEvent, filter);
    }

    private void unregisterBroadcastReceiver() {
        this.unregisterReceiver(mAllEvent);
    }

    private void logServiceStarted() {
        EventLog eventLog = new EventLog(System.currentTimeMillis(), this.getString(R.string.event_category_service),
                this.getString(R.string.event_action_service_start), this.getString(R.string.event_action_service_start_label));
        eventLogsRepository.logEvent(null, eventLog);
    }

    private void logServiceStopped() {
        EventLog eventLog = new EventLog(System.currentTimeMillis(), this.getString(R.string.event_category_service),
                this.getString(R.string.event_action_service_end), this.getString(R.string.event_action_service_end_label));
        eventLogsRepository.logEvent(null, eventLog);
    }

    private static void save(Context context, int type, long time) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(type), time);
        editor.apply();
    }

}
