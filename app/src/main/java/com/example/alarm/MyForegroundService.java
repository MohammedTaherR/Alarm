package com.example.alarm;

// BackgroundService.java
//package com.example.backgroundserviceexample;
//

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyForegroundService extends Service {
    private BroadcastReceiver chargingReceiver;
    int cameraFacing = CameraSelector.LENS_FACING_FRONT;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Handler handler;
    private Runnable statusChecker;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        statusChecker = new Runnable() {
            @Override
            public void run() {
                Log.e("harry", "Service is running...");
                checkChargingStatus();
                handler.postDelayed(this, 1000);
            }
        };
        registerChargingReceiver();
        handler.post(statusChecker);
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        Intent notifyIntent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("App is running....")
                .setSmallIcon(R.mipmap.bellring)
                .setContentIntent(pendingIntent);

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

        private void registerChargingReceiver() {
        chargingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkChargingStatus();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(chargingReceiver, filter);
    }
    private MediaPlayer music;
    private boolean charge= true;
    private void checkChargingStatus() {

        int status = getBatteryStatus();
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) {
            if (music != null && music.isPlaying()) {
                music.stop();
                music.release();
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


                music = MediaPlayer.create(this, R.raw.audio);
                music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        music.start();

                    }
                });
            } else {
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
    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (music != null) {
            if (music.isPlaying()) {
                music.stop();
            }
            music.release();
            music = null;
        }

        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
