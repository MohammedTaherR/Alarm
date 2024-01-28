package com.example.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyForegroundService extends Service implements IFrontCaptureCallback {
    private BroadcastReceiver chargingReceiver;
    int cameraFacing = CameraSelector.LENS_FACING_FRONT;
//    import java.time.LocalDate;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private Handler handler;
    private Runnable statusChecker;
    private static ActionLocks actionLocks = null;
    private SharedPreferences preferences;
    private String photoPath = null;
    private static GetBackStateFlags stateFlags = new GetBackStateFlags();
    private static GetBackFeatures features = new GetBackFeatures();

    Looper myLooper = Looper.myLooper();
    private static boolean isModeActive = false;
    private static class ActionLocks {
        public AtomicBoolean lockCapture;
        public AtomicBoolean lockSmsSend;
        public AtomicBoolean lockEmailSend;
        public AtomicBoolean lockLocationFind;
        public AtomicBoolean lockDataDelete;

        public ActionLocks() {
            lockCapture = new AtomicBoolean(false);
            lockSmsSend = new AtomicBoolean(false);
            lockEmailSend = new AtomicBoolean(false);
            lockLocationFind = new AtomicBoolean(false);
            lockDataDelete = new AtomicBoolean(false);
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        statusChecker = new Runnable() {
            @Override
            public void run() {
                Log.e("harry", "Service is running...");
                checkChargingStatus();

                handler.postDelayed(this, 1000);
//                takeAction(null);
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
//        takeAction(null);
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
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
//            takeAction(null);
//            sendSms();
            if (music == null) {
                if(charge == false){
//                    takeAction(null);
                    showToast("Device is not charging");
                    sendSms();
                    charge = true;
                }

//                takeAction(null);
                music = MediaPlayer.create(this, R.raw.audio);
                music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        music.start();


                    }
                });
            } else {
                if(charge == false){
//                    takeAction(null);
                    showToast("Device is not charging");
                    charge = true;
                }
//takeAction(null);

                music.start();
            }
        }
    }

    protected void sendSms() {
        try {
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            String formattedDate = myDateObj.format(myFormatObj);
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage("8667874962",null,"Some Tried to Unplug Charger at "+formattedDate,null,null);
            Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Some fields is Empty",Toast.LENGTH_LONG).show();
        }

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("8667874962", null, "Sample Text", null, null);
//                    Toast.makeText(getApplicationContext(), "SMS sent.",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//        }
//    }


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
        super.onDestroy();
        stopForeground(true);
        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onPhotoCaptured(String filePath) {
        synchronized (stateFlags) {
            Log.d("fuck","k2");
            Toast.makeText(this,"onphotocaputred",Toast.LENGTH_LONG).show();
            stateFlags.isPhotoCaptured = true;
            addBooleanPreference(Constants.PREFERENCE_IS_PHOTO_CAPTURED,
                    stateFlags.isPhotoCaptured);

            Utils.LogUtil.LogD(Constants.LOG_TAG, "Image saved at - "
                    + filePath);
            photoPath = filePath;
            addStringPreference(Constants.PREFERENCE_PHOTO_PATH, photoPath);
        }

        actionLocks.lockCapture.set(false);
        takeAction(null);
    }

    private void addBooleanPreference(String key, boolean value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }
    private void addStringPreference(String key, String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    private synchronized void takeAction(Bundle bundle) {
        capturePhoto();
    }

    private void capturePhoto() {
        Log.d("fuck","k");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.LogUtil.LogD(Constants.LOG_TAG, "Inside captureThread run");

                myLooper.prepare();
                CameraView frontCapture = new CameraView(MyForegroundService.this.getBaseContext());
                frontCapture.capturePhoto(MyForegroundService.this);

                myLooper.loop();
            }
        }).start();
    }


    @Override
    public void onCaptureError(int errorCode) {

    }
    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MyForegroundService.class), 0);

        return new NotificationCompat.Builder(this)
                .setTicker("Ticker")
                .setContentTitle("Service")
                .setContentText("Capture picture using foreground service")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }
}