package com.github.huansern.eventlogger.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;
import com.github.huansern.eventlogger.service.EventLoggingService;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_LOCKED_BOOT_COMPLETED;
import static android.content.Intent.ACTION_SHUTDOWN;
import static com.google.common.base.Preconditions.checkNotNull;

public class BootEvent extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(context);

        long time = System.currentTimeMillis();
        //String timeText = DateFormat.getTimeFormat(context).format(new Date(time));

        String category = context.getString(R.string.event_category_boot), action = intent.getAction(), desc;

        checkNotNull(action);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && action.equals(ACTION_LOCKED_BOOT_COMPLETED)){
            desc = "Device startup completed.";
        } else {

            switch (action) {
                case ACTION_SHUTDOWN:
                    desc = "Device shutting down.";
                    EventLoggingService.stop(context);
                    break;
                case ACTION_BOOT_COMPLETED:
                    desc = "Device startup completed.";
                    EventLoggingService.start(context);
                    break;
                default:
                    return;
            }

        }

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvent(null, eventLog);
    }
}
