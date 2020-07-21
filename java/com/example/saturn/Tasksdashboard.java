package com.example.saturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.saturn.aditya.Task;
import com.example.saturn.aditya.taskAdapter;
import com.example.saturn.aditya.taskadder;
import com.example.saturn.aditya.taskviewer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class Tasksdashboard extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ListView taskslis;
    ArrayList<Task> tlist=new ArrayList<Task>();
    ArrayList<Task> completed_tasks=new ArrayList<Task>();
    ArrayAdapter<String> adapter;
    int a,b,c,d,e;
    String date;

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> stringArrayListnames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tasksdashboard);

        barChart=(BarChart)findViewById(R.id.barcharttask);

        taskslis=(ListView)findViewById(R.id.taskslists);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        barEntryArrayList=new ArrayList<BarEntry>();
        stringArrayListnames=new ArrayList<String>();

        Calendar calendar=Calendar.getInstance();

        a=calendar.get(Calendar.YEAR);
        b=calendar.get(Calendar.MONTH);
        c=calendar.get(Calendar.DAY_OF_MONTH);
        d = calendar.get(Calendar.HOUR_OF_DAY);
        e = calendar.get(Calendar.MINUTE);

        date=a+"-"+b+"-"+c;

        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, Task>> gti=new GenericTypeIndicator<Map<String, Task>>() {
                };
                Map<String,Task> abstmaplist=snapshot.getValue(gti);
                try {
                    tlist.clear();
                    completed_tasks.clear();
                    int i=0;
                    for (Map.Entry<String,Task> tsk:abstmaplist.entrySet())
                    {
                        Task added=tsk.getValue();

                        if(a<=Integer.parseInt(added.year) && b<=Integer.parseInt(added.month) && c<=Integer.parseInt(added.day))
                        {
                            if(c==Integer.parseInt(added.day))
                            {
                                if(d<Integer.parseInt(added.hour))
                                {
                                    tlist.add(added);
                                    barEntryArrayList.add(new BarEntry(i, Integer.parseInt(added.percentage_completion)));
                                    stringArrayListnames.add(added.taskname);
                                    i++;
                                }
                                else if(d==Integer.parseInt(added.hour) && e<Integer.parseInt(added.minute))
                                {
                                    tlist.add(added);
                                    barEntryArrayList.add(new BarEntry(i, Integer.parseInt(added.percentage_completion)));
                                    stringArrayListnames.add(added.taskname);
                                    i++;
                                }
                                else {
                                    completed_tasks.add(added);
                                }

                            }
                            else {
                                barEntryArrayList.add(new BarEntry(i, Integer.parseInt(added.percentage_completion)));
                                stringArrayListnames.add(added.taskname);
                                i++;
                            }

                        }
                        else
                        {
                            completed_tasks.add(added);
                        }

                    }
                    barEntryArrayList.add(new BarEntry(i, 0));
                    stringArrayListnames.add("");
                    i++;
//                    barEntryArrayList.add(new BarEntry(i, 100));
//                    stringArrayListnames.add("");
                    BarDataSet barDataSet=new BarDataSet(barEntryArrayList,"Percentage of Tasks Completed");
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    Description description=new Description();
                    description.setText("Tasks");
                    barChart.setDescription(description);
                    BarData barData=new BarData(barDataSet);
                    barChart.setData(barData);
                    XAxis xAxis=barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(stringArrayListnames));
                    xAxis.setPosition(XAxis.XAxisPosition.TOP);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(stringArrayListnames.size());
                    xAxis.setLabelRotationAngle(270);
                    barChart.animateY(2000);
                    barChart.invalidate();
                    if(tlist.isEmpty())
                    {
                        Toast.makeText(Tasksdashboard.this,"No Deadlines Today",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
//                        adapter = new ArrayAdapter<String>(Tasksdashboard.this, android.R.layout.simple_list_item_1,tlist);
                        taskslis.setAdapter(new taskAdapter(Tasksdashboard.this,tlist));
                    }
                    if(!completed_tasks.isEmpty())
                    {
                        create_dialoge();
                    }

                }
                catch (Exception e)
                {
                    Toast.makeText(Tasksdashboard.this,"No new tasks",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void create_dialoge() {


        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        String str = "";
        for(Task t: completed_tasks)
        {
            str=t.taskname+"\n";
        }
        builder.setTitle("Deadlines for some tasks are completed\n"+str+"better to update date or delete Task");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Tasksdashboard.this,taskviewer.class));

            }
        });
        builder.setNegativeButton("Delete These Tasks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(Task t: completed_tasks)
                {
                    databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks").child(t.taskname);
                    databaseReference.removeValue();

                    DatabaseReference deleteddata=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Deletedtasks").child(t.taskname);
                    deleteddata.setValue(t);
                }
//                Toast.makeText(Tasksdashboard.this,"Tasks Deleted",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void tasksdashboardback(View view) {
        finish();
        startActivity(new Intent(Tasksdashboard.this,MainPage.class));
    }

    public void buttonviewtask(View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), taskviewer.class));

    }

    public void buttonaddtask(View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), taskadder.class));

    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }

}
