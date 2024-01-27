package com.example.alarm;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver chargingReceiver;
    private Handler handler;
    private Runnable statusChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.button);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        String s1 = sharedPreferences.getString("service", "Start Service");
        btn.setText((s1));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn.getText().equals("Start Service")) {
                    btn.setText("STOP SERVICE");
                    myEdit.putString("service","Stop Service");
                    myEdit.commit();
                    Intent serviceIntent = new Intent(MainActivity.this, MyForegroundService.class);
                    startForegroundService(serviceIntent);
                    foregroundServiceRunning();
                    Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
                }else{
                    btn.setText("Start Service");
                    myEdit.putString("service","Start Service");
                    myEdit.commit();
                    Intent serviceIntent = new Intent(MainActivity.this, MyForegroundService.class);
                    stopService(serviceIntent);
                    Toast.makeText(MainActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });
    } public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
