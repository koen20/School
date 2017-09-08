package com.koenhabets.school;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.koenhabets.school.api.BackgroundUpdateService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BackgroundUpdateService.class);
        context.startService(serviceIntent);
    }
}
