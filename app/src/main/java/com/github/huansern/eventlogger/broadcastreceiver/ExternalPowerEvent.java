package com.github.huansern.eventlogger.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static com.google.common.base.Preconditions.checkNotNull;

public class ExternalPowerEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(context);

        long time = System.currentTimeMillis();

        String category = context.getString(R.string.event_category_power), action = intent.getAction(), desc;

        checkNotNull(action);

        switch (action) {
            case ACTION_POWER_CONNECTED:
                desc = "External power connected.";
                break;
            case ACTION_POWER_DISCONNECTED:
                desc = "External power unplugged.";
                break;
            default:
                return;
        }

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvent(null, eventLog);
    }
}
