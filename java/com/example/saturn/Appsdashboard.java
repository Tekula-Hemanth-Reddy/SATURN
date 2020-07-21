package com.example.saturn;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.appdatainfo.LoadAppsiconsnames;
import com.example.saturn.appstatisticdetails.Appstatsinfo;
import com.example.saturn.appstatisticdetails.app_used_stats_info;
import com.example.saturn.sctaps.App_select_show_data;
import com.example.saturn.slider.WelcomeActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Appsdashboard extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1 ;

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> stringArrayListnames;

    ImageView appsinstalledapps,appsrecentlyusedapps,appssetlimits;

    UsageStatsManager usageStatsManager;
    List<UsageStats> queryUsageStats;
    List<Appstatsinfo> appsstats = new ArrayList<>();

    Context ctx;
    PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_appsdashboard);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());

            if (mode != AppOpsManager.MODE_ALLOWED) {
                Toast.makeText(Appsdashboard.this, "Please give Permission for App SATURN", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }

        }

        barChart=(BarChart)findViewById(R.id.barchart);

        appsinstalledapps=(ImageView)findViewById(R.id.apsinsapps);
        appsrecentlyusedapps=(ImageView)findViewById(R.id.apsrectusdapps);
        appssetlimits=(ImageView)findViewById(R.id.apssetlim);

        barEntryArrayList=new ArrayList<BarEntry>();
        stringArrayListnames=new ArrayList<String>();

        ctx=Appsdashboard.this;
        packageManager=ctx.getPackageManager();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY,-12);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queryUsageStats= usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(), System.currentTimeMillis());
            for(UsageStats pkgsts : queryUsageStats)
            {
                Appstatsinfo app = new Appstatsinfo();
                app.appname=pkgsts.getPackageName();
                app.usedtimetoday= pkgsts.getTotalTimeInForeground() / 1000;
                app.lastuseddate= (String) DateUtils.formatSameDayTime(pkgsts.getLastTimeUsed(),
                        System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
                if(app.usedtimetoday!=0 && app.appname!=null && app.lastuseddate!=null){
                    appsstats.add(app);
                }
            }
            for(int i=0;i<appsstats.size();i++)
            {
                ApplicationInfo info = null;
                try {
                    info = packageManager.getApplicationInfo(appsstats.get(i).appname, PackageManager.GET_META_DATA);
                    String appName = (String) packageManager.getApplicationLabel(info);
                    barEntryArrayList.add(new BarEntry(i,(appsstats.get(i).usedtimetoday)));
                    stringArrayListnames.add(appName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            BarDataSet barDataSet=new BarDataSet(barEntryArrayList,"last 12hrs usage");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            Description description=new Description();
            description.setText("Apps");
            barChart.setDescription(description);
            BarData barData=new BarData(barDataSet);
            barChart.setData(barData);
            XAxis xAxis=barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(stringArrayListnames));
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(stringArrayListnames.size());
            xAxis.setLabelRotationAngle(270);
            barChart.animateY(2000);
            barChart.invalidate();
        }

        appsinstalledapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Appsdashboard.this, LoadAppsiconsnames.class));
            }
        });
        appsrecentlyusedapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Appsdashboard.this, app_used_stats_info.class));
            }
        });
        appssetlimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Appsdashboard.this, App_select_show_data.class));
            }
        });


    }

    public void appsdashboardback(View view) {
        finish();
        startActivity(new Intent(Appsdashboard.this,MainPage.class));
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainPage.class));
    }
}
