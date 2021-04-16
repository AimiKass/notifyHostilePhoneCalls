package com.example.notifyhostilephonecalls.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.notifyhostilephonecalls.SQLite.DBBlockedNumbersHandler;
import com.example.notifyhostilephonecalls.SQLite.DBIncomingCallsHandler;
import com.example.notifyhostilephonecalls.models.PhoneNumber;
import com.example.notifyhostilephonecalls.sendNotifications.Notification;
import com.example.notifyhostilephonecalls.utils.ExtractRating;
import com.example.notifyhostilephonecalls.utils.Permissions;
import com.example.notifyhostilephonecalls.utils.Settings;

import java.lang.reflect.Method;

public class CallBroadcastReceiver extends BroadcastReceiver
{
    public static final int PIE_API_VERSION = 28;
    private static final String TAG = CallBroadcastReceiver.class.getName();
    Notification notification = new Notification();
    private boolean newIncomingCallIs = true;
    private String rating = null;


    @Override
    public void onReceive(Context context, Intent intent)
    {


        if (!Permissions.isGranted(context, Permissions.READ_PHONE_STATE) || !Permissions.isGranted(context, Permissions.CALL_PHONE))
        {
            return;
        }

        // get telephony service
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getCallState() != TelephonyManager.CALL_STATE_RINGING)
        {
            return;
        }


        // get incoming call number.
        String incomingCallNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.d(TAG, "Incoming number: " + incomingCallNumber);

        if (incomingCallNumber != null)
        {
            DBIncomingCallsHandler dbIncomingCallsHandler = new DBIncomingCallsHandler(context);
            DBBlockedNumbersHandler dbBlockedNumbersHandler = new DBBlockedNumbersHandler(context);

            ExtractRating extract = new ExtractRating();


            //if block_all_calls in settings is checked
            if (Settings.getBooleanValue(context, Settings.BLOCK_ALL_CALLS))
            {
                breakCallAndNotify(context, incomingCallNumber);
                newIncomingCallIs = false;
            }


            //if block_calls_from_black_list in settings is checked
            if (Settings.getBooleanValue(context, Settings.BLOCK_CALLS_FROM_BLACK_LIST))
            {
                for (PhoneNumber blockedNumber : dbBlockedNumbersHandler.getAllNumbers())
                    if (blockedNumber.getPhoneNumber().equals(incomingCallNumber))
                    {
                        breakCallAndNotify(context, incomingCallNumber);
                        newIncomingCallIs = false;
                        break;
                    }
            }


            // if auto_block_hostile_calls in settings is checked
            if (Settings.getBooleanValue(context, Settings.AUTO_BLOCK_HOSTILE_CALLS))
            {
                rating = extract.getRating(incomingCallNumber);
                if (rating != null)
                {
                    if (Integer.parseInt(rating) > 0)
                    {
                        breakCallAndNotify(context, incomingCallNumber);
                        for (PhoneNumber blockedNumber:dbBlockedNumbersHandler.getAllNumbers() )
                        {
                            if (blockedNumber.getPhoneNumber().equals(incomingCallNumber))
                                break;
                            else
                            {
                                dbBlockedNumbersHandler.addPhoneNumber(incomingCallNumber, String.valueOf(rating));
                                newIncomingCallIs = false;
                            }
                        }

                    }
                }
            }


            if (newIncomingCallIs)
            {
                rating = extract.getRating(incomingCallNumber);

                //if incoming call is hostile rite in log and notify
                if (rating == null )
                {
                    dbIncomingCallsHandler.addPhoneNumber(incomingCallNumber, " unknown");
                }else if (Integer.parseInt(rating) > 0)
                {
                    dbIncomingCallsHandler.addPhoneNumber(incomingCallNumber, String.valueOf(rating));
                    notification.onNewPhoneCall(context, incomingCallNumber, String.valueOf(rating));
                }else
                {
                    dbIncomingCallsHandler.addPhoneNumber(incomingCallNumber, String.valueOf(rating));
                }
            }




        }
    }

    // Ends phone call
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void breakCallAndNotify(Context context, String number)
    {
        if (!Permissions.isGranted(context, Permissions.CALL_PHONE))
        {
            return;
        }
        if (Build.VERSION.SDK_INT >= PIE_API_VERSION)
        {
            breakCallPieAndHigher(context);
        } else
        {
            breakCallNougatAndLower(context);
        }


        notification.onCallBlocked(context, number);


    }


    private void breakCallNougatAndLower(Context context)
    {
        Log.d(TAG, "Trying to break call for Nougat and lower with TelephonyManager.");
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
            Log.d(TAG, "Invoked 'endCall' on TelephonyService.");
        } catch (Exception e)
        {
            Log.e(TAG, "Could not end call. Check stdout for more info.");
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void breakCallPieAndHigher(Context context)
    {
        Log.d(TAG, "Trying to break call for Pie and higher with TelecomManager.");
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        try
        {
            telecomManager.getClass().getMethod("endCall").invoke(telecomManager);
            Log.d(TAG, "Invoked 'endCall' on TelecomManager.");
        } catch (Exception e)
        {
            Log.e(TAG, "Could not end call. Check stdout for more info.");
            e.printStackTrace();
        }
    }

}
