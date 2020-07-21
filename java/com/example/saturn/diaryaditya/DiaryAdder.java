package com.example.saturn.diaryaditya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DiaryAdder extends AppCompatActivity
{


    TextView matterdate;
    EditText matter,mattertitle;
    Button adddiarybtn;

    String day,year,month;

    int var=0;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    DatabaseReference databaseReference1,databaseReference2,databaseReference3;

    String uid;





    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_diary_adder);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid().toString();

        matterdate=(TextView)findViewById(R.id.diaryadder_date);
        matter=(EditText)findViewById(R.id.dairyadder_writediary);
        adddiarybtn=(Button)findViewById(R.id.diaryadder_addbtn);
        mattertitle=(EditText)findViewById(R.id.diaryadder_title);

        matterdate.setText("");
        matter.setText("");
        mattertitle.setText("");

        set_text_for_date();

        //first check whether today whether the person has written some diary already;

        //so first get the  current date ;

        //checkit now;

        //first check if the diary list is empty or not if not empty check again in that date

        databaseReference=firebaseDatabase.getReference(uid.toString()).child("Diary");

        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.hasChildren())
                {
                    //it means that yet no tasks are being added;
                    //so add the present today task

//                    Toast.makeText(getApplicationContext(),"This is the first memory you are adding",Toast.LENGTH_SHORT).show();

                    databaseReference1=firebaseDatabase.getReference(uid.toString()).child("Diary").child(year+" "+month+" "+day);

                    Toast.makeText(getApplicationContext(),"Start your Diary",Toast.LENGTH_SHORT).show();

                    var=1;


                }
                else
                {
                    //it means that it has already somee diaries;
                    //so check the matching content;

                    //so check whether if the user has already written something today; if not crete new;

                    GenericTypeIndicator<Map<String,Diary>> gti = new GenericTypeIndicator<Map<String,Diary>>()
                    {

                    };
                    Map<String,Diary> diaries = snapshot.getValue(gti);
                    for (Map.Entry<String,Diary> entry : diaries.entrySet())
                    {
                        if(entry.getValue().day.equals(day) && entry.getValue().month.equals(month) && entry.getValue().year.equals(year))
                        {
                            //so if var==2 then already todays diary exsists.. so directly copy the text so that they can edit directly
                            var=2;

                            mattertitle.setText(entry.getValue().title);
                            matter.setText(entry.getValue().matter);

                            break;

                        }

                    }

                }

            }


            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adddiarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savediary();



            }
        });



    }

    private void savediary() {
        if(mattertitle.getText().equals(""))
        {
            mattertitle.setError("Please provide a Title");
        }
        if(matter.getText().equals(""))
        {
            matter.setError("Add your Memory");

        }
        else
        {

            Diary diary=new Diary(day,matter.getText().toString(),mattertitle.getText().toString(),month,year);

            databaseReference=firebaseDatabase.getReference(uid.toString()).child("Diary").child(year+" "+month+" "+day);

            databaseReference.setValue(diary).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
//                        Toast.makeText(getApplicationContext(),"Memory Added Succesfully",Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(DiaryAdder.this, MainPage.class));

                    }
                    else
                    {
//                        Toast.makeText(getApplicationContext(),"Memory did not add Succesfully",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }


    private void set_text_for_date()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());


        matterdate.setText((currentDateandTime.substring(0,10)));


        year=currentDateandTime.substring(0,4);
        month=currentDateandTime.substring(5,7);
        day=currentDateandTime.substring(8,10);

        matterdate.setText(year+" "+month+" "+day);




    }


    public void diaryadderstarn(View view) {
        savediary();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }

}
