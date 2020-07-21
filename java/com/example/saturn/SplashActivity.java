package com.example.saturn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.saturn.prfacc.MainActivity;

public class SplashActivity extends AppCompatActivity {


//    ImageView imageView;
//    TextView textView;
//
//    Animation topAnim;
//    Animation bottomAnim;

    private static int SPLASH_SCREEN =2500;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

//        imageView=(ImageView)findViewById(R.id.splash_img);
//        textView=(TextView)findViewById(R.id.splash_text);

//        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
//        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

//        imageView.setAnimation(topAnim);
//        textView.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {

            public void run() {
                Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}
