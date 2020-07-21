package com.example.saturn.prfacc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextView nameprofile,mailprofile,phnoprofile;
    TextView prfnme,prfmil,prfphn;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        nameprofile=(TextView)findViewById(R.id.profilename);
        mailprofile=(TextView)findViewById(R.id.profilemail);
        phnoprofile=(TextView)findViewById(R.id.profilphonenumber);

        prfnme=(TextView)findViewById(R.id.nameprofiletext);
        prfmil=(TextView)findViewById(R.id.mailprofiletext);
        prfphn=(TextView)findViewById(R.id.phnoprofiletext);

        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile up =snapshot.getValue(UserProfile.class);
                nameprofile.setText(up.Name);
                mailprofile.setText(up.Mail);
                phnoprofile.setText(up.PhoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void appsprofile(View view) {
        finish();
        startActivity(new Intent(Profile.this, MainPage.class));
    }

    public void editdetails(View view) {
        Intent itnt=new Intent(Profile.this,EditDetails.class);
        Pair[] pairs=new Pair[6];
        pairs[0]=new Pair<View,String>(nameprofile,"profileeditname");
        pairs[1]=new Pair<View,String>(mailprofile,"profileeditmail");
        pairs[2]=new Pair<View,String>(phnoprofile,"profileeditphonenumber");
        pairs[3]=new Pair<View,String>(prfnme,"profiletextname");
        pairs[4]=new Pair<View,String>(prfmil,"profiletextmail");
        pairs[5]=new Pair<View,String>(prfphn,"profiletextphonenumber");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(Profile.this,pairs);
            finish();
            startActivity(itnt,activityOptions.toBundle());
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }
}
