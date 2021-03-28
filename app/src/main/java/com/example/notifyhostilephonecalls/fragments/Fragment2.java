package com.example.notifyhostilephonecalls.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.SQLite.DBBlockedNumbersHandler;
import com.example.notifyhostilephonecalls.adapters.RecyclerViewAdapter;
import com.example.notifyhostilephonecalls.models.PhoneNumber;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Fragment2 extends Fragment implements View.OnClickListener
{

    private Animation animation;
    private static DBBlockedNumbersHandler dbBlockedNumbersHandler;
    private static ArrayList<PhoneNumber> contacts;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapter rcAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab_fragment2, container, false);
        FloatingActionButton fab2 = (FloatingActionButton) rootView.findViewById(R.id.fragment2_fab_btn);
        fab2.setOnClickListener(this);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        dbBlockedNumbersHandler = new DBBlockedNumbersHandler(getContext());

        contacts = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.swing_up_left);

        contacts = dbBlockedNumbersHandler.getAllNumbers();
        recyclerView = getView().findViewById(R.id.recycler_view_fragment_2);
        rcAdapter = new RecyclerViewAdapter(contacts, getContext());

        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setAnimation(animation);


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