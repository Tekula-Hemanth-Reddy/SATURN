package com.example.saturn.sctaps;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.Appsdashboard;
import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.example.saturn.appstatisticdetails.AppstatsAdapter;
import com.example.saturn.appstatisticdetails.Appstatsinfo;
import com.example.saturn.appstatisticdetails.app_used_stats_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class App_select_show_data extends AppCompatActivity {

    ListView listViewappstats;

    EditText searchtxt;
    ImageView searchbutton;
    UsageStatsManager usageStatsManager;
    List<UsageStats> queryUsageStats;
    List<Appstatsinfo> appsstats = new ArrayList<>();
    List<Appstatsinfo> appsstatsselected = new ArrayList<>();
    Boolean setvalue=false;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public static ArrayList<String> appsselectedlist=new ArrayList<>();

    Context context;
    PackageManager packageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_select_show_data);

        appsselectedlist=new ArrayList<String>();

        listViewappstats=findViewById(R.id.appslistappsofphone);
        searchtxt = (EditText) findViewById(R.id.appssearchappsid);
        searchbutton = (ImageView) findViewById(R.id.appssearchimagebutton);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        context=App_select_show_data.this;
        packageManager=context.getPackageManager();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queryUsageStats= usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, cal.getTimeInMillis(), System.currentTimeMillis());
            takeitems();

            searchbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!searchtxt.getText().toString().trim().isEmpty())
                    {
                        appsstats.clear();
                        String str=searchtxt.getText().toString().trim();
                        String rts= String.valueOf(str.charAt(0)).toUpperCase();

                        for(UsageStats pkgsts : queryUsageStats)
                        {
                            Appstatsinfo app = new Appstatsinfo();
                            Context ctx = getApplicationContext();
                            PackageManager packageManager = ctx.getPackageManager();
                            ApplicationInfo info = null;
                            try {
                                info = packageManager.getApplicationInfo(pkgsts.getPackageName(), PackageManager.GET_META_DATA);
                                String start = (String) packageManager.getApplicationLabel(info);
                                app.appname=pkgsts.getPackageName();
                                app.usedtimetoday= pkgsts.getTotalTimeInForeground() / 1000;
                                app.lastuseddate= (String) DateUtils.formatSameDayTime(pkgsts.getLastTimeUsed(),
                                        System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
                                if(start.startsWith(str.replace(str.charAt(0),rts.charAt(0))) && app.usedtimetoday!=0 && app.lastuseddate!=null && app.appname!=null){
                                    appsstats.add(app);}

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                        if(appsstats.isEmpty())
                        {
                            Toast.makeText(App_select_show_data.this,"No Apps till now",Toast.LENGTH_SHORT).show();
                        }
                        listViewappstats.setAdapter(new AppstatsAdapter(App_select_show_data.this,appsstats));

                    }
                    else {
                        takeitems();
                    }

                }
            });

        }


    }

    private void takeitems() {

        appsstats.clear();
        for(UsageStats pkgsts : queryUsageStats)
        {
            Appstatsinfo app = new Appstatsinfo();
            app.appname=pkgsts.getPackageName();
            app.usedtimetoday= pkgsts.getTotalTimeInForeground() / 1000;
            app.lastuseddate= (String) DateUtils.formatSameDayTime(pkgsts.getLastTimeUsed(),
                    System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
            if(app.usedtimetoday!=0 && app.lastuseddate!=null && app.appname!=null){
                appsstats.add(app);}
        }

        listViewappstats.setAdapter(new AppstatsAdapter(App_select_show_data.this,appsstats));
//        setecteditems();
    }

    private void setecteditems() {
        appsselectedlist.clear();
        appsstatsselected.clear();
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Selected Apps");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                GenericTypeIndicator<Map<String,String>> gtindicator=new GenericTypeIndicator<Map<String,String>>(){};
//                Map<String,String> names =snapshot.getValue(gtindicator);
                try {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String str=ds.getValue(String.class);
                        appsselectedlist.add(str);
                    }
                    for(Appstatsinfo app:appsstats)
                    {
                        ApplicationInfo info = null;
                        try {
                            info = packageManager.getApplicationInfo(app.appname, PackageManager.GET_META_DATA);
                            String appName = (String) packageManager.getApplicationLabel(info);

                            if(appsselectedlist.contains(appName))
                            {
                                appsstatsselected.add(app);

                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    listViewappstats.setAdapter(new AppstatsAdapter(App_select_show_data.this,appsstatsselected));
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void sortbynames(View view) {
        takeitems();
        Collections.sort(appsstats,new App_select_show_data.oncomparatorusagetimeselected());
        listViewappstats.setAdapter(new AppstatsAdapter(App_select_show_data.this,appsstats));
    }

//    public void Addapps(View view) {
//        finish();
//        startActivity(new Intent(App_select_show_data.this, Apps_select.class));
//    }

    public void appselectshowdata(View view) {
        finish();
        startActivity(new Intent(App_select_show_data.this, Appsdashboard.class));
    }

    private  class oncomparatorusagetimeselected implements Comparator<Appstatsinfo> {

        @Override
        public int compare(Appstatsinfo o1, Appstatsinfo o2) {


            return (int)(-1*(o1.usedtimetoday-o2.usedtimetoday));
        }
    }

    public static ArrayList sendselectedappstoshowdata()
    {
        return appsselectedlist;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }
}
