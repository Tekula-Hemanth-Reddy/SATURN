package com.example.saturn.sctaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.saturn.MainPage;
import com.example.saturn.R;
import com.example.saturn.appdatainfo.AppAdapter;
import com.example.saturn.appdatainfo.AppInfo;
import com.example.saturn.appdatainfo.LoadAppsiconsnames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Apps_select extends AppCompatActivity {

    EditText searchtextapp;
    ImageView imvsearch;
    ListView applistshows;

    Boolean setvalue=false;
    Boolean taketext=false;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

//    public static ArrayList<String> appsselected=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_apps_select2);
        searchtextapp=(EditText)findViewById(R.id.appsselectsearchappsid);
        imvsearch=(ImageView)findViewById(R.id.appsselectsearchimagebutton);
        applistshows=(ListView)findViewById(R.id.appsselectlistappsofphone);
        applistshows.setTextFilterEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

//        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Selected Apps");
//        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                GenericTypeIndicator<Map<String,String>> gtindicator=new GenericTypeIndicator<Map<String,String>>(){};
////                Map<String,String> names =snapshot.getValue(gtindicator);
//                try {
//                        for(DataSnapshot ds:snapshot.getChildren())
//                        {
//
//                            String str=ds.getValue(String.class);
//                            appsselected.add(str);
//                        }
//
//
//                }
//                catch (Exception e)
//                {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//

        imvsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchtextapp.getText().toString().isEmpty())
                {
                    taketext=true;

                }
                Apps_select.LoadAppInfotaskselectApp loadAppInfotaskselectApp=new Apps_select.LoadAppInfotaskselectApp();
                loadAppInfotaskselectApp.execute(PackageManager.GET_META_DATA);
            }
        });

        Apps_select.LoadAppInfotaskselectApp loadAppInfotaskselectApp=new Apps_select.LoadAppInfotaskselectApp();
        loadAppInfotaskselectApp.execute(PackageManager.GET_META_DATA);
    }

    public void sortbynames(View view) {
        setvalue=true;
        Apps_select.LoadAppInfotaskselectApp loadAppInfotaskselectApp=new Apps_select.LoadAppInfotaskselectApp();
        loadAppInfotaskselectApp.execute(PackageManager.GET_META_DATA);
    }

    public void saveappsnames(View view) {
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Selected Apps");
        databaseReference.removeValue();

        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Selected Apps");
        databaseReference.setValue(AppSelectAdapter.getselectedappsnames()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    finish();
                    startActivity(new Intent(Apps_select.this,App_select_show_data.class));

                }
            }
        });
    }

    public void appsselect(View view) {
        finish();
        startActivity(new Intent(Apps_select.this,App_select_show_data.class));
    }


    public class LoadAppInfotaskselectApp extends AsyncTask<Integer,Integer, List<AppInfo>> {


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
                Collections.sort(apps,new Apps_select.oncomparator());
                setvalue=false;
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            applistshows.setAdapter(new AppSelectAdapter(Apps_select.this,appInfos));


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Apps_select.LoadAppInfotaskselectApp loadAppInfotask=new Apps_select.LoadAppInfotaskselectApp();
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

//    public static ArrayList<String>allreadyselected()
//    {
//        return appsselected;
//    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainPage.class));
    }

}
