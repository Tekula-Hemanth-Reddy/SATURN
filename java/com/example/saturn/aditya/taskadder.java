package com.example.saturn.aditya;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.example.saturn.Tasksdashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class taskadder extends AppCompatActivity {


    FloatingActionButton calendarbutton,timebutton;

    TextInputEditText taskname,calendar_date,calendar_time;

    ImageView starred;

    Button addtask_button;

    String percentage_completion="0";

    static String year,month,day;
    static String minute,hour;
    String uid;
    String string_taskname;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ArrayList<String> alredytasks=new ArrayList<String>();

    int a,b,c;
    int mHour,mMinute;

    Boolean star=false;

    Boolean contain=false;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_taskadder);

        //intialising...................................................................
        init();

        //listen to the buttons...........................................................
        goonfun();

        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!star)
                {
                    star=true;
                    starred.setImageResource(R.drawable.ic_staron);
                    Toast.makeText(taskadder.this,"Task Starred",Toast.LENGTH_SHORT).show();
                }
                else if(star)
                {
                    star=false;
                    starred.setImageResource(R.drawable.ic_staroff);
//                    Toast.makeText(taskadder.this,"Task Unstarred",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


    public void init()
    {
        //intialising the widgets......................................................

        calendarbutton=(FloatingActionButton)findViewById(R.id.taskadder_calendarbutton);
        timebutton=(FloatingActionButton)findViewById(R.id.taskadder_timebutton);


        taskname=(TextInputEditText)findViewById(R.id.taskadder_taskname);
        calendar_date=(TextInputEditText)findViewById(R.id.taskadder_datetext);
        calendar_time=(TextInputEditText)findViewById(R.id.taskadder_timetext);


        addtask_button=(Button)findViewById(R.id.taskadder_ADDTASK_Button);

        starred=(ImageView) findViewById(R.id.starimportant);


        //Now we need to intialise the Firebase...........................................

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid().toString();

        //check the floating action buttons

    }


    public void goonfun()
    {

        //check the floating action buttons

        calendarbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                Calendar calendar=Calendar.getInstance();

                a=calendar.get(Calendar.YEAR);
                b=calendar.get(Calendar.MONTH);
                c=calendar.get(Calendar.DAY_OF_MONTH);







                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(taskadder.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth1) {

                            calendar_date.setText(year1+"-"+month1+"-"+dayOfMonth1);

//                            year=String.valueOf(year1);
//                            month=String.valueOf(month1);
//                            day=String.valueOf(dayOfMonth1);




                        }
                    },a,b,c);
                    datePickerDialog.show();

                }


            }
        });

        timebutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);



                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(taskadder.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            public void onTimeSet(TimePicker view, int hourOfDay1, int minute1) {

                                calendar_time.setText(hourOfDay1 + ":" + minute1);


//                                minute=String.valueOf(minute1);
//                                hour=String.valueOf(hourOfDay1);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });

        addtask_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addnewtask();
            }
        });



    }

    public void addnewtask()
    {
        if(taskname.getText().toString().trim().isEmpty() || calendar_date.getText().toString().trim().isEmpty() || calendar_time.getText().toString().trim().isEmpty())
        {
            Toast.makeText(taskadder.this,"Please fill all the details",Toast.LENGTH_SHORT).show();
        }
        else
         {
            if(calendar_date.getText().toString().trim().matches("^(2[0-9][0-9]{2})-([0-9]|1[01])-([1-9]|[12][0-9]|3[01])*$") && calendar_time.getText().toString().trim().matches("^([0-9]|1[0-9]|2[0-3]):([0-9]|[1-5][0-9]|60)*$"))
            {
                String[] arryofdte=calendar_date.getText().toString().trim().split("-",3);
                year=arryofdte[0];
                month=arryofdte[1];
                day=arryofdte[2];
                String[] arryoftim=calendar_time.getText().toString().trim().split(":",2);
                hour=arryoftim[0];
                minute=arryoftim[1];
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,Integer.parseInt(year));
                cal.set(Calendar.MONTH,Integer.parseInt(month));
                cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day));
                cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour));
                cal.set(Calendar.MINUTE,Integer.parseInt(minute));
                if(cal.getTimeInMillis()-System.currentTimeMillis()<0)
                {
                    calendar_date.setError("please fill the valid date");
//                    Toast.makeText(taskadder.this,"please Fill correct date and time",Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(taskadder.this,MainPage.class));
                }
                else {
                    string_taskname = taskname.getText().toString().trim();
                    databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks");
                    databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            alredytasks.clear();
                            GenericTypeIndicator<Map<String,Task>>gti=new GenericTypeIndicator<Map<String, Task>>() {
                            };
                            Map<String,Task> mp=snapshot.getValue(gti);
                            try {
                                for (Map.Entry<String,Task> tsk:mp.entrySet())
                                {
                                    if(string_taskname.equals(tsk.getKey()))
                                    {
                                        contain=true;
                                        taskname.setError("Task name already exists");
//                                        Toast.makeText(taskadder.this,"This Task Name Already Exist",Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    alredytasks.add(tsk.getKey());
                                }
                            }
                            catch (Exception e)
                            {

                            }

                            if(!contain)
                            {
                                addenteredtask();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
            else {
                calendar_date.setError("Fill date in correct Format");
                calendar_time.setError("Fill time in correct Format");
//                Toast.makeText(taskadder.this,"please Fill date and time in correct Formate",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(taskadder.this,MainPage.class));

            }


        }
    }

    private void addenteredtask() {
        String taskuid = uid.toString() + " " + string_taskname;
        String value="0";
        if (star == true) {
            value="1";

//            DatabaseReference dbr;
//            dbr = firebaseDatabase.getReference(uid.toString()).child("StarredTasks").child(string_taskname);
//            dbr.setValue(string_taskname);
        }
        Task task = new Task(uid, percentage_completion, string_taskname, taskuid, hour, minute, day, month, year,value);
        databaseReference = firebaseDatabase.getReference(uid.toString()).child("Tasks").child(string_taskname);

        databaseReference.setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "Task added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(taskadder.this, Tasksdashboard.class));

                } else {
                    Toast.makeText(getApplicationContext(), "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(taskadder.this, Tasksdashboard.class));
                }
            }
        });
    }


    public void taskadder(View view) {
        addnewtask();
        finish();
        startActivity(new Intent(taskadder.this,Tasksdashboard.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }

}
