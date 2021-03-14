package com.example.notifyhostilephonecalls.phonecallReceiver;

import android.content.Context;

import com.example.notifyhostilephonecalls.retrieveData.ExtractFromSite;
import com.example.notifyhostilephonecalls.sendNotifications.Notification;

import java.util.Date;


public class CallReceiver extends PhonecallReceiver
{

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {

        notifyAboutTheIncomingNumber(number, ctx);

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date missed) {
    }


    private void notifyAboutTheIncomingNumber(String number, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ExtractFromSite ex = new ExtractFromSite();
                Notification not = new Notification();

                not.showNotification(number,ex.getPhoneNumberRating(number),context);

            }
        }).start();
    }



}
