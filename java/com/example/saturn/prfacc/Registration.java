package com.example.saturn.prfacc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    TextInputLayout registermailbox,registernamebox,registerphonenumberbox,registerpasswordbox,registerconpasswordbox;
    TextInputEditText registermail,registername,registerphonenumber,registerpassword,registerconpassword;
    TextView registeralreadyauser,registerwelcome,registersaturn;
    Button registersign;
    Spinner spinner;

    Animation top,botom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        botom=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        registername=(TextInputEditText)findViewById(R.id.regname);
        registermail=(TextInputEditText)findViewById(R.id.regmail);
        registerphonenumber=(TextInputEditText)findViewById(R.id.regphonenumber);
        registerpassword=(TextInputEditText)findViewById(R.id.regpassword);
        registerconpassword=(TextInputEditText)findViewById(R.id.regconpassword);
        registeralreadyauser=(TextView)findViewById(R.id.regalreadyauser);
        registersign=(Button)findViewById(R.id.regsignin);
        spinner=(Spinner)findViewById(R.id.phonespinnereditdetails);

        registernamebox=(TextInputLayout) findViewById(R.id.regnamebox);
        registermailbox=(TextInputLayout)findViewById(R.id.regmailbox);
        registerphonenumberbox=(TextInputLayout)findViewById(R.id.regphonenumberbox);
        registerpasswordbox=(TextInputLayout)findViewById(R.id.regpasswordbox);
        registerconpasswordbox=(TextInputLayout)findViewById(R.id.regconpasswordbox);
        registerwelcome=(TextView)findViewById(R.id.regwelcome);
        registersaturn=(TextView)findViewById(R.id.regsaturn);


        registersaturn.setAnimation(top);
        registernamebox.setAnimation(botom);
        registermailbox.setAnimation(botom);
        registerphonenumberbox.setAnimation(botom);
        registerpasswordbox.setAnimation(botom);
        registerconpasswordbox.setAnimation(botom);
        registername.setAnimation(botom);
        registermail.setAnimation(botom);
        registerphonenumber.setAnimation(botom);
        registerpassword.setAnimation(botom);
        registerconpassword.setAnimation(botom);

        CountryData.countryAreaCodes=getResources().getStringArray(R.array.country_code);

        CountryData.countryNames=getResources().getStringArray(R.array.country_arrays);

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        registeralreadyauser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });

        registersign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=registermail.getText().toString().trim();
                String password=registerpassword.getText().toString().trim();
                if(isfilled())
                {
//                    if(CountryData.countryAreaCodes[spinner.getSelectedItemPosition()].isEmpty())
//                    {
//                        Toast.makeText(Registration.this, "Please Select Country Code", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
                        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registration.this, "Registration Succesfull", Toast.LENGTH_SHORT).show();
                                    createprofile();
                                    sendemailverification();
                                } else {
                                    Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(Registration.this, MainActivity.class));
                                }
                            }
                        });
//                    }
                }
            }
        });


    }

    private void createprofile() {

        String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        String name=registername.getText().toString().trim();
        String mail=registermail.getText().toString().trim();
        String phonenum=registerphonenumber.getText().toString().trim();// "+" + code +
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");
        UserProfile up=new UserProfile(name,mail,phonenum);
        databaseReference.setValue(up).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
//                    Toast.makeText(Registration.this,"Profile set",Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference dbrref=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Notifications");
        dbrref.child("Apps").setValue("No");
        dbrref.child("Tasks").setValue("No");
        dbrref.child("Diary").setValue("No");
    }

    private boolean isfilled() {
        String nam = registername.getText().toString().trim();
        String mai = registermail.getText().toString().trim();
        String pas = registerpassword.getText().toString().trim();
        String repas = registerconpassword.getText().toString().trim();
        String ph = registerphonenumber.getText().toString().trim();
        if(nam.isEmpty() || mai.isEmpty() || pas.isEmpty() || repas.isEmpty() || ph.isEmpty())
        {
            Toast.makeText(Registration.this,"Please fill all the details",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(ph.length()==10 && ph.matches("^[0-9]*$"))
            {
                //do nothing just for check
            }
            else
            {
                registerphonenumber.setError("Enter valid Phone number");
//                Toast.makeText(Registration.this,"Enter Valid Phone number ",Toast.LENGTH_LONG).show();
                return false;
            }
            if(pas.equals(repas))
            {
                if(pas.length()<6)
                {
                    registerpassword.setError("Minimum 7 characters");
//                    Toast.makeText(Registration.this,"Password must contain 6 letters One capital, one digit ",Toast.LENGTH_LONG).show();
                    return false;
                }
                else
                {
                    if(pas.matches("^[0-9a-zA-z!@#$%^&*]*$")) {
                        return true; }
                    else
                    {
                        registerpassword.setError("Password is weak");
//                        Toast.makeText(Registration.this,"Password must contain 6 letters",Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
            else
            {
                registerconpassword.setError("Miss match");
//                Toast.makeText(Registration.this,"Please Check Re-enter Password",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void sendemailverification() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Registration.this,"Verify with your email, a link has been sent to it",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this,MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Oops! something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
