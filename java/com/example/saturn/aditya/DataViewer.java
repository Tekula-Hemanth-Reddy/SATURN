package com.example.saturn.aditya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;

public class DataViewer extends AppCompatActivity
{

    FloatingActionButton calendarbutton,timebutton;

    TextInputEditText dateedittext,timeedittext;

    TextView tv_taskname,tv_status;

    Button updatebtn;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String uid;

    String taskname;

    ProgressBar progressBar;

    SeekBar seekBar;

    String percentagecmp;

    static String year,month,day;
    static String minute,hour;

    int a,b,c;
    int mHour,mMinute;

    Task selectedtask;

    String taskuid;

    String sdate,stime;
    ProgressDialog progressDialog;
    String percentage_completion;

    ImageView starview;

    Boolean stared=false;
    Boolean contain=false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_data_viewer);

        init();

        starview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!stared)
                {
                    selectedtask.starred="1";
                    stared=true;
                    starview.setImageResource(R.drawable.ic_staron);
                    Toast.makeText(DataViewer.this,"Task Starred",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    selectedtask.starred="0";
                    stared=false;
                    starview.setImageResource(R.drawable.ic_staroff);
//                    Toast.makeText(DataViewer.this,"Task Un Starred",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }




    public void init()
    {

        calendarbutton=(FloatingActionButton)findViewById(R.id.dataviewer_calendarbtn);
        timebutton=(FloatingActionButton)findViewById(R.id.dataviewer_timebtn);

        dateedittext=(TextInputEditText)findViewById(R.id.dataviewer_calendardate);
        timeedittext=(TextInputEditText)findViewById(R.id.dataviewer_calendartime);


        tv_taskname=(TextView)findViewById(R.id.dataviewer_taskname);
        tv_status=(TextView)findViewById(R.id.dataviewer_percentcompletion);

        updatebtn=(Button)findViewById(R.id.dataviewer_updatebutton);

        seekBar=(SeekBar)findViewById(R.id.dataviewer_seekbar);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();

        uid=firebaseAuth.getUid().toString();

        taskname=getIntent().getStringExtra("TaskName");
        progressDialog = new ProgressDialog(this);

        starview=(ImageView)findViewById(R.id.starimportantviewer);

//        progressBar=(ProgressBar)findViewById(R.id.dataviewer_progressbar);


        initialise_thevalues_fromdb_ofselecteditem();




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                tv_status.setText(String.valueOf(progress)+"%");
                percentagecmp=String.valueOf(progress);

                if(progress==100)
                {
                    remove_task();
                }
                else {
                    selectedtask.percentage_completion = percentagecmp;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //check the floating action buttons

        calendarbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                Calendar calendar=Calendar.getInstance();

//                a=calendar.get(Calendar.YEAR);
//                b=calendar.get(Calendar.MONTH);
//                c=calendar.get(Calendar.DAY_OF_MONTH);
                a=Integer.parseInt(selectedtask.year);
                b=Integer.parseInt(selectedtask.month);
                c=Integer.parseInt(selectedtask.day);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(DataViewer.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth1) {

                            dateedittext.setText(year1+"-"+month1+"-"+dayOfMonth1);

                            year=String.valueOf(year1);
                            month=String.valueOf(month1);
                            day=String.valueOf(dayOfMonth1);
//                            selectedtask.year=year;
//                            selectedtask.month=month;
//                            selectedtask.day=day;

                        }
                    },a,b,c);
                    datePickerDialog.show();

                }


            }
        });

        timebutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
