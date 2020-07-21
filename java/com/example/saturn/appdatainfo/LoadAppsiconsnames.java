package com.example.saturn.appdatainfo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saturn.Appsdashboard;
import com.example.saturn.MainPage;
import com.example.saturn.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoadAppsiconsnames extends AppCompatActivity {


    EditText searchtextapp;
    ImageView imvsearch;
    ListView applistshows;

    Boolean setvalue=false;
    Boolean taketext=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_appsiconsnames);

        searchtextapp=(EditText)findViewById(R.id.searchappsid);
        imvsearch=(ImageView)findViewById(R.id.searchimagebutton);
        applistshows=(ListView)findViewById(R.id.listappsofphone);
        applistshows.setTextFilterEnabled(true);

        imvsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchtextapp.getText().toString().isEmpty())
                {
                    taketext=true;

                }
                LoadAppInfotask loadAppInfotask=new LoadAppInfotask();
                loadAppInfotask.execute(PackageManager.GET_META_DATA);
            }
        });

        LoadAppInfotask loadAppInfotask=new LoadAppInfotask();
        loadAppInfotask.execute(PackageManager.GET_META_DATA);
    }

    public void sortbynames(View view) {
        setvalue=true;
        LoadAppInfotask loadAppInfotask=new LoadAppInfotask();
        loadAppInfotask.execute(PackageManager.GET_META_DATA);
    }

    public void loadappsiconnames(View view) {
        finish();
        startActivity(new Intent(LoadAppsiconsnames.this, Appsdashboard.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }


    public class LoadAppInfotask extends AsyncTask<Integer,Integer, List<AppInfo>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<AppInfo> doInBackground(Integer... integers) {

            List<AppInfo> apps = new ArrayList<>();
            PackageManager packageManager = getPackageManager();

            List<ApplicationInfo> infos = packageManager.getInstalledApplications(integers[0]);

            for(ApplicationInfo info:infos)
            {
                if(((info.flags & ApplicationInfo.FLAG_SYSTEM)==1))
                {
                    continue;
                }

                AppInfo app = new AppInfo();
                app.info=info;
                app.lable=(String)info.loadLabel(packageManager);
                if(taketext)
                {
                    String str=searchtextapp.getText().toString().trim();
                    String rts= String.valueOf(str.charAt(0)).toUpperCase();
                    if(app.lable.startsWith(str.replace(str.charAt(0),rts.charAt(0))))
                    {
                        apps.add(app);
                    }
                    else
                    {
                        continue;
                    }
                }
                else{
                apps.add(app);}
            }
            taketext=false;
            if(setvalue)
            {
                Collections.sort(apps,new oncomparator());
                setvalue=false;
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            applistshows.setAdapter(new AppAdapter(LoadAppsiconsnames.this,appInfos));


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadAppInfotask loadAppInfotask=new LoadAppInfotask();
        loadAppInfotask.execute(PackageManager.GET_META_DATA);
    }

    private  class oncomparator implements Comparator<AppInfo>
    {

        @Override
        public int compare(AppInfo o1, AppInfo o2) {
            CharSequence sa=o1.lable;
            CharSequence sb=o2.lable;

            if(sa==null)
            {
                sa=o1.info.packageName;
            }
            if(sb==null)
            {
                sb=o2.info.packageName;
            }

            return Collator.getInstance().compare(sa.toString(),sb.toString());
        }
    }


}
