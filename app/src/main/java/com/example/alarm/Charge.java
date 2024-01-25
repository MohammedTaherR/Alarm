package com.example.alarm;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.Toast;

public class Charge extends Service {

    private BroadcastReceiver chargingReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the charging status receiver
        registerChargingReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service will continue running until explicitly stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(chargingReceiver);
    }

    private void registerChargingReceiver() {
        chargingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                if (isCharging) {
                    MediaPlayer music = MediaPlayer.create(context, R.raw.audio);
                        music.start();
                    Toast.makeText(context, "Device is charging", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Device is not charging", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Register the receiver with the intent filter for battery changes
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(chargingReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return null since a service does not need a binding
        return null;
    }
}

