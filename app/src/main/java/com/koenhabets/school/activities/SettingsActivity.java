package com.koenhabets.school.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.koenhabets.school.AlarmReceiver;
import com.koenhabets.school.R;

public class SettingsActivity extends AppCompatActivity {
    private Switch switchSchedule;
    private Switch switchScheduleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        switchSchedule = findViewById(R.id.switchSchedule);
        switchScheduleLayout = findViewById(R.id.switchScheduleLayout);

        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        switchSchedule.setChecked(sharedPref.getBoolean("scheduleNotifcation", true));
        switchScheduleLayout.setChecked(sharedPref.getBoolean("scheduleWeek", true));

        switchSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    startAlarm();
                    editor.putBoolean("scheduleNotifcation", true);
                    editor.apply();
                } else {
                    editor.putBoolean("scheduleNotifcation", false);
                    editor.apply();
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(555);
                }

            }
        });
        switchScheduleLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor.putBoolean("scheduleWeek", isChecked);
                editor.apply();
            }
        });
    }

    private void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        this.startService(alarmIntent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}
