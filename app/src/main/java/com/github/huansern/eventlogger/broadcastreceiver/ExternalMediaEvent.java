package com.github.huansern.eventlogger.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.huansern.eventlogger.Injection;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;
import com.github.huansern.eventlogger.data.source.EventLogsRepository;

import static android.content.Intent.ACTION_MEDIA_BAD_REMOVAL;
import static android.content.Intent.ACTION_MEDIA_EJECT;
import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_REMOVED;
import static android.content.Intent.ACTION_MEDIA_SHARED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static com.google.common.base.Preconditions.checkNotNull;

public class ExternalMediaEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventLogsRepository eventLogsRepository = Injection.provideEventLogsRepository(context);

        long time = System.currentTimeMillis();

        String category = context.getString(R.string.event_category_storage), action = intent.getAction(), desc;

        checkNotNull(action);

        switch (action) {
            case ACTION_MEDIA_MOUNTED:
                desc = "External media(SD card) mounted.";
                break;
            case ACTION_MEDIA_SHARED:
                desc = "External media(SD card) being shared as USB mass storage.";
                break;
            case ACTION_MEDIA_EJECT:
                desc = "External media(SD card) is being ejected.";
                break;
            case ACTION_MEDIA_UNMOUNTED:
                desc = "External media(SD card) unmounted.";
                break;
            case ACTION_MEDIA_REMOVED:
                desc = "External media(SD card) has been removed.";
                break;
            case ACTION_MEDIA_BAD_REMOVAL:
                desc = "External media(SD card) is being removed without unmounted first.";
                break;
            default:
                return;
        }

        EventLog eventLog = new EventLog(time, category, action, desc);
        eventLogsRepository.logEvent(null, eventLog);
    }
}
