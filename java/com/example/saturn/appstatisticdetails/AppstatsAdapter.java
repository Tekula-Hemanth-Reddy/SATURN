package com.example.saturn.appstatisticdetails;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saturn.R;

import java.util.List;

public class AppstatsAdapter extends ArrayAdapter<Appstatsinfo> {

    LayoutInflater layoutInflater;
    PackageManager packageManager;
    List<Appstatsinfo> myapps;
    Context ctx;
    public AppstatsAdapter(@NonNull Context context, List<Appstatsinfo> resource) {
        super(context, R.layout.app_usage_item_statistics, resource);
        ctx=context;
        layoutInflater=LayoutInflater.from(context);
        packageManager=context.getPackageManager();
        myapps=resource;
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        final Appstatsinfo current=myapps.get(position);
        View view=convertView;
        if(view==null)
        {
            view=layoutInflater.inflate(R.layout.app_usage_item_statistics,parent,false);

        }
        PackageManager packageManager = ctx.getPackageManager();
        String appName = "";
        ApplicationInfo info = null;
        try {
            info = packageManager.getApplicationInfo(current.appname, PackageManager.GET_META_DATA);
            appName = (String) packageManager.getApplicationLabel(info);
            ImageView imviconapp=(ImageView) view.findViewById(R.id.appiconlauncherstats);
            TextView pkgName = (TextView) view.findViewById(R.id.appnamelauncherstats);
            pkgName.setText(appName);
            TextView usageTime = (TextView) view.findViewById(R.id.howmuchtimeusedstats);
            usageTime.setText("USED TIME "+DateUtils.formatElapsedTime(current.usedtimetoday));
            TextView lastusageTime = (TextView) view.findViewById(R.id.lastusedstats);
            lastusageTime.setText("LAST USED "+current.lastuseddate);
            Drawable icon = null;
            try {
                icon = getContext().getPackageManager().getApplicationIcon(current.appname);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            imviconapp.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return view;
    }
}
