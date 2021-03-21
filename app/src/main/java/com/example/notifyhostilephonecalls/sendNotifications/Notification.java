package com.example.notifyhostilephonecalls.sendNotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.activities.AppInitializer;
import com.example.notifyhostilephonecalls.activities.MainActivity;

public class Notification
{

    private NotificationManagerCompat notificationManagerCompat;



    public void notifyAboutNumber(Context context , String number, String phoneNumberRating)
    {

        notificationManagerCompat = NotificationManagerCompat.from(context);

        android.app.Notification notification = new NotificationCompat.Builder(context, AppInitializer.CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(number)
                .setContentText("Βαθμός επικινδυνότητας: "+phoneNumberRating+"%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0))
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(2,notification);



    }


    public void notifyAboutAppStart(Context context)
    {
        notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent notifIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, PendingIntent.FLAG_ONE_SHOT);

        android.app.Notification notification = new NotificationCompat.Builder(context, AppInitializer.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("NotifyHostilePhoneCalls")
                .setContentText("App Is Running!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(1,notification);


    }


}
