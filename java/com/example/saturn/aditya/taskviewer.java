package com.example.saturn.aditya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.example.saturn.Tasksdashboard;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Map;

public class taskviewer extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String uid;

    RecyclerView recyclerView;

    ArrayList<Task> tasknameslist;

    LinearLayout relativeLayout;

    MyAdapter adapter;
    MydeleteAdapter deleteadapter;
    Toolbar toolbar;
    TextView tlbnme;

    Boolean star=false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_taskviewer);

        init();


    }

    public void init() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        uid = firebaseAuth.getUid().toString();

        recyclerView = (RecyclerView) findViewById(R.id.taskviewer_recyclerview);

        relativeLayout = (LinearLayout) findViewById(R.id.taskviewer_relativelayout);

        tasknameslist = new ArrayList<Task>();
        tlbnme=(TextView)findViewById(R.id.toolbarname);

        toolbar = findViewById(R.id.toolbartaskviewer);
        setSupportActionBar(toolbar);


        get_list_of_tasks();


    }

    public void get_list_of_tasks() {


        databaseReference = firebaseDatabase.getReference(uid.toString()).child("Tasks");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasknameslist.clear();

                GenericTypeIndicator<Map<String, Task>> gti = new GenericTypeIndicator<Map<String, Task>>() {
                };
                Map<String, Task> tasknames = dataSnapshot.getValue(gti);
                try {
                    for (Map.Entry<String, Task> entry : tasknames.entrySet()) {
                        if(star)
                        {
                            if(entry.getValue().starred.equals("1"))
                            {
                                tasknameslist.add(entry.getValue());
                            }
                        }
                        else
                        {
                            tasknameslist.add(entry.getValue());
                        }

                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(taskviewer.this,"No tasks yet",Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(taskviewer.this, MainPage.class));
                }

                adapter = new MyAdapter(taskviewer.this, tasknameslist);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(taskviewer.this));

                ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                        = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, new RecyclerItemTouchHelperListener() {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

                        if (viewHolder instanceof MyAdapter.MyViewHolder) {
                            String name = tasknameslist.get(viewHolder.getAdapterPosition()).taskname;

                            final Task deletedItem = tasknameslist.get(viewHolder.getAdapterPosition());
                            final int deleteIndex = viewHolder.getAdapterPosition();
                            adapter.removeItem(deleteIndex);

                            Snackbar snackbar = Snackbar.make(relativeLayout, name + "removed from cart", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter.restoreItem(deletedItem, deleteIndex);
                                }
                            });

                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.setDuration(3000);
                            snackbar.show();


                        }


                    }
                });
                new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void taskviewer(View view) {
        finish();
        startActivity(new Intent(taskviewer.this, Tasksdashboard.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_dash_menu_page, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_all:{
                star=false;
                tlbnme.setText("All Tasks");
                get_list_of_tasks();
                break;
            }
            case R.id.action_starred:{
                star=true;
                tlbnme.setText("Starred Tasks");
                get_list_of_tasks();
                break;
            }
            case R.id.action_deleted:{
                tlbnme.setText("Deleted Tasks");
                show_deleted_task_list();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void show_deleted_task_list() {
        databaseReference = firebaseDatabase.getReference(uid.toString()).child("Deletedtasks");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasknameslist.clear();

                GenericTypeIndicator<Map<String, Task>> gti = new GenericTypeIndicator<Map<String, Task>>() {
                };
                Map<String, Task> tasknames = dataSnapshot.getValue(gti);
                try {
                    for (Map.Entry<String, Task> entry : tasknames.entrySet()) {

                            tasknameslist.add(entry.getValue());

                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(taskviewer.this,"No Deleted tasks",Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(taskviewer.this, MainPage.class));
                }

                deleteadapter = new MydeleteAdapter(taskviewer.this, tasknameslist);
                recyclerView.setAdapter(deleteadapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(taskviewer.this));

                ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                        = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, new RecyclerItemTouchHelperListener() {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

                        if (viewHolder instanceof MydeleteAdapter.MyViewHolder) {
                            String name = tasknameslist.get(viewHolder.getAdapterPosition()).taskname;

                            final Task deletedItem = tasknameslist.get(viewHolder.getAdapterPosition());
                            final int deleteIndex = viewHolder.getAdapterPosition();
                            deleteadapter.removeItem(deleteIndex);

                            Snackbar snackbar = Snackbar.make(relativeLayout, name + "removed from cart", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteadapter.restoreItem(deletedItem, deleteIndex);
                                }
                            });

                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.setDuration(3000);
                            snackbar.show();


                        }


                    }
                });
                new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /*
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof MyAdapter.MyViewHolder) {
            String name = tasknameslist.get(viewHolder.getAdapterPosition()).taskname;

            final Task deletedItem = tasknameslist.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deleteIndex);

            Snackbar snackbar = Snackbar.make(relativeLayout, name + "removed from cart", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedItem, deleteIndex);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();


        }


    }*/
}
