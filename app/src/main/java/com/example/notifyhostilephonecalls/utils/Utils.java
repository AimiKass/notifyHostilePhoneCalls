package com.example.notifyhostilephonecalls.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.example.notifyhostilephonecalls.R;

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


    /**
     * Scales passed view with passed dimension on Tablets only
     */
    public static void scaleViewOnTablet(Context context, View view, @DimenRes int dimenRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
            if (isTablet) {
                TypedValue outValue = new TypedValue();
                context.getResources().getValue(dimenRes, outValue, true);
                float scale = outValue.getFloat();
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        }
    }


    /**
     * Resolves attribute of passed theme returning referenced resource id
     **/
    public static int getResourceId(@AttrRes int attrRes, Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrRes, typedValue, true);
        return typedValue.resourceId;
    }

    /**
     * Resolves current theme's attribute returning referenced resource id
     **/
    public static int getResourceId(Context context, @AttrRes int attrRes) {
        return getResourceId(attrRes, context.getTheme());
    }



    /**
     * Sets drawable for the view
     **/
    @SuppressWarnings("deprecation")
    public static void setDrawable(Context context, View view, @DrawableRes int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }



}
