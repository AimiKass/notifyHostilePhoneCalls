package com.example.notifyhostilephonecalls.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.phonecallReceiver.PhonecallReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class MainActivity extends AppCompatActivity
{

    public static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    public static final int MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2;

    IntentFilter intentFilter;
    CallReceiver callReceiver;

    private NotificationManager mNotificationManager;


    private void init()
    {
        intentFilter = new IntentFilter();
        callReceiver = new CallReceiver();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Coming Soon !!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            // We do not have this permission. Let's ask the user
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_CALL_LOG);
        }


        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(callReceiver,intentFilter);





    }


    public class CallReceiver extends PhonecallReceiver
    {



        @Override
        protected void onIncomingCallStarted(Context ctx, String number, Date start)
        {

            getPhoneNumberRating(number);

        }

        @Override
        protected void onOutgoingCallStarted(Context ctx, String number, Date start)
        {
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
        {
        }

        @Override
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
        {
        }

        @Override
        protected void onMissedCall(Context ctx, String number, Date missed)
        {
        }



        private void getPhoneNumberRating(String number)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder builder = new StringBuilder();

                    try {


                        String url="https://www.white-pages.gr/arithmos/"+number+"/";//your website url

                        Document   doc = Jsoup.connect(url).get();


                        String rating = doc.getElementsByClass("td78").select("div#progress-bar-inner").text();




                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

                        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();




                        bigText.setBigContentTitle(number);

                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
                        mBuilder.setContentTitle("Your Title");
                        mBuilder.setContentText("Βαθμος Επικινδυνότητας: "+rating+"%");
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setStyle(bigText);

                        mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // === Removed some obsoletes
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            String channelId = "Your_channel_id";
                            NotificationChannel channel = new NotificationChannel(
                                    channelId,
                                    "Channel human readable title",
                                    NotificationManager.IMPORTANCE_HIGH);
                            mNotificationManager.createNotificationChannel(channel);
                            mBuilder.setChannelId(channelId);
                        }

                        mNotificationManager.notify(0, mBuilder.build());



                    } catch (Exception e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }



                }
            }).start();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALL_LOG: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "READ_CALL_LOG granted!");
                    // check READ_PHONE_STATE permission only when READ_CALL_LOG is granted
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "READ_CALL_LOG denied!");
                    Toast.makeText(getApplicationContext(), "missing READ_CALL_LOG", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "READ_PHONE_STATE granted!");
                    // check PROCESS_OUTGOING_CALLS permission only when READ_PHONE_STATE is granted
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS);
                    }
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "READ_PHONE_STATE denied!");
                    Toast.makeText(getApplicationContext(), "missing READ_PHONE_STATE", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "PROCESS_OUTGOING_CALLS granted!");
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "PROCESS_OUTGOING_CALLS denied!");
                    Toast.makeText(getApplicationContext(), "missing PROCESS_OUTGOING_CALLS", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}