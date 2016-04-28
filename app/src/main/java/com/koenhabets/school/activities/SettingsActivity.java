package com.koenhabets.school.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.koenhabets.school.R;
import com.koenhabets.school.SchoolApp;
import com.koenhabets.school.api.BackgroudUpdateService;

public class SettingsActivity extends AppCompatActivity {
    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);

        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        boolean notificatie = sharedPref.getBoolean("notificatie", true);
        boolean notificatienet = sharedPref.getBoolean("notificatie-netpresenter", true);
        boolean notificatiegrades = sharedPref.getBoolean("notificatie-cijfers", true);
        boolean notificatiecalendar = sharedPref.getBoolean("notificatie-calendar", true);
        final SharedPreferences.Editor editor = sharedPref.edit();

        if (notificatie) {
            switch1.setChecked(true);
        }
        if (notificatienet) {
            switch3.setChecked(true);
        }
        if (notificatiegrades) {
            switch2.setChecked(true);
        }
        if (notificatiecalendar) {
            switch4.setChecked(true);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    notificatieon();
                }else{
                    editor.putBoolean("notificatie", false);
                    editor.apply();
                    NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    gradesoff();
                    netpresenteroff();
                    calendaroff();
                }

            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("notificatie-cijfers", true);
                    editor.apply();
                    notificatieon();
                } else {
                    gradesoff();
                }

            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("notificatie-netpresenter", true);
                    editor.apply();
                    notificatieon();
                } else {
                    netpresenteroff();
                }

            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("notificatie-calendar", true);
                    editor.apply();
                    notificatieon();
                } else {
                    calendaroff();
                }

            }
        });

    }

    public void netpresenteroff() {
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("notificatie-netpresenter", false);
        editor.apply();
        NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        switch3.setChecked(false);
    }

    public void gradesoff() {
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("notificatie-cijfers", false);
        editor.apply();
        NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);
        switch2.setChecked(false);
    }

    public void calendaroff() {
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("notificatie-calendar", false);
        editor.apply();
        NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        switch4.setChecked(false);
    }

    public void notificatieon() {
        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("notificatie", true);
        editor.apply();
        Intent serviceIntent = new Intent(SchoolApp.getContext(), BackgroudUpdateService.class);
        serviceIntent.setAction(BackgroudUpdateService.ACTION_REFRESH);
        SchoolApp.getContext().startService(serviceIntent);
        switch1.setChecked(true);
    }
}
