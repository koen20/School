package com.koenhabets.school;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.koenhabets.school.api.BackgroudUpdateService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BackgroudUpdateService.class);
        serviceIntent.setAction(BackgroudUpdateService.ACTION_REFRESH);
        context.startService(serviceIntent);
    }
}