package com.example.saturn.diaryaditya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class view_diary extends AppCompatActivity {
    TextView date, title, memory;

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String uid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_diary);

        String childname = getIntent().getStringExtra("Childname");

        date = (TextView) findViewById(R.id.diaryitemviewer_date);
        title = (TextView) findViewById(R.id.diaryitemviewer_title);
        memory = (TextView) findViewById(R.id.dairyitemviewer_memorydiary);

        date.setText("");
        title.setText("");
        memory.setText("");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        uid = firebaseAuth.getUid().toString();


        databaseReference = firebaseDatabase.getReference(uid.toString()).child("Diary").child(childname);

        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Diary d = (Diary) dataSnapshot.getValue(Diary.class);

                memory.setText(d.matter);
                title.setText(d.title);
                date.setText(d.year+"-"+d.month+"-"+d.day);


            }

            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    public void diaryfinal(View view) {
        finish();
        startActivity(new Intent(view_diary.this, Dateselector.class));
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }

}
