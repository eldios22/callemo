package com.accessibilityexample.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;



import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.accessibilityexample.ui.activity.MainActivity;

public class ForegroundService extends Service  {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    LocalBroadcastManager localBroadcastManager
            = LocalBroadcastManager.getInstance(ForegroundService.this);

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent overlayIntent = new Intent(context,OverlayService.class);
            context.startService(overlayIntent);

        }
    };



    @Override
    public void onCreate() {
        super.onCreate();





    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, new Intent(getApplicationContext(), MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        IntentFilter myFilter = new IntentFilter("my-local-broadcast");


        localBroadcastManager.registerReceiver(
                myBroadcastReceiver, myFilter);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(
                myBroadcastReceiver);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }


}