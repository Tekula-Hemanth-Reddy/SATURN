package com.example.saturn.appstatisticdetails;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.Appsdashboard;
import com.example.saturn.MainPage;
import com.example.saturn.R;

import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class app_used_stats_info extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listViewappstats;
    Spinner spinnerappstats;
    EditText searchtxt;
    ImageView searchbutton;
    UsageStatsManager usageStatsManager;
    List<UsageStats> queryUsageStats;
    List<Appstatsinfo> appsstats = new ArrayList<>();
    Boolean setvalue=false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_used_stats_info);

        listViewappstats=findViewById(R.id.listappsstatofphone);
        spinnerappstats=findViewById(R.id.typeSpinner);
        spinnerappstats.setOnItemSelectedListener(this);
        searchtxt = (EditText) findViewById(R.id.searchappsidstats);
        searchbutton = (ImageView) findViewById(R.id.searchimagebuttonstats);





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        queryUsageStats= usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(), System.currentTimeMillis());
        takedetails();

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
                        searchtxt.setError("Enter valid name");
//                        Toast.makeText(app_used_stats_info.this,"Give Correct Name",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
//                    appsstats.clear();
//                    for(UsageStats pkgsts : queryUsageStats)
//                    {
//                        Appstatsinfo app = new Appstatsinfo();
//                        app.appname=pkgsts.getPackageName();
//                        app.usedtimetoday= pkgsts.getTotalTimeInForeground() / 1000;
//                        app.lastuseddate= (String)DateUtils.formatSameDayTime(pkgsts.getLastTimeUsed(),
//                                System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
//                        if(app.usedtimetoday!=0){
//                            appsstats.add(app);}
//                    }
                    takedetails();
                }
                listViewappstats.setAdapter(new AppstatsAdapter(app_used_stats_info.this,appsstats));
            }
        });

        listViewappstats.setAdapter(new AppstatsAdapter(app_used_stats_info.this,appsstats));
    }

    private void takedetails() {
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        takedetails();
        if(position==1)
        {
            Collections.sort(appsstats,new oncomparatorappname());
            listViewappstats.setAdapter(new AppstatsAdapter(app_used_stats_info.this,appsstats));
        }
        else
        {
            Collections.sort(appsstats,new oncomparatorusagetime());
            listViewappstats.setAdapter(new AppstatsAdapter(app_used_stats_info.this,appsstats));

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void appsusedstatsinfo(View view) {
        finish();
        startActivity(new Intent(app_used_stats_info.this, Appsdashboard.class));
    }

    private  class oncomparatorappname implements Comparator<Appstatsinfo> {

        @Override
        public int compare(Appstatsinfo o1, Appstatsinfo o2) {


            Context ctx = getApplicationContext();
            PackageManager packageManager = ctx.getPackageManager();
            ApplicationInfo info = null;
            ApplicationInfo ofni = null;
            CharSequence sa="",sb="";
            try {
                info = packageManager.getApplicationInfo(o1.appname, PackageManager.GET_META_DATA);
                ofni = packageManager.getApplicationInfo(o2.appname, PackageManager.GET_META_DATA);
               sa= (String) packageManager.getApplicationLabel(info);
                sb = (String) packageManager.getApplicationLabel(ofni);


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

            }
            return Collator.getInstance().compare(sa.toString(), sb.toString());
        }
    }
    private  class oncomparatorusagetime implements Comparator<Appstatsinfo> {

        @Override
        public int compare(Appstatsinfo o1, Appstatsinfo o2) {


            return (int)(-1*(o1.usedtimetoday-o2.usedtimetoday));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }
}
