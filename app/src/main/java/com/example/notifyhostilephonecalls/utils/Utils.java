package com.example.notifyhostilephonecalls.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Utils
{

    private static final String TAG = Utils.class.getName();

    /**
     * Makes and shows threadsafe toast
     */
    public static void showToast(final Context context, final String message, final int duration)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), message, duration).show();
            }
        });
    }


}
