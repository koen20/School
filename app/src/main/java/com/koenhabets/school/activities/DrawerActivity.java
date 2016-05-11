package com.koenhabets.school.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.AlarmReceiver;
import com.koenhabets.school.R;
import com.koenhabets.school.api.ProfileRequest;
import com.koenhabets.school.api.TokenRequest;
import com.koenhabets.school.fragments.GradesFragment;
import com.koenhabets.school.fragments.NetpresenterFragment;
import com.koenhabets.school.fragments.TimeTableFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RequestQueue requestQueue;
    private PendingIntent pendingIntent;
    TextView email;
    TextView name;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        int day = sharedPref.getInt("request_token_day", 99);
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_YEAR);
        if (day != today) {
            Log.i("Request token", "Getting new request token.");
            getToken();
        }

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        name = (TextView) header.findViewById(R.id.textViewName);
        email = (TextView) header.findViewById(R.id.textViewMail);
        imageView = (ImageView) header.findViewById(R.id.imageView);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        if (!sharedPref.contains("name")) {
            Log.d("Request", "Getting profile info.");
            getProfile();
        } else {
            String Name = sharedPref.getString("name", "");
            String Email = sharedPref.getString("email", "");
            String PictureUrl = sharedPref.getString("picture", "");
            name.setText(Name);
            email.setText(Email);

            ImageRequest imgRequest = new ImageRequest(PictureUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                    saveBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Image request", error.getMessage());
                        }
                    });
            Bitmap bitmap = getBitmap();
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.i("Image", "Downloading image");
                requestQueue.add(imgRequest);
            }
        }
        startAlarm();
        replaceFragment(new TimeTableFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        } else if (id == R.id.nav_netpresenter) {
            replaceFragment(new NetpresenterFragment());
        } else if (id == R.id.nav_grades) {
            replaceFragment(new GradesFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getToken() {
        TokenRequest tokenRequest = new TokenRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(tokenRequest);
    }

    public void startAlarm() {
        Log.i("Alarm", "set");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }

    public void getProfile() {
        SharedPreferences sharedPref = getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String requestToken = sharedPref.getString("request_token", "");

        ProfileRequest profileRequest = new ProfileRequest(requestToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(profileRequest);
    }

    public void saveBitmap(Bitmap image) {
        Log.i("bitmap", "saving bitmap");
        try {
            FileOutputStream fos = this.openFileOutput("user.png", Context.MODE_PRIVATE);

            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (Exception e) {
            Log.e("Save bitmap", e.getMessage());
        }
    }

    public Bitmap getBitmap() {
        Log.i("Bitmap", "Loading bitmap from internal storage");
        String filename = "user.png";
        Bitmap bitmap = null;

        try {
            File filePath = this.getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e("get Bitmap", ex.getMessage());
        }
        return bitmap;
    }

}
