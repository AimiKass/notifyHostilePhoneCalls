package com.example.notifyhostilephonecalls.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.models.PhoneNumber;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{

    // variable for our array list and context
    private ArrayList<PhoneNumber> courseModalArrayList;
    private Context context;


    // constructor
    public RecyclerViewAdapter(ArrayList<PhoneNumber> courseModalArrayList, Context context)
    {
        this.courseModalArrayList = courseModalArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_number_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // on below line we are setting data
        // to our views of recycler view item.
        PhoneNumber model = courseModalArrayList.get(position);

        holder.phoneNumberTextView.setText(model.getPhoneNumber());
        holder.phoneRatingTextView.setText("Βαθμος Επικινδυνοτητας:"+model.getRating()+"%");
    }

    @Override
    public int getItemCount()
    {
        // returning the size of our array list
        return courseModalArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        // creating variables for our text views.
        public TextView phoneNumberTextView;
        public TextView phoneRatingTextView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            // initializing our text views
            phoneNumberTextView = itemView.findViewById(R.id.idPhoneNumber);
            phoneRatingTextView = itemView.findViewById(R.id.idPhoneRating);
        }
    }



}