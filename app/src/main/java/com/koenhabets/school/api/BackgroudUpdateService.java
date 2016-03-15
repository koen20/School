package com.koenhabets.school.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class BackgroudUpdateService extends IntentService {

    public static final String ACTION_REFRESH = "com.koenhabets.school.api.action.REFRESH";


    public BackgroudUpdateService() {
        super("BackgroudUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh();
            }
        }
    }

    private void handleActionRefresh() {
        Log.d(this.getClass().getSimpleName(), "Hallo, ik ben een service");
    }
}
