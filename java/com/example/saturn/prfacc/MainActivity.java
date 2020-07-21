package com.example.saturn.prfacc;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements otpdialoglyt.otpdialoglistener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;;

    TextInputLayout loginmailbox,loginpasswordbox;
    TextInputEditText loginmail;
    TextInputEditText loginpassword;
    TextView loginforgetpassword,loginnewuser;
    Button loginlogin;

    private String verificationid;
    Animation botomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        botomanim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        loginmail=(TextInputEditText) findViewById(R.id.lgnmail);
        loginpassword=(TextInputEditText)findViewById(R.id.lgnpassword);
        loginforgetpassword=(TextView)findViewById(R.id.lgnforgetpassword);
        loginnewuser=(TextView)findViewById(R.id.lgnnewuser);
        loginlogin=(Button)findViewById(R.id.lgnlogin);
        loginmailbox=(TextInputLayout)findViewById(R.id.lgnmailbox);
        loginpasswordbox=(TextInputLayout)findViewById(R.id.lgnpasswordbox);

        loginmail.setAnimation(botomanim);
        loginpassword.setAnimation(botomanim);
        loginforgetpassword.setAnimation(botomanim);
        loginnewuser.setAnimation(botomanim);
        loginlogin.setAnimation(botomanim);
        loginmailbox.setAnimation(botomanim);
        loginpasswordbox.setAnimation(botomanim);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            finish();
//            startActivity(new Intent(MainActivity.this, Appsdashboard.class));
            startActivity(new Intent(MainActivity.this, MainPage.class));
        }
        loginnewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Registration.class);

                Pair[] pairs=new Pair[1];
                pairs[0]=new Pair<View,String>(loginmailbox,"login_newuser");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,activityOptions.toBundle());
                }
//                startActivity(new Intent(MainActivity.this, Appsdashboard.class));
            }
        });
        loginlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("please wait");
                progressDialog.show();
                if(filled())
                {
                    String mail=loginmail.getText().toString().trim();
                    String password=loginpassword.getText().toString().trim();
                    firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                checkEmailVerification();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginforgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, PassReset.class);

                Pair[] pairs=new Pair[2];
                pairs[0]=new Pair<View,String>(loginmailbox,"login_mail");
                pairs[1]=new Pair<View,String>(loginforgetpassword,"login_forgetpassword");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,activityOptions.toBundle());
                }
            }
        });
    }

    private boolean filled() {
        String mail=loginmail.getText().toString().trim();
        String password=loginpassword.getText().toString().trim();
        if(mail.isEmpty() || password.isEmpty())
        {
            progressDialog.dismiss();
            loginmail.setError("Please fill all the details");
            loginpassword.setError("Please fill all the details");
//            Toast.makeText(this,"Please fill all details",Toast.LENGTH_SHORT).show();
            return false;
        }
            return true;
        }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();
        if (emailflag) {
            finish();
            startActivity(new Intent(MainActivity.this, MainPage.class));


//            final String phno;
//            DatabaseReference dbr=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");
//            dbr.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    UserProfile up=snapshot.getValue(UserProfile.class);
////                    sendVerificationCode(up.PhoneNumber);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

//            createdialogforotp();

//            startActivity(new Intent(MainActivity.this, Appsdashboard.class));
        } else {
            Toast.makeText(this, "Verify with your email, a link has been sent to it", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private void createdialogforotp() {

        otpdialoglyt dilg=new otpdialoglyt();
        dilg.show(getSupportFragmentManager(),"Otpdialoglyt");
    }

    @Override
    public void applyText(String code) {

        if ((code.isEmpty() || code.length() < 6)){
            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();

            return;
        }
        else {
            verifyCode(code);
        }

    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);

        finish();
        startActivity(new Intent(MainActivity.this, MainPage.class));

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

}
