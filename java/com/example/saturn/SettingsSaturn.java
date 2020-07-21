package com.example.saturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.saturn.prfacc.EditDetails;
import com.example.saturn.sercofapp.MyAppsService;
import com.example.saturn.sercofapp.MyDiaryService;
import com.example.saturn.sercofapp.MyTasksService;
import com.example.saturn.sercofapp.Myallsaturnservices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Calendar;
import java.util.Map;

public class SettingsSaturn extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    Boolean appsboin,tskboin,dirboin;

    Switch task,diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings_saturn);

//        app=(Switch)findViewById(R.id.appsswitch);
        task=(Switch)findViewById(R.id.tasksswitch);
        diary=(Switch)findViewById(R.id.diaryswitch);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
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
//                            app.setChecked(true);
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
                            task.setChecked(true);
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
                            diary.setChecked(true);
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



//        app.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    appsboin=true;
//
//                    finish();
//                    startActivity(new Intent(SettingsSaturn.this, EditDetails.class));
//                    Calendar calendar=Calendar.getInstance();
//                    calendar.set(Calendar.HOUR_OF_DAY,0);
//                    calendar.set(Calendar.MINUTE,00);
//                    calendar.set(Calendar.SECOND,0);
//
//                    Intent intent=new Intent(SettingsSaturn.this,MyAppsService.class);
//                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),00,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);
//                    Toast.makeText(SettingsSaturn.this, "Apps Notifications set", Toast.LENGTH_SHORT).show();

//                    startService(new Intent(SettingsSaturn.this, MyAppsService.class));
//                }
//                else
//                {
//                    appsboin=false;
//                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//                    Intent intent=new Intent(SettingsSaturn.this,MyAppsService.class);
//                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),00,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    alarmManager.cancel(pendingIntent);
//                    Toast.makeText(SettingsSaturn.this, "Apps Notification cancled", Toast.LENGTH_SHORT).show();
//                    stopService(new Intent(SettingsSaturn.this, MyAppsService.class));
//                }
//            }
//        });

        task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tskboin=true;
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,9);
                    calendar.set(Calendar.MINUTE,20);
                    calendar.set(Calendar.SECOND,30);

                    Intent intent=new Intent(SettingsSaturn.this,MyTasksService.class);
                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),002,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
                    Toast.makeText(SettingsSaturn.this, "Notifications set", Toast.LENGTH_SHORT).show();
//                    startService(new Intent(SettingsSaturn.this, MyTasksService.class));
                }
                else
                {
                    tskboin=false;

                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                    Intent intent=new Intent(SettingsSaturn.this,MyTasksService.class);
                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),002,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(SettingsSaturn.this, "Notifications cancelled", Toast.LENGTH_SHORT).show();

//                    stopService(new Intent(SettingsSaturn.this, MyTasksService.class));
                }
            }
        });

        diary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    dirboin=true;

                    databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Deletedtasks");
                    try {
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful())
//                                    Toast.makeText(SettingsSaturn.this, "You Saved Your Memory!..!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }
//                    Calendar calendar=Calendar.getInstance();
//                    calendar.set(Calendar.HOUR_OF_DAY,10);
//                    calendar.set(Calendar.MINUTE,50);
//                    calendar.set(Calendar.SECOND,00);
//
//                    Intent intent=new Intent(SettingsSaturn.this,MyDiaryService.class);
//                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),00,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
//                    Toast.makeText(SettingsSaturn.this, "Diary Notifications set", Toast.LENGTH_SHORT).show();
//                    startService(new Intent(SettingsSaturn.this, MyDiaryService.class));
                }
                else
                {
                    dirboin=false;
//                    AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//                    Intent intent=new Intent(SettingsSaturn.this,MyDiaryService.class);
//                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),00,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    alarmManager.cancel(pendingIntent);
//                    Toast.makeText(SettingsSaturn.this, "Diary Notifications cancled", Toast.LENGTH_SHORT).show();
//                    stopService(new Intent(SettingsSaturn.this, MyDiaryService.class));
                }
            }
        });

    }

    public void settingssaturn(View view) {
        savefinallist();
        finish();
        startActivity(new Intent(SettingsSaturn.this,MainPage.class));
    }

    @Override
    public void onBackPressed() {
        savefinallist();
        finish();
        startActivity(new Intent(SettingsSaturn.this,MainPage.class));
    }

    private void savefinallist() {
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()).child("Notifications");
//        if(appsboin)
//        {
//            databaseReference.child("Apps").setValue("Yes");
//        }
//        else
//        {
//            databaseReference.child("Apps").setValue("No");
//        }

        if(tskboin)
        {
            databaseReference.child("Tasks").setValue("Yes");
        }
        else
        {
            databaseReference.child("Tasks").setValue("No");
        }

//        if(dirboin)
//        {
//            databaseReference.child("Diary").setValue("Yes");
//        }
//        else
//        {
//            databaseReference.child("Diary").setValue("No");
//        }

//        if(appsboin || tskboin || dirboin)
//        {
//            Calendar calendar=Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY,12);
//            calendar.set(Calendar.MINUTE,20);
////            calendar.set(Calendar.SECOND,0);
//
//            Intent intent=new Intent(SettingsSaturn.this, Myallsaturnservices.class);
//            PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent);
//            Toast.makeText(SettingsSaturn.this, "Notifications set", Toast.LENGTH_SHORT).show();
//
//        }
//        else
//        {
//            AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
//            Intent intent=new Intent(SettingsSaturn.this,Myallsaturnservices.class);
//            PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            alarmManager.cancel(pendingIntent);
//            Toast.makeText(SettingsSaturn.this, "Notifications cancel", Toast.LENGTH_SHORT).show();
//
//
//        }
    }
}