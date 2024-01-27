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


//        handler = new Handler();
//        statusChecker = new Runnable() {
//            @Override
//            public void run() {
//                checkChargingStatus();
//                handler.postDelayed(this, 1000);
//
//            }
//        };
//        registerChargingReceiver();
//        handler.post(statusChecker);
//    }


//    private void registerChargingReceiver() {
//        chargingReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                checkChargingStatus();
//            }
//        };
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        registerReceiver(chargingReceiver, filter);
//    }
//    private MediaPlayer music;
//    private boolean charge= true;
//    private void checkChargingStatus() {
//
//        int status = getBatteryStatus();
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//
//        if (isCharging) {
//            if (music != null && music.isPlaying()) {
//                music.stop();
////                music.release();
//                music = null;
//            }
//            if(charge==true){
//            showToast("Device is charging");
//                charge  = false;
//            }
//
//        } else {
//            if (music == null) {
//                if(charge == false){
//                    showToast("Device is not charging");
//                    charge = true;
//                }
//                // Initialize the MediaPlayer if not already initialized
//                music = MediaPlayer.create(MainActivity.this, R.raw.audio);
//                music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mediaPlayer) {
//                        music.start();
//
//                    }
//                });
//            } else {
//                if(charge == false){
//                    showToast("Device is not charging");
//                    charge = true;
//                }
//                music.start();
//
//            }
//
//        }
//    }
//
//    private int getBatteryStatus() {
//        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent batteryStatus = registerReceiver(null, ifilter);
//        return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


}
