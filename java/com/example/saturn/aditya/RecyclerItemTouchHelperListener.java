package com.example.saturn.aditya;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener
{
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);


}
