package com.example.saturn.diaryaditya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder>
{

    Context context;
    ArrayList<Diary> date_dateslist;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;


    String uid;



    public DiaryAdapter(Context context, ArrayList<Diary> tasknames)
    {
        this.context = context;
        this.date_dateslist = tasknames;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        uid = firebaseAuth.getUid().toString();

    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.taskitem,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {

            final String date_needtobeprinted=date_dateslist.get(position).year+" "+date_dateslist.get(position).month+" "+date_dateslist.get(position).day;

            holder.date_tv.setText(date_needtobeprinted);
            holder.imageView.setVisibility(View.INVISIBLE);
//        holder.cardView.setBackgroundResource(R.drawable.appsinfo);
//            holder.date_tv.setTextColor(R.color.Black);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date_needtobeprinted.equals(Dateselector.todaysdate))
                {
                    Intent intent=new Intent(context,DiaryAdder.class);
                    context.startActivity(intent);
//                    Toast.makeText(context, "You can click DearDiary if u want to see today", Toast.LENGTH_LONG).show();

                }
                else
                {
                    Intent intent=new Intent(context,view_diary.class);
                    intent.putExtra("Childname",date_needtobeprinted);
                    context.startActivity(intent);
                }

            }
        });




    }







    @Override
    public int getItemCount() {
        return date_dateslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView date_tv;
        ConstraintLayout constraintLayout;
        ImageView imageView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date_tv=itemView.findViewById(R.id.taskitem_Taskname);
            constraintLayout=itemView.findViewById(R.id.constraintlayout);
            imageView=itemView.findViewById(R.id.restoretask);
            cardView=itemView.findViewById(R.id.task_cardview);


        }
    }

}
