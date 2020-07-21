package com.example.saturn.sercofapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.saturn.R;
import com.example.saturn.appstatisticdetails.AppstatsAdapter;
import com.example.saturn.appstatisticdetails.Appstatsinfo;
import com.example.saturn.diaryaditya.DiaryAdder;
import com.example.saturn.sctaps.App_select_show_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyAppsService extends BroadcastReceiver {

    UsageStatsManager usageStatsManager;
    List<UsageStats> queryUsageStats;
    PackageManager packageManager;
    List<Appstatsinfo> appsstats = new ArrayList<>();
    List<Appstatsinfo> appsstatsselected = new ArrayList<>();
    ArrayList<String> appsselected=new ArrayList<>();

    //    private Boolean appsmutex;
    private final String CHANNEL_ID = "Personal_notifications";
    private final int NOTIFICATION_ID = 001;
    Context mycontext;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String apn="";

    @Override
    public void onReceive(Context context, Intent intent) {
        mycontext=context;

        packageManager=context.getPackageManager();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(), System.currentTimeMillis());
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
            appsselected.clear();
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
                            appsselected.add(str);
                        }
                        for(Appstatsinfo app:appsstats)
                        {
                            ApplicationInfo info = null;
                            try {
                                info = packageManager.getApplicationInfo(app.appname, PackageManager.GET_META_DATA);
                                String appName = (String) packageManager.getApplicationLabel(info);
                                apn="";
                                if(appsselected.contains(appName))
                                {
                                    appsstatsselected.add(app);
                                    if(app.usedtimetoday>540000)
                                    {
                                        apn=appName;
                                        displayNotificationforapps();

                                    }

                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }

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



    }

    private void displayNotificationforapps() {

        createNotificationchannelforapps();

        Intent landingintent =new Intent(mycontext, App_select_show_data.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,00,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mycontext,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logobackgrdnill);
        builder.setContentTitle("Apps");
        builder.setContentText("You have used this"+apn+"for 90 mins so better to stop it");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingpendingintent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(mycontext);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }

    private void createNotificationchannelforapps()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name="Personal Notifications";
            String description="include all the personal notifications";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager=(NotificationManager) mycontext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
}
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        appsmutex=true;
//        while(appsmutex)
//        {
//            Calendar calendar=Calendar.getInstance();
//            if(calendar.get(Calendar.HOUR_OF_DAY)==20 && calendar.get(Calendar.MINUTE)==0)
//            {
//                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
//                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
//                DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Diary").child("todaysdatefun()");
//                databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(!snapshot.exists())
//                        {
////                            displayNotificationfordiary();
//                        }
//                        else
//                        {
//                            //do nothing
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//        }
//        return START_STICKY;
//    }
//
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        appsmutex=false;
//    }
//}
