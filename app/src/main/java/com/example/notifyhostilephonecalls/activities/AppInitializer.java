package com.example.notifyhostilephonecalls.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.NetworkOnMainThreadException;

// TODO: 3/21/2021 rename class
public class AppInitializer extends Application
{
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";


    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannel();
    }



    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Let you know when app is running",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel1 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Notifies hostile phone calls",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("This is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
}
