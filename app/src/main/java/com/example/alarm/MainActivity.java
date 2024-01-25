package com.example.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        handler = new Handler();
        statusChecker = new Runnable() {
            @Override
            public void run() {
                checkChargingStatus();
                handler.postDelayed(this, 1000);
            }
        };
        registerChargingReceiver();
        handler.post(statusChecker);
    }


    private void registerChargingReceiver() {
        chargingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Check the charging status and display Toast
                checkChargingStatus();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(chargingReceiver, filter);
    }
    private MediaPlayer music;
    private boolean charge= true;
    private void checkChargingStatus() {
//        int status = getBatteryStatus();
//        MediaPlayer music = MediaPlayer.create(MainActivity.this, R.raw.audio);
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//        if (isCharging) {
//            music.stop();
//            music.release();
////            if(music.isPlaying()){
////            Looper.myLooper().quit();}
//            showToast("Device is charging");
//        } else {
//            music.start();
//            showToast("Device is not charging");
//        }


        int status = getBatteryStatus();
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            if (music != null && music.isPlaying()) {
                music.stop();
//                music.release();  // Release the MediaPlayer resources
                music = null;
            }
            if(charge==true){
            showToast("Device is charging");
                charge  = false;
            }

        } else {
            if (music == null) {
                if(charge == false){
                    showToast("Device is not charging");
                    charge = true;
                }
                // Initialize the MediaPlayer if not already initialized
                music = MediaPlayer.create(MainActivity.this, R.raw.audio);
                music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // Start playing the music once prepared

                        music.start();

                    }
                });
            } else {
                // If MediaPlayer is already initialized, start playing the music
                if(charge == false){
                    showToast("Device is not charging");
                    charge = true;
                }
                music.start();

            }

        }
    }

    private int getBatteryStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
