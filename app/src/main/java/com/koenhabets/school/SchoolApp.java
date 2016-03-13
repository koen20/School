package com.koenhabets.school;

import android.app.Application;
import android.content.Context;

/**
 * Created by Wouter Habets on 13-3-16.
 */
public class SchoolApp extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
