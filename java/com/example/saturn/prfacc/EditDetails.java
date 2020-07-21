package com.example.saturn.prfacc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditDetails extends AppCompatActivity {

    TextInputEditText nameedit,mailedit,phnoedit;

    TextInputLayout namebox,mailbox,phnobox;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    UserProfile up;

//    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_details);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        nameedit=(TextInputEditText)findViewById(R.id.editprofilename);
        mailedit=(TextInputEditText)findViewById(R.id.editprofilemail);
        phnoedit=(TextInputEditText)findViewById(R.id.editprofilephonenumber);
//        spinner=(Spinner)findViewById(R.id.phonespinnereditdetails);

        namebox=(TextInputLayout)findViewById(R.id.editprofilenamebox);
        mailbox=(TextInputLayout)findViewById(R.id.editprofilemailbox);
        phnobox=(TextInputLayout)findViewById(R.id.editprofilephonenumberbox);

        CountryData.countryAreaCodes=getResources().getStringArray(R.array.country_code);

        CountryData.countryNames=getResources().getStringArray(R.array.country_arrays);

//        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));



        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                up=snapshot.getValue(UserProfile.class);
                nameedit.setText(up.Name);
                mailedit.setText(up.Mail);
                phnoedit.setText(up.PhoneNumber);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void appseditprofile(View view) {
        finish();
        startActivity(new Intent(EditDetails.this,Profile.class));
    }

    public void saveediteddetails(View view) {
        if(isfilleddetails())
        {
            UserProfile editeduserprofile=new UserProfile(nameedit.getText().toString().trim(),mailedit.getText().toString().trim(),phnoedit.getText().toString().trim());
            databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");
            databaseReference.setValue(editeduserprofile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful())
                  {
                      Toast.makeText(EditDetails.this,"Details Saved",Toast.LENGTH_SHORT).show();
                      Intent itnt=new Intent(EditDetails.this,Profile.class);
                      Pair[] pairs=new Pair[6];
                      pairs[0]=new Pair<View,String>(nameedit,"profileeditname");
                      pairs[1]=new Pair<View,String>(mailedit,"profileeditmail");
                      pairs[2]=new Pair<View,String>(phnoedit,"profileeditphonenumber");
                      pairs[3]=new Pair<View,String>(namebox,"profiletextname");
                      pairs[4]=new Pair<View,String>(mailbox,"profiletextmail");
                      pairs[5]=new Pair<View,String>(phnobox,"profiletextphonenumber");

                      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                          ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(EditDetails.this,pairs);
                          finish();
                          startActivity(itnt,activityOptions.toBundle());
                      }
                  }
                }
            });

        }

    }

    private boolean isfilleddetails() {
        String nam = nameedit.getText().toString().trim();
        String mai = mailedit.getText().toString().trim();
        String ph = phnoedit.getText().toString().trim();
        if(nam.isEmpty() || mai.isEmpty() || ph.isEmpty())
        {
            Toast.makeText(EditDetails.this,"Please fill all the details",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(ph.length()==10 && ph.matches("^[0-9]*$"))
            {
                return true;
            }
            else
            {
                phnoedit.setError("Enter Valid Phone number");
//                Toast.makeText(EditDetails.this,"Enter Valid Phone number ",Toast.LENGTH_LONG).show();
                return false;
            }

        }
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }
}
