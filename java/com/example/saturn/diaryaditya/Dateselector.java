package com.example.saturn.diaryaditya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class Dateselector extends AppCompatActivity
{

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String uid;

    int var=0;
    Calendar calendar;

    String tyear,tmonth,tday,fyear,fday,fmonth;

    int a,b,c;

    FloatingActionButton calendarbutton;

    public static String todaysdate;
    String selecteddate;

    ImageView submitbtn;
    RecyclerView alldateslist;
    DiaryAdapter myadapter;
    ArrayList<Diary>dates=new ArrayList<Diary>();


//    TextView sample;

    TextInputEditText datetextinput;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int i=R.layout.activity_dateselector;
        setContentView(i);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid().toString();

        alldateslist=(RecyclerView)findViewById(R.id.recyclerlistalldates);


        calendarbutton=(FloatingActionButton)findViewById(R.id.dateselector_calendarbutton);

        submitbtn=(ImageView) findViewById(R.id.dateselector_submitbtn);

        datetextinput=(TextInputEditText)findViewById(R.id.dateselector_datetext);
        todaysdate=todaysdatefun();


        databaseReference=firebaseDatabase.getReference(uid.toString()).child("Diary");

        //now make the recycler view for the diary...............


        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dates.clear();
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getApplicationContext(), "Start your Diary", Toast.LENGTH_SHORT).show();

                } else {

                    GenericTypeIndicator<Map<String, Diary>> gti = new GenericTypeIndicator<Map<String,Diary>>() {
                    };
                    Map<String,Diary> tasknames = dataSnapshot.getValue(gti);
                    for (Map.Entry<String,Diary> entry : tasknames.entrySet()) {
                        dates.add(entry.getValue());
                    }

                    myadapter = new DiaryAdapter(Dateselector.this,dates);
                    alldateslist.setAdapter(myadapter);
                    alldateslist.setLayoutManager(new LinearLayoutManager(Dateselector.this));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        calendarbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                calendar=Calendar.getInstance();

                a=calendar.get(Calendar.YEAR);
                b=calendar.get(Calendar.MONTH);
                c=calendar.get(Calendar.DAY_OF_MONTH);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(Dateselector.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth1) {

                            month1=month1+1;
                            datetextinput.setText(year1+"-"+month1+"-"+dayOfMonth1);
                        }
                    },a,b,c);
                    datePickerDialog.show();

                }
            }
        });



        submitbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                //check whether the date text format is correct or not;

                if(datetextinput.getText().equals(""))
                {
                    datetextinput.setError("Provide a valid calander date");
                }
                else
                {
                    if(datetextinput.getText().toString().trim().matches("^(2[0-9][0-9]{2})-([0-9]|1[01])-([1-9]|[12][0-9]|3[01])*$"))
                    {
                        String[] arrOfStr = datetextinput.getText().toString().split("-", 3);

                        tyear=arrOfStr[0];
                        tmonth=arrOfStr[1];
                        tday=arrOfStr[2];


                        fyear=tyear;
                        if(Integer.parseInt(tmonth) < 10)
                        {
                            fmonth="0"+tmonth;
                        }
                        else
                        {
                            fmonth=tmonth;
                        }
                        if(Integer.parseInt(tday) < 10)
                        {
                            fday="0"+tday;
                        }
                        else
                        {
                            fday=tday;
                        }

                        selecteddate=fyear+" "+fmonth+" "+fday;


                        if(selecteddate.equals(todaysdate))
                        {
                            datetextinput.setError("You cannot select Today's date");
                            startActivity(new Intent(Dateselector.this,DiaryAdder.class));
//                            Toast.makeText(Dateselector.this, "Go to Dear Diary to see today's entry", Toast.LENGTH_LONG).show();
                            datetextinput.setText("");

                        }
                        else
                        {


                                databaseReference=firebaseDatabase.getReference(uid.toString()).child("Diary");
                                databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {

                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(!snapshot.hasChildren())
                                        {
                                            //it means that yet no tasks are being added;
                                            //so add the present today task

                                            Toast.makeText(getApplicationContext(),"Start your Diary",Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            GenericTypeIndicator<Map<String,Diary>> gti = new GenericTypeIndicator<Map<String,Diary>>()
                                            {

                                            };
                                            Map<String,Diary> diaries = snapshot.getValue(gti);
                                            for (Map.Entry<String,Diary> entry : diaries.entrySet())
                                            {
                                                if(entry.getValue().day.equals(fday) && entry.getValue().month.equals(fmonth) && entry.getValue().year.equals(fyear))
                                                {
                                                    var=1;
                                                    break;

                                                }

                                            }

                                            if(var==1)
                                            {

                                                Intent intent=new Intent(Dateselector.this,view_diary.class);
                                                intent.putExtra("Childname",selecteddate);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                datetextinput.setError("No entry made on that Day");

                                            }

                                        }

                                    }
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

//                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(Dateselector.this,"please fill the date in correct format",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private String todaysdatefun()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        Calendar td=Calendar.getInstance();
        String currentDateandTime = sdf.format(new Date());





        String year1=currentDateandTime.substring(0,4);
        String month1=currentDateandTime.substring(5,7);
        String day1=currentDateandTime.substring(8,10);

        datetextinput.setHint(year1+"-"+month1+"-"+day1);
        datetextinput.setHintTextColor(getResources().getColor(R.color.Red));

        return(year1+" "+month1+" "+day1);

    }



    public void diaryshownselecter(View view) {
        finish();
        startActivity(new Intent(Dateselector.this, MainPage.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }
}
