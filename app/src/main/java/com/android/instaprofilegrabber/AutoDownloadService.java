package com.android.instaprofilegrabber;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Ramin on 8/7/2017.
 */

public class AutoDownloadService extends Service {
    public static boolean Running = false;
    private RClipboard rClipboard;

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Insta Profile Grabber")
                .setContentText("دانلود خودکار از اینستاگرام")
                .setTicker("دانلود خودکار از اینستاگرام")
                .setSmallIcon(R.mipmap.auto_dl)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(22222, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Running = true;

        //start clipboard detector
        if(rClipboard == null) rClipboard = new RClipboard(this);
        rClipboard.run();
        rClipboard.start();
        //end of start clipboard detector

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Running = false;
        if(rClipboard != null) rClipboard.stop();
        stopForeground(true);
    }
}