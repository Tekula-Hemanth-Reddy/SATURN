package com.example.saturn.sercofapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.saturn.R;
import com.example.saturn.aditya.Task;
import com.example.saturn.aditya.taskviewer;
import com.example.saturn.appstatisticdetails.Appstatsinfo;
import com.example.saturn.diaryaditya.DiaryAdder;
import com.example.saturn.sctaps.App_select_show_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Myallsaturnservices extends BroadcastReceiver {

    private final String CHANNEL_ID="Personal_notifications";
    private  final int NOTIFICATION_ID=003;

    Context mycontext;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    ArrayList<Task> taskslist=new ArrayList<>();
    ArrayList<MyTasksService.Pair> pairedlist=new ArrayList<>();

    UsageStatsManager usageStatsManager;
    List<UsageStats> queryUsageStats;
    PackageManager packageManager;
    List<Appstatsinfo> appsstats = new ArrayList<>();
    List<Appstatsinfo> appsstatsselected = new ArrayList<>();
    ArrayList<String> appsselected=new ArrayList<>();

    String apn="";

    String str="";

    Boolean appsboin=false,tskboin=false,dirboin=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        mycontext=context;

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        packageManager=context.getPackageManager();

        Calendar lac=Calendar.getInstance();

        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Notifications");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String,String>> gti=new GenericTypeIndicator<Map<String, String>>() {
                };

                Map<String,String> mpnft=snapshot.getValue(gti);
                for(Map.Entry<String,String> ent:mpnft.entrySet())
                {
                    if(ent.getKey().equals("Apps"))
                    {
                        if(ent.getValue().equals("Yes"))
                        {
                            appsboin=true;
                        }
                        else
                        {
                            appsboin=false;
                        }
                    }
                    if(ent.getKey().equals("Tasks"))
                    {
                        if(ent.getValue().equals("Yes"))
                        {
                            tskboin=true;
                        }
                        else
                        {
                            tskboin=false;
                        }
                    }
                    if(ent.getKey().equals("Diary"))
                    {
                        if(ent.getValue().equals("Yes"))
                        {
                            dirboin=true;

                        }
                        else
                        {
                            dirboin=false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        taskslist.clear();


        if(tskboin && (lac.get(Calendar.HOUR_OF_DAY)==12) && (lac.get(Calendar.MINUTE)==20) && (lac.get(Calendar.SECOND)==0))
        {
            databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Tasks");
            databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    pairedlist.clear();
                    str="";
                    GenericTypeIndicator<Map<String,Task>> gtit=new GenericTypeIndicator<Map<String, Task>>() {
                    };
                    Map<String,Task> tsk=snapshot.getValue(gtit);
                    try {
                        for (Map.Entry<String, Task> entry : tsk.entrySet()) {

                            Calendar c=Calendar.getInstance();

                            c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(entry.getValue().hour));
                            c.set(Calendar.YEAR,Integer.parseInt(entry.getValue().year));
                            c.set(Calendar.MONTH,Integer.parseInt(entry.getValue().month));
                            c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(entry.getValue().day));
                            c.set(Calendar.MINUTE,Integer.parseInt(entry.getValue().minute));

                            long timeinmillis=c.getTimeInMillis();


                            pairedlist.add(new MyTasksService.Pair(entry.getValue(),timeinmillis));

                        }
                        str=pairedlist.get(0).t.taskname;

                        displayNotificationfortasks();
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
        else if(dirboin && (lac.get(Calendar.HOUR_OF_DAY)==12) && (lac.get(Calendar.MINUTE)==35) && (lac.get(Calendar.SECOND)==0))
        {

            databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Diary").child(todaysdatefun());
            databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists())
                    {
                        displayNotificationfordiary();
                    }
                    else
                    {
                        apn="Phone";
                        displayNotificationforapps();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else if(appsboin)
        {
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






    }

    private void displayNotificationforapps() {

        createNotificationchannelforsaturn();

        Intent landingintent =new Intent(mycontext, App_select_show_data.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,0,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mycontext,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logobackgrdnill);
        builder.setContentTitle("Apps");
        builder.setContentText("You have used "+apn+" for 90 mins so better to NOT to use it");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingpendingintent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(mycontext);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }


    private void displayNotificationfortasks() {

        createNotificationchannelforsaturn();

        Intent landingintent =new Intent(mycontext, taskviewer.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,0,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mycontext,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logobackgrdnill);
        builder.setContentTitle("Tasks");
        builder.setContentText("It's importantant to complete "+str+" task first");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingpendingintent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(mycontext);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }

    private void displayNotificationfordiary() {

        createNotificationchannelforsaturn();

        Intent landingintent =new Intent(mycontext, DiaryAdder.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,0,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mycontext,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logobackgrdnill);
        builder.setContentTitle("Dear Diary");
        builder.setContentText("We are missing you, Put your thoughts in the Diary");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingpendingintent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(mycontext);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }


    private void createNotificationchannelforsaturn()
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

    private String todaysdatefun()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        Calendar td=Calendar.getInstance();
        String currentDateandTime = sdf.format(new Date());
        String year1=currentDateandTime.substring(0,4);
        String month1=currentDateandTime.substring(5,7);
        String day1=currentDateandTime.substring(8,10);
        return(year1+" "+month1+" "+day1);

    }

    public class Pair implements Comparable<Myallsaturnservices.Pair>
    {
        Task t;
        long timeinmilli;

        public Pair(Task t, long timeinmilli) {
            this.t = t;
            this.timeinmilli = timeinmilli;
        }

        @Override
        public int compareTo(Myallsaturnservices.Pair o)
        {
            if(timeinmilli - o.timeinmilli < 0)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }

}
