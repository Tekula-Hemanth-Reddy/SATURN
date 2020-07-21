package com.example.saturn.slider;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.example.saturn.SplashActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1 ;

    private  static final int REQUEST_CODE_LOCATION_PERMISSION=1;

    private ViewPager mpager;

    private int[] layouts={R.layout.fourthslide,R.layout.thirdslide,R.layout.secondslide,R.layout.firstslide,R.layout.fifthslide,R.layout.slidersaturn};

    private MpagerAdapter mpagerAdapter;

    private LinearLayout DotsLayout;
    private ImageView[] dots;

    private Button Btnnext,Btnskip;


    protected void onCreate(Bundle savedInstanceState)
    {

        if(new PreferenceManager(this).checkPreference())
        {
            loadhome();
        }



        super.onCreate(savedInstanceState);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                AppOpsManager appOps = (AppOpsManager)
                        getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), getPackageName());

                if (mode != AppOpsManager.MODE_ALLOWED) {
                    Toast.makeText(WelcomeActivity.this, "Please give Permission for App SATURN", Toast.LENGTH_LONG).show();
                    startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                }

            }






        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(WelcomeActivity.this, "Please give Permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(WelcomeActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        }

        mpager=(ViewPager)findViewById(R.id.viewPager);
        mpagerAdapter=new MpagerAdapter(layouts,this);
        mpager.setAdapter(mpagerAdapter);



        DotsLayout=(LinearLayout)findViewById(R.id.dotslayout);

        Btnnext=(Button)findViewById(R.id.btnnext);
        Btnskip=(Button)findViewById(R.id.btnskip);

        Btnnext.setOnClickListener(this);
        Btnskip.setOnClickListener(this);

        createDots(0);

        mpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);

                if(position== layouts.length-1)
                {
                    Btnnext.setText("Start");
                    Btnskip.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Btnnext.setText("Next");
                    Btnskip.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void createDots(int current_position)
    {
        if(DotsLayout!=null)
        {
            DotsLayout.removeAllViews();
        }
        dots=new ImageView[layouts.length];

        for(int i=0;i<layouts.length;i++)
        {
            dots[i]=new ImageView(this);

            if(i==current_position)
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dots));
            }
            else
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_dots));
            }

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(4,0,4,0);
            DotsLayout.addView(dots[i],params);

        }

    }

    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.btnnext:
                loadNextSlide();
                break;
            case R.id.btnskip :
                loadhome();
                new PreferenceManager(this).writePreference();
                break;

        }

    }

    private void loadhome()
    {

        startActivity(new Intent(this, SplashActivity.class));
        finish();


    }

    private void loadNextSlide()
    {

        int next_slide=mpager.getCurrentItem()+1;
        if(next_slide < layouts.length)
        {

            mpager.setCurrentItem(next_slide);
        }
        else
        {
            loadhome();
            new PreferenceManager(this).writePreference();
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Sorry,Permission Denied by you",Toast.LENGTH_SHORT).show();
            }
        }
    }



}
