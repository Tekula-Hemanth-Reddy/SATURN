package com.example.saturn.sctaps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.saturn.R;
import com.example.saturn.appdatainfo.AppInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppSelectAdapter extends ArrayAdapter<AppInfo>
{
    LayoutInflater layoutInflater;
    PackageManager packageManager;
    List<AppInfo> myapps;
    Context ctx;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static ArrayList appnames=new ArrayList<String>();

    public static ArrayList<String> appsselected=new ArrayList<String>();


    public AppSelectAdapter(Context context, List<AppInfo> appInfos)
    {
        super(context, R.layout.app_select_time,appInfos);
        ctx=context;
        layoutInflater=LayoutInflater.from(context);
        packageManager=context.getPackageManager();
        myapps=appInfos;

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();


        appnames=App_select_show_data.sendselectedappstoshowdata();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final AppInfo current=myapps.get(position);
        View view=convertView;
        if(view==null)
        {
            view=layoutInflater.inflate(R.layout.app_select_time,parent,false);

        }
      TextView title = view.findViewById(R.id.appselectnamelauncher);
        title.setText(current.lable);
      ImageView imv = view.findViewById(R.id.appselecticonlauncher);

        Drawable background = current.info.loadIcon(packageManager);
        imv.setImageDrawable(background);

        Switch swt = view.findViewById(R.id.appselectbutton);
        if(appnames.contains(current.lable))
        {
            swt.setChecked(true);
//            if(!appnames.contains(current.lable))
//            {
//                appnames.add(current.lable);
//            }
        }

        swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(!appnames.contains(current.lable))
                    {
                        appnames.add(current.lable);
                    }

                }
                else
                {
                    if(appnames.contains(current.lable))
                    appnames.remove(current.lable);
                }
            }
        });
        return view;
    }


    public static ArrayList<String>getselectedappsnames()
    {
        return appnames;
    }
}
