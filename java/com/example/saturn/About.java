package com.example.saturn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about);
    }

    public void aboutsaturn(View view) {
        finish();
        startActivity(new Intent(About.this,MainPage.class));
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }
}
