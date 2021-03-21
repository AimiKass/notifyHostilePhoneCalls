package com.example.notifyhostilephonecalls.activities;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.SQLite.DBHandler;
import com.example.notifyhostilephonecalls.adapters.RecyclerViewAdapter;
import com.example.notifyhostilephonecalls.adapters.SwipeCardViewAdapter;
import com.example.notifyhostilephonecalls.models.PhoneNumber;
import com.example.notifyhostilephonecalls.phonecallReceiver.PhonecallReceiver;
import com.example.notifyhostilephonecalls.retrieveData.ExtractFromSite;
import com.example.notifyhostilephonecalls.sendNotifications.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity
{

    public static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    public static final int MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2;

    IntentFilter intentFilter;
    CallReceiver callReceiver;

    private ArrayList<PhoneNumber> contacts;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter rcAdapter;
    private DBHandler dbHandler;
    LinearLayoutManager linearLayoutManager;

    CoordinatorLayout coordinatorLayout;
    private SwipeCardViewAdapter swipeCardViewAdapter;
    Animation animation;



    private void init()
    {
        intentFilter = new IntentFilter();
        dbHandler = new DBHandler(MainActivity.this);
        contacts = new ArrayList<>();
        callReceiver = new CallReceiver();

        coordinatorLayout = findViewById(R.id.main_content);

        recyclerView = findViewById(R.id.my_recycler_view);

        contacts = dbHandler.getAllNumbers();

        rcAdapter = new RecyclerViewAdapter(contacts, this);

        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rcAdapter);

        swipeCardViewAdapter = new SwipeCardViewAdapter(rcAdapter,recyclerView,this);
        animation = AnimationUtils.loadAnimation(this, R.anim.swing_up_left);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        swipeCardViewAdapter.enable();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "List Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                dbHandler.clearDatabase();
                contacts.clear();

                rcAdapter.notifyDataSetChanged();


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




    @Override
    protected void onStart()
    {
        super.onStart();
        // TODO: 3/18/2021 check what is the reason for this!  
//        contacts = dbHandler.getAllNumbers();
        rcAdapter.notifyDataSetChanged();
        recyclerView.startAnimation(animation);

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
            dbHandler.clearDatabase();
            rcAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // TODO: 3/18/2021 try a way to separate this class
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


        private void notifyAboutTheIncomingNumber(String number, Context context)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run() {

                    ExtractFromSite ex = new ExtractFromSite();
                    Notification notification = new Notification();
                    DBHandler dbHandler = new DBHandler(context);

                    String rating = ex.getPhoneNumberRating(number);

                    if (rating == null)
                        rating = "0";


                    notification.notifyAboutNumber(context,number,rating);


                    dbHandler.addPhoneNumber(number, rating);
                    contacts.add(new PhoneNumber(number,rating));

                }
            }).start();
        }


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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}