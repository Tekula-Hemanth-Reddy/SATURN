package com.example.saturn.sercofapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.saturn.R;
import com.example.saturn.aditya.Task;
import com.example.saturn.aditya.taskviewer;
import com.example.saturn.diaryaditya.DiaryAdder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MyTasksService extends BroadcastReceiver
{
//    private Boolean tasksmutex;
    private final String CHANNEL_ID = "Personal_notifications";
    private final int NOTIFICATION_ID = 002;
    Context mycontext;

    String str="";

    ArrayList<Task> taskslist=new ArrayList<>();
    ArrayList<Pair> pairedlist=new ArrayList<>();

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public void onReceive(Context context, Intent intent) {
        mycontext=context;
        taskslist.clear();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

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


                        if (timeinmillis-System.currentTimeMillis()>0) {

                            pairedlist.add(new Pair(entry.getValue(), timeinmillis));
                        }

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
    private void displayNotificationfortasks() {

        createNotificationchannelfortasks();

        Intent landingintent =new Intent(mycontext, taskviewer.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,002,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mycontext,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logobackgrdnill);
        builder.setContentTitle("Tasks");
        builder.setContentText("It's time to complete "+str+" task.");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingpendingintent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(mycontext);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }

    private void createNotificationchannelfortasks()
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

    public static class Pair implements Comparable<Pair>
    {
        Task t;
        long timeinmilli;

        public Pair(Task t, long timeinmilli) {
            this.t = t;
            this.timeinmilli = timeinmilli;
        }

        @Override
        public int compareTo(Pair o)
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
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        tasksmutex=true;
//        while(tasksmutex)
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
//        tasksmutex=false;
//    }
//
//
//}
