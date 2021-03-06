package com.example.notifyhostilephonecalls.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.SQLite.DBBlockedNumbersHandler;
import com.example.notifyhostilephonecalls.adapters.RecyclerViewAdapter;
import com.example.notifyhostilephonecalls.models.PhoneNumber;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BlockedNumbersFragment extends Fragment implements View.OnClickListener
{

    private Animation animation;
    private static DBBlockedNumbersHandler dbBlockedNumbersHandler;
    private static ArrayList<PhoneNumber> contacts;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapter rcAdapter;
    private LinearLayoutManager linearLayoutManager;

    private Paint paint;

    private AdView adView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_blocked_numbers, container, false);
        FloatingActionButton fab2 = (FloatingActionButton) rootView.findViewById(R.id.fragment2_fab_btn);
        fab2.setOnClickListener(this);

        return rootView;
    }

    private void init()
    {
        dbBlockedNumbersHandler = new DBBlockedNumbersHandler(getContext());
        paint = new Paint();


        contacts = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.swing_up_left);

        contacts = dbBlockedNumbersHandler.getAllNumbers();
        recyclerView = getView().findViewById(R.id.recycler_view_fragment_2);
        rcAdapter = new RecyclerViewAdapter(contacts, getContext());

        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setAnimation(animation);


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus)
            {

            }
        });

        adView = getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);



        init();



        // TODO: 3/27/2021 find a way to separate that
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) //LEFT SWIPE (INFO)
                {

                    //store the data before they get deleted to restore them
                    PhoneNumber number = rcAdapter.getData().get(position);
                    int pos = position;

                    //restore the data to rcView
                    rcAdapter.removeItem(position);
                    rcAdapter.restoreItem(number, pos);

                    //open webSite for more info
                    String url = ("https://www.white-pages.gr/arithmos/" + rcAdapter.getPhonesNumber(position));
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(browserIntent);
                } else                               //RIGHT SWIPE (UNBLOCK)
                {

                    DBBlockedNumbersHandler dbBlockedNumbersHandler = new DBBlockedNumbersHandler(getContext());
                    dbBlockedNumbersHandler.deletePhoneNumber(rcAdapter.getData().get(position).getPhoneNumber());
                    rcAdapter.removeItem(position);
                }
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0)
                    {
                        //swipe right

                        paint.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.unlock_icn);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    } else
                    {
                        //swipe left

                        paint.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.info);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };




        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView.startAnimation(animation);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
    }

    

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public static void refreshRVAdapter()
    {
        contacts = dbBlockedNumbersHandler.getAllNumbers();
        rcAdapter.notifyDataSetChanged();
    }

    //Fab Button
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fragment2_fab_btn:
                dbBlockedNumbersHandler.clearDatabase();
                contacts.clear();
                rcAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}