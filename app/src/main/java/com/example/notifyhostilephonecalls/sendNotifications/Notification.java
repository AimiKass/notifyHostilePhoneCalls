package com.example.notifyhostilephonecalls.sendNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.activities.MainActivity;


// TODO: 3/25/2021 create not to send periodically in order for app to keep running in the backgtound
public class Notification
{
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    // Notification on call blocked
    public void onCallBlocked(Context context, String phoneNumber)
    {

        String message = context.getString(R.string.call_is_blocked);
        int icon = R.drawable.ic_block;
        //        String action = ;
        Intent intent = new Intent(context, MainActivity.class);

        notify(context, phoneNumber, message, intent,0, icon);
    }

    public void onNewPhoneCall(Context context, String phoneNumber,String rating)
    {
        String message = "Hostility Rating:"+rating+"%";
        int icon = R.drawable.inc_phone_call_icn;
        Intent intent = new Intent(context, MainActivity.class);
        notify(context, phoneNumber, message, intent,1, icon);
    }

    public void weakOrNoInternet(Context context, String phoneNumber)
    {
        String message = "Weak Or None Internet";
        String header = "Can't Extract Hostility Rating";
        int icon = R.drawable.icn_weak_con;
        Intent intent = new Intent(context,MainActivity.class);
        notify(context,phoneNumber,message,intent,2,icon);
    }


    /**
     *
     * @param context
     * @param title  --> title to show
     * @param message --> details to show
     * @param intent --> What should happen on clicking the notification
     * @param reqCode --> unique code for the notification
     */
    private static void notify(Context context, String title, String message, Intent intent,int reqCode ,@DrawableRes int icon/*, String action, Uri ringtone, boolean vibration*/)
    {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_2_ID).setSmallIcon(icon).setContentTitle(title).setContentText(message).setAutoCancel(true)
                //                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
        
    }
        
}