package com.example.saturn;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.saturn.diaryaditya.Dateselector;
import com.example.saturn.diaryaditya.DiaryAdder;
import com.example.saturn.prfacc.MainActivity;
import com.example.saturn.prfacc.Profile;
import com.example.saturn.prfacc.UserProfile;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.location.Address;
import android.location.Geocoder;

import android.os.Looper;
import android.os.StrictMode;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import okhttp3.Callback;
import okhttp3.Response;


public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FrameLayout frameLayout;

    //    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mydata;
    DrawerLayout drawer;
    NavigationView navigationView;


    ImageView imv;
    TextView nam, wis;
    TextView txtnamenav;

    TextView datetext, timetext, citytext, descriptiontext, temperaturetext;
    ImageView weatherimage, townimage;
    ProgressBar progressBar;

    Animation top_anim;

    double latitude, longitude;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mydata = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Profile");

        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_add, R.id.nav_show,R.id.nav_send)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        imv = header.findViewById(R.id.imageView);
        txtnamenav = header.findViewById(R.id.name);

//        nam=findViewById(R.id.wishname);
//        wis=findViewById(R.id.wish);
//
//

        mydata.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile up = dataSnapshot.getValue(UserProfile.class);
//                nam.setText(up.Name);
                txtnamenav.setText(up.Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        frameLayout=(FrameLayout)findViewById(R.id.fragment_container);
//        Calendar c=Calendar.getInstance();
//        int timeofday=c.get(Calendar.HOUR_OF_DAY);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (timeofday >= 0 && timeofday < 12) {
//                wis.setText("GOOD MORNING");
//
//                frameLayout.setBackground(getDrawable(R.drawable.gm));
//
//            } else if (timeofday >= 12 && timeofday < 16) {
//
//                wis.setText("GOOD AFTERNOON");
//                frameLayout.setBackground(getDrawable(R.drawable.gm));
//            } else if (timeofday >= 16 && timeofday < 21) {
//
//                wis.setText("GOOD EVENING");
//                frameLayout.setBackground(getDrawable(R.drawable.gm));
//            } else if (timeofday >= 21 && timeofday < 24) {
//
//                wis.setText("GOOD NIGHT");
//                frameLayout.setBackground(getDrawable(R.drawable.gn));
//            }
//        }
//        else
//        {
//            wis.setText("hello");
//        }
//
        datetext = (TextView) findViewById(R.id.weather_DD_YY_MM);
        timetext = (TextView) findViewById(R.id.weather_HHMMSSZONE);
        citytext = findViewById(R.id.weather_cityname);
        descriptiontext = findViewById(R.id.weather_desc);
        temperaturetext = findViewById(R.id.weather_temperature);

        progressBar = (ProgressBar) findViewById(R.id.weather_progressbar);

        weatherimage = findViewById(R.id.weather_image);
        townimage = (ImageView) findViewById(R.id.weather_town);

        top_anim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        datetext.setText("");
        timetext.setText("");
        citytext.setText("");
        descriptiontext.setText("");
        temperaturetext.setText("");

        datefun();
        locationfun();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_apps:
                finish();
                startActivity(new Intent(this, Appsdashboard.class));
                break;
            case R.id.nav_tasks:
                finish();
                startActivity(new Intent(getApplicationContext(), Tasksdashboard.class));
                break;
            case R.id.nav_home:
                //startActivity(new Intent(this,information.class));
                break;
            case R.id.nav_dairy:
                finish();
                startActivity(new Intent(getApplicationContext(), DiaryAdder.class));
                break;
            case R.id.nav_view_last_entries:
                finish();
                startActivity(new Intent(getApplicationContext(), Dateselector.class));
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainPage.this, MainActivity.class));
                Toast.makeText(MainPage.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.action_profile: {
                finish();
                startActivity(new Intent(MainPage.this, Profile.class));
                break;
            }
            case R.id.action_about: {
                finish();
                startActivity(new Intent(MainPage.this, About.class));
                break;
            }
            case R.id.action_settings: {
                finish();
                startActivity(new Intent(MainPage.this, SettingsSaturn.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void locationfun() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getcurrentlocation();
        }


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getcurrentlocation();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry,Permission Denied by you", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getcurrentlocation() {

        progressBar.setVisibility(View.VISIBLE);
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        LocationServices.getFusedLocationProviderClient(MainPage.this).requestLocationUpdates(locationRequest, new LocationCallback() {

            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MainPage.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestlocationIndex = locationResult.getLocations().size() - 1;
                    latitude = locationResult.getLocations().get(latestlocationIndex).getLatitude();
                    longitude = locationResult.getLocations().get(latestlocationIndex).getLongitude();


                    try {
                        Geocoder geocoder = new Geocoder(MainPage.this);
                        List<Address> addresses = null;
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String city = addresses.get(0).getLocality();

                        citytext.setText(String.format("%s", city));

                        weatherforecast();
                        progressBar.setVisibility(View.GONE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }, Looper.getMainLooper());
    }



    private void weatherforecast()
    {

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=1e5ffb83925716c066798dc87a62dd1e&units=metric")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response= client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData= response.body().string();
                    try {
                        JSONObject json=new JSONObject(responseData);
                        JSONArray array=json.getJSONArray("weather");
                        JSONObject object=array.getJSONObject(0);

                        String description=object.getString("description");
                        String icons = object.getString("icon");

                        JSONObject temp1= json.getJSONObject("main");
                        Double Temperature=temp1.getDouble("temp");



                        String temps=Math.round(Temperature)+" °C";


                        setText(temperaturetext,temps);
                        setText(descriptiontext,description);
                        setImage(weatherimage,icons);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
          });
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private void setText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    private void setImage(final ImageView imageView, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //paste switch
                switch (value){
                    case "01d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "01n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "02d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "02n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "03d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "03n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "04d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "04n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "09d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "09n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "10d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "10n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "11d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "11n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "13d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    case "13n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    default:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.wheather));

                }
            }
        });
    }






    private void init()
    {

        datetext=(TextView)findViewById(R.id.weather_DD_YY_MM);
        timetext=(TextView)findViewById(R.id.weather_HHMMSSZONE);
        citytext=findViewById(R.id.weather_cityname);
        descriptiontext=findViewById(R.id.weather_desc);
        temperaturetext=findViewById(R.id.weather_temperature);

        progressBar=(ProgressBar)findViewById(R.id.weather_progressbar);

        weatherimage=findViewById(R.id.weather_image);
        townimage=(ImageView)findViewById(R.id.weather_town);

        top_anim= AnimationUtils.loadAnimation(this,R.anim.top_animation);

        datetext.setText("");
        timetext.setText("");
        citytext.setText("");
        descriptiontext.setText("");
        temperaturetext.setText("");

        townimage.setAnimation(top_anim);



    }

    private void datefun()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());

        datetext.setText("     "+currentDateandTime.substring(0,10));
        String ampm;
        if(Integer.parseInt(currentDateandTime.substring(17,19))>12)
        {
            ampm="PM";
        }
        else
        {
            ampm="AM";
        }
        timetext.setText("     "+currentDateandTime.substring(17,22)+" "+ampm+" "+currentDateandTime.substring(26));


    }


}
