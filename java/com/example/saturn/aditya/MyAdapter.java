package com.example.saturn.aditya;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saturn.R;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{

    Context context;
    ArrayList<Task> tasknames;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;


    String uid;

    int a,b,c,d,e;



    public MyAdapter(Context context, ArrayList<Task> tasknames)
    {
        this.context = context;
        this.tasknames = tasknames;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        uid = firebaseAuth.getUid().toString();
        Calendar calendar=Calendar.getInstance();

        a=calendar.get(Calendar.YEAR);
        b=calendar.get(Calendar.MONTH);
        c=calendar.get(Calendar.DAY_OF_MONTH);
        d = calendar.get(Calendar.HOUR_OF_DAY);
        e = calendar.get(Calendar.MINUTE);

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
        holder.imageView.setVisibility(View.INVISIBLE);
        holder.taskname.setText(tasknames.get(position).taskname.toString());

        if(a<=Integer.parseInt(tasknames.get(position).year) && b<=Integer.parseInt(tasknames.get(position).month) && c<=Integer.parseInt(tasknames.get(position).day))
        {
            if(c==Integer.parseInt(tasknames.get(position).day))
            {
                if(d<Integer.parseInt(tasknames.get(position).hour))
                {
                    //
                }
                else if(d==Integer.parseInt(tasknames.get(position).hour) && e<Integer.parseInt(tasknames.get(position).minute))
                {
                    //
                }
                else
                {
                    holder.cardView.setBackgroundResource(R.drawable.cardlayoutbackgroundtaskcompleted);
//                    holder.cardView.setBackgroundResource(R.color.Red);
                }

            }
        }
        else
        {
            holder.cardView.setBackgroundResource(R.drawable.cardlayoutbackgroundtaskcompleted);
//            holder.cardView.setBackgroundResource(R.color.Red);
        }



        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,DataViewer.class);
                intent.putExtra("TaskName",tasknames.get(position).taskname);
                context.startActivity(intent);

            }
        });



    }

    public void removeItem(final int position)
    {
//
//        DatabaseReference databaseReference;
//        databaseReference=firebaseDatabase.getReference(uid.toString()).child("Tasks").child(tasknames.get(position).taskname);
//
//        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Task tsk=snapshot.getValue(Task.class);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        DatabaseReference deleteddata=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Deletedtasks").child(tasknames.get(position).taskname);
        deleteddata.setValue(tasknames.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if(task.isSuccessful())
                {

                    Toast.makeText(context,"Task removed successfully",Toast.LENGTH_SHORT).show();

                }
            }
        });
        DatabaseReference delt=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks").child(tasknames.get(position).taskname);
        delt.removeValue();



//        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
//                if (task.isSuccessful())
//                {
//                    Toast.makeText(context,"Task Removed Successfully",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        })
//        ;
//        tasknames.remove(position);


        notifyItemRemoved(position);

    }

    public void restoreItem(final Task item, int position)
    {

        tasknames.add(position,item);

        DatabaseReference databaseReference;
        databaseReference=firebaseDatabase.getReference(uid.toString()).child("Tasks").child(item.taskname.toString());

        databaseReference.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference delt=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Deletedtasks").child(item.taskname.toString());
                    delt.removeValue();
                    Toast.makeText(context,"Task added successfully",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(context,"Task not added",Toast.LENGTH_SHORT).show();
                }
            }
        });



        notifyItemInserted(position);

    }


    @Override
    public int getItemCount() {
        return tasknames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView taskname;
        ConstraintLayout constraintLayout;
        CardView cardView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            taskname=itemView.findViewById(R.id.taskitem_Taskname);
            constraintLayout=itemView.findViewById(R.id.constraintlayout);
            cardView=itemView.findViewById(R.id.taskitem_cardview);
            imageView=itemView.findViewById(R.id.restoretask);


        }
    }

}
