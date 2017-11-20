package com.koenhabets.school.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.AlarmReceiver;
import com.koenhabets.school.JobUpdate;
import com.koenhabets.school.R;
import com.koenhabets.school.api.som.RefreshTokenRequest;
import com.koenhabets.school.fragments.GradeFragment;
import com.koenhabets.school.fragments.HomeworkFragment;
import com.koenhabets.school.fragments.TimeTableFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RequestQueue requestQueue;
    TextView email;
    TextView name;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        createNotificationChannel();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("Logged-in", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        refreshSomToken();

        //drawer top
        name = header.findViewById(R.id.textViewName);
        email = header.findViewById(R.id.textViewMail);
        imageView = header.findViewById(R.id.imageView);

        //serviceIntent = new Intent(this, BackgroundUpdateService.class);
        //startService(serviceIntent);

        if (sharedPref.getBoolean("scheduleNotifcation", true)) {
            scheduleJob(getApplicationContext());
        }

        replaceFragment(new TimeTableFragment());
    }

    private void refreshSomToken(){
        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);

        RefreshTokenRequest accessTokenRequest = new RefreshTokenRequest(sharedPref.getString("somRefreshToken", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Logged-in", true);
                    editor.putString("somAccessToken", jsonObject.getString("access_token"));
                    editor.putString("somRefreshToken", jsonObject.getString("refresh_token"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());

            }
        });
        requestQueue.add(accessTokenRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_time_table) {
            replaceFragment(new TimeTableFragment());
        } else if (id == R.id.nav_homework) {
            replaceFragment(new HomeworkFragment());
        } else if (id == R.id.nav_grades){
            replaceFragment(new GradeFragment());
        } else if (id == R.id.signout) {
            SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel("schedule", "Rooster notificatie", NotificationManager.IMPORTANCE_MIN);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void scheduleJob(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ComponentName serviceComponent = new ComponentName(context, JobUpdate.class);
            JobInfo.Builder builder = new JobInfo.Builder(54, serviceComponent);
            builder.setPeriodic(3600000);//1 hour
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            JobScheduler jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
            jobScheduler.schedule(builder.build());
        } else {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
        }
    }
}
