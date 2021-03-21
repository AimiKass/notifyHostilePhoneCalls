package com.example.notifyhostilephonecalls.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.adapters.RecyclerViewAdapter;
import com.example.notifyhostilephonecalls.models.PhoneNumber;

public class SwipeCardViewAdapter
{
    private RecyclerViewAdapter rcAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private Paint paint;

    public SwipeCardViewAdapter(RecyclerViewAdapter rcAdapter, RecyclerView recyclerView, Context context)
    {
        this.rcAdapter = rcAdapter;
        this.recyclerView = recyclerView;
        this.context = context;
        paint = new Paint();
    }

    public void enable()
    {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,/* ItemTouchHelper.LEFT |*/ ItemTouchHelper.RIGHT)  //LEFT SWIPE IS DISABLED FOR THE MOMENT
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) //LEFT SWIPE
                {
                    rcAdapter.removeItem(position);
                } else {                               //RIGHT SWIPE


                    //store the data before they get deleted to restore them
                    PhoneNumber number = rcAdapter.getData().get(position);
                    int pos = position;

                    //restore the data to rcView
                    rcAdapter.removeItem(position);
                    rcAdapter.restoreItem(number,pos);

                    //open webSite for more info
                    String url = ("https://www.white-pages.gr/arithmos/"+rcAdapter.getPhonesNumber(position));
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if(dX > 0)
                    {
                        //swipe right

                        paint.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.info);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest, paint);
                    } else
                    {
                        //swipe left

                        paint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
