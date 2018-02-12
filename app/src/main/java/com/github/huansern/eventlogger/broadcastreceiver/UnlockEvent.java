package com.github.huansern.eventlogger.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_USER_PRESENT;
import static com.google.common.base.Preconditions.checkNotNull;

public class UnlockEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(context);

        long time = System.currentTimeMillis();
        //String timeText = DateFormat.getTimeInstance().format(new Date(time));

        String category = context.getString(R.string.event_category_lock), action = intent.getAction(), desc;

        checkNotNull(action);

        switch (action) {
            case ACTION_USER_PRESENT:
                desc = "Device unlocked.";
                break;
            default:
                return;
        }

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvent(null, eventLog);
    }
}
