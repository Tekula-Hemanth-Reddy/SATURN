package com.example.saturn.prfacc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PassReset extends AppCompatActivity {

    TextInputEditText emailtxt;
    Button rest;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pass_reset);

        emailtxt = (TextInputEditText) findViewById(R.id.premailreset);
        rest = (Button) findViewById(R.id.prresetbt);
        firebaseAuth = FirebaseAuth.getInstance();

        rest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mail = emailtxt.getText().toString().trim();
                if (mail.isEmpty()) {
                    emailtxt.setError("Please enter your mail ID");

//                    Toast.makeText(PassReset.this,"please enter mail",Toast.LENGTH_SHORT).show();

                } else {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(PassReset.this,"password reset link has been sent to your mail",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PassReset.this, MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(PassReset.this,"Password reset FAILED!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    public void noneedtochange(View view) {
        Toast.makeText(PassReset.this,"Password reset FAILED!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PassReset.this,MainActivity.class));
    }
}
