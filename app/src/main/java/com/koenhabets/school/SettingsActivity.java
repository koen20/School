package com.koenhabets.school;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.koenhabets.school.api.BackgroudUpdateService;

public class SettingsActivity extends AppCompatActivity {
    Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mySwitch = (Switch) findViewById(R.id.switch1);

        SharedPreferences sharedPref = this.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final boolean notificatie = sharedPref.getBoolean("notificatie", true);
        final SharedPreferences.Editor editor = sharedPref.edit();

        if (notificatie) {
            mySwitch.setChecked(true);
        }

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    editor.putBoolean("notificatie", true);
                    editor.apply();
                    Intent serviceIntent = new Intent(SchoolApp.getContext(), BackgroudUpdateService.class);
                    serviceIntent.setAction(BackgroudUpdateService.ACTION_REFRESH);
                    SchoolApp.getContext().startService(serviceIntent);
                }else{
                    editor.putBoolean("notificatie", false);
                    editor.apply();
                    NotificationManager notificationManager = (NotificationManager) SchoolApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    notificationManager.cancel(2);
                    notificationManager.cancel(3);
                }

            }
        });

    }
}
