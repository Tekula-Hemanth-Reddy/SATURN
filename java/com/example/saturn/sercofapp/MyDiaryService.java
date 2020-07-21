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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.saturn.R;
import com.example.saturn.diaryaditya.Diary;
import com.example.saturn.diaryaditya.DiaryAdder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MyDiaryService extends BroadcastReceiver
{
    private final String CHANNEL_ID="Personal_notifications";
    private  final int NOTIFICATION_ID=003;

    Context mycontext;


    @Override
    public void onReceive(Context context, Intent intent) {

        mycontext=context;

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Diary").child(todaysdatefun());
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    System.out.println("Entered");
                    displayNotificationfordiary();
                }
                else
                {
                    //do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent repeating=new Intent(context,DiaryAdder.class);
//        repeating.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Toast.makeText(context, "Msg set", Toast.LENGTH_SHORT).show();
//
//        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,100,repeating,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder= new NotificationCompat.Builder(context)
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.drawable.logobackgrdnill)
//                .setContentTitle("Dear Diary")
//                .setContentText("We are missing you, Put your thoughts in the Diary")
//                .setAutoCancel(true);
//
//        notificationManager.notify(100,builder.build());

    }

    private void displayNotificationfordiary() {

        createNotificationchannelfordiary();

        Intent landingintent =new Intent(mycontext, DiaryAdder.class);
        landingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent landingpendingintent=PendingIntent.getActivity(mycontext,003,landingintent,PendingIntent.FLAG_UPDATE_CURRENT);

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

    private void createNotificationchannelfordiary()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name="Personal Notifications";
            String description="include all the personal notifications";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;

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
}
//
//
// extends Service
//{
//
//    private Boolean diarymutex;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        diarymutex=true;
//        while(diarymutex)
//        {
//            Calendar calendar=Calendar.getInstance();
//            if(calendar.get(Calendar.HOUR_OF_DAY)==16 && calendar.get(Calendar.MINUTE)==54 && calendar.get(Calendar.SECOND)==0)
//            {

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
//        diarymutex=false;
//    }

//
//}
