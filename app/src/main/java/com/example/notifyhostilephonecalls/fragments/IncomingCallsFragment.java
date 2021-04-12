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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.SQLite.DBBlockedNumbersHandler;
import com.example.notifyhostilephonecalls.SQLite.DBIncomingCallsHandler;
import com.example.notifyhostilephonecalls.adapters.RecyclerViewAdapter;
import com.example.notifyhostilephonecalls.models.PhoneNumber;
import com.example.notifyhostilephonecalls.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class IncomingCallsFragment extends Fragment implements View.OnClickListener
{

    private Animation animation;
    private ArrayList<PhoneNumber> contacts;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter rcAdapter;
    private DBIncomingCallsHandler dbIncomingCallsHandler;
    private LinearLayoutManager linearLayoutManager;

    Paint paint;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        FloatingActionButton fab1 = (FloatingActionButton) rootView.findViewById(R.id.fragment1_fab_btn);
        fab1.setOnClickListener(this);


        return rootView;
    }


    //in order to keep RC updated both onViewCreated() and onStart()
    private void init()
    {
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.swing_up_left);
        contacts = dbIncomingCallsHandler.getAllNumbers();
        rcAdapter = new RecyclerViewAdapter(contacts, getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rcAdapter);

    }



    public void onViewCreated (@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        dbIncomingCallsHandler = new DBIncomingCallsHandler(getContext());
        paint = new Paint();
        contacts = new ArrayList<>();

        recyclerView = getView().findViewById(R.id.recycler_view_fragment_1);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

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
                } else                               //RIGHT SWIPE (BLOCK)
                {

                    DBBlockedNumbersHandler dbBlockedNumbersHandler = new DBBlockedNumbersHandler(getContext());
                    dbBlockedNumbersHandler.addPhoneNumber(rcAdapter.getData().get(position).getPhoneNumber(),rcAdapter.getData().get(position).getRating());
                    dbIncomingCallsHandler.deletePhoneNumber(rcAdapter.getData().get(position).getPhoneNumber());
                    rcAdapter.removeItem(position);
//                    Fragment2.refreshRVAdapter();
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

                        paint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.block_number_icn);
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
    public void onStart()
    {
        super.onStart();

        init();

    }

    //Fab Button
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fragment1_fab_btn:
                if (contacts.isEmpty())
                {
                    Utils.showToast(getContext(),"No Incoming Calls Yet", Toast.LENGTH_SHORT);
                }
                else
                {
                    dbIncomingCallsHandler.clearDatabase();
                    contacts.clear();
                    rcAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }


}