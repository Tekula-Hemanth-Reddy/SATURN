package com.example.saturn.appdatainfo;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.saturn.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AppAdapter extends ArrayAdapter<AppInfo>
{
    LayoutInflater layoutInflater;
    PackageManager packageManager;
    List<AppInfo> myapps;
    Context ctx;

    public AppAdapter(Context context, List<AppInfo> appInfos)
    {
        super(context, R.layout.apps_include_items,appInfos);
        ctx=context;
        layoutInflater=LayoutInflater.from(context);
        packageManager=context.getPackageManager();
        myapps=appInfos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final AppInfo current=myapps.get(position);
        View view=convertView;
        if(view==null)
        {
            view=layoutInflater.inflate(R.layout.apps_include_items,parent,false);

        }
      TextView title = (TextView)view.findViewById(R.id.appnamelauncher);
        title.setText(current.lable);
      ImageView imv = (ImageView)view.findViewById(R.id.appiconlauncher);

        Drawable background = current.info.loadIcon(packageManager);
        imv.setImageDrawable(background);

        Button btn=view.findViewById(R.id.showappinfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               creatdialogtoshowinfo(current);
            }
        });
        return view;
    }

    private void creatdialogtoshowinfo(AppInfo current) {
        String versioninfo = null;
        String firstinstalltime=null;
        DateFormat formatter= new SimpleDateFormat("dd/mm/yyyy");
        Calendar cal=Calendar.getInstance();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(current.info.packageName,0);

            if(!TextUtils.isEmpty(packageInfo.versionName))
            {
                versioninfo= String.format("%s",packageInfo.versionName);
                firstinstalltime=String.format("%s", packageInfo.packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(ctx);
        builder.setTitle("App detatils");
        builder.setMessage("Version : "+versioninfo+"\nPackage Name : "+firstinstalltime);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