//               mHour=c.get(Calendar.HOUR);
//               mMinute=c.get(Calendar.MINUTE);
                mHour=Integer.parseInt(selectedtask.hour);
                mMinute=Integer.parseInt(selectedtask.minute);



                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(DataViewer.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            public void onTimeSet(TimePicker view, int hourOfDay1, int minute1) {

                                timeedittext.setText(hourOfDay1 + ":" + minute1);


                                minute=String.valueOf(minute1);
                                hour=String.valueOf(hourOfDay1);
//                                selectedtask.minute=minute;
//                                selectedtask.hour=hour;

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }




        });


        //the update button finally

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatealldetails();
                            }
        });





    }

    private void updatealldetails() {
        if(dateedittext.getText().toString().trim().isEmpty() || timeedittext.getText().toString().trim().isEmpty())
        {
            Toast.makeText(DataViewer.this,"please fill all the details",Toast.LENGTH_SHORT).show();
        }
        else {
            if(dateedittext.getText().toString().trim().matches("^(2[0-9][0-9]{2})-([0-9]|1[01])-([1-9]|[12][0-9]|3[01])*$") && timeedittext.getText().toString().trim().matches("^([0-9]|1[0-9]|2[0-3]):([0-9]|[1-5][0-9]|60)*$"))
            {
                String[] aryofdat=dateedittext.getText().toString().trim().split("-",3);
                selectedtask.year=aryofdat[0];
                selectedtask.month=aryofdat[1];
                selectedtask.day=aryofdat[2];
                String[] aryoftim=timeedittext.getText().toString().trim().split(":",2);
                selectedtask.hour=aryoftim[0];
                selectedtask.minute=aryoftim[1];
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(selectedtask.year));
                cal.set(Calendar.MONTH, Integer.parseInt(selectedtask.month));
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedtask.day));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(selectedtask.hour));
                cal.set(Calendar.MINUTE, Integer.parseInt(selectedtask.minute));
                if (cal.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    dateedittext.setError("Please fill valid date");
//                            Toast.makeText(DataViewer.this, "please Fill correct date and time", Toast.LENGTH_SHORT).show();
                } else {
                    Task task1 = new Task(uid, percentagecmp, taskname, taskuid, hour, minute, day, month, year,selectedtask.starred);
//                            progressDialog.setMessage("Updating");
//                            progressDialog.show();
//                            if(contain && !stared)
//                            {
//                                DatabaseReference delt=firebaseDatabase.getReference(firebaseAuth.getUid()).child("StarredTasks").child(taskname);
//                                delt.removeValue();
//                            }
//                            else if(!contain && stared)
//                            {
//                                DatabaseReference deltadd=firebaseDatabase.getReference(firebaseAuth.getUid()).child("StarredTasks").child(taskname);
//                                deltadd.setValue(taskname);
//
//                            }
                    databaseReference.setValue(selectedtask).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Details Saved Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DataViewer.this, taskviewer.class));
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Details  not saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DataViewer.this, taskviewer.class));
                            }

                        }
                    });
                }
            }
            else {
                dateedittext.setError("Please fill date in correct format");
                timeedittext.setError("Please fill time in correct format");
//                        Toast.makeText(DataViewer.this,"please Fill date and time in correct Formate",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initialise_thevalues_fromdb_ofselecteditem()
    {

//        progressBar.setVisibility(View.VISIBLE);

//        DatabaseReference dlt=firebaseDatabase.getReference(firebaseAuth.getUid()).child("StarredTasks").child(taskname);
//        dlt.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String str=snapshot.getValue(String.class);
//                try{
//                    if(str.equals(taskname))
//                    {
//                        contain=true;
//                        stared=true;
//                        starview.setImageResource(R.drawable.ic_staron);
//                    }
//                }
//                catch (Exception e)
//                {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        databaseReference=firebaseDatabase.getReference(uid.toString()).child("Tasks").child(taskname);



        tv_taskname.setText(taskname.toString());

        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Task s1=(Task)dataSnapshot.getValue(Task.class);
                selectedtask=s1;

                sdate=s1.year+"-"+s1.month+"-"+s1.day;
                stime=s1.hour+":"+s1.minute;

                dateedittext.setText(sdate);
                timeedittext.setText(stime);

                taskuid=s1.taskuid;

                percentage_completion=s1.percentage_completion+"%";

                tv_status.setText(percentage_completion);
                seekBar.setProgress(Integer.parseInt(selectedtask.percentage_completion));
                if(selectedtask.starred.equals("1"))
                {
                        contain=true;
                        stared=true;
                        starview.setImageResource(R.drawable.ic_staron);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void taskcompleted(View view) {
//        seekBar.setProgress(100);
        remove_task();

    }

    private void remove_task() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Are you sure your task "+selectedtask.taskname+" is completed");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seekBar.setProgress(100);
                databaseReference=firebaseDatabase.getReference(uid.toString()).child("Tasks").child(taskname);
                databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Task tsk=snapshot.getValue(Task.class);
                        DatabaseReference deleteddata=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Deletedtasks").child(taskname);
                        deleteddata.setValue(tsk).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    DatabaseReference delt=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks").child(taskname);
                                    delt.removeValue();
//                                    if(contain)
//                                    {
//                                        DatabaseReference deltprm=firebaseDatabase.getReference(firebaseAuth.getUid()).child("StarredTasks").child(taskname);
//                                        deltprm.removeValue();
//                                    }
                                    Toast.makeText(DataViewer.this,"You Succesfully Completed task "+tsk.taskname+" Congratulations!!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(DataViewer.this,taskviewer.class));
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seekBar.setProgress(Integer.parseInt(selectedtask.percentage_completion));
//                Toast.makeText(DataViewer.this,"Task Not Completed",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void dataviwer(View view) {
        updatealldetails();
        finish();
        startActivity(new Intent(DataViewer.this,taskviewer.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }
}
