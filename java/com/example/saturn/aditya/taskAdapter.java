package com.example.saturn.aditya;

import android.content.Context;
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

public class taskAdapter extends ArrayAdapter<Task> {
    LayoutInflater layoutInflater;
    List<Task> mytaskstoday;
    public taskAdapter(@NonNull Context context,List<Task> resource) {
        super(context, R.layout.tasklist_items_include,resource);
        layoutInflater=LayoutInflater.from(context);
        mytaskstoday=resource;

    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       Task current = mytaskstoday.get(position);
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.tasklist_items_include, parent, false);

        }
        TextView name=(TextView)view.findViewById(R.id.tasknameinclude);
        TextView deadline=(TextView)view.findViewById(R.id.tasktimeinclude);
        TextView perc=(TextView)view.findViewById(R.id.percentaskinclude);
        name.setText(current.taskname);
        deadline.setText(current.hour+":"+current.minute);
        perc.setText(current.percentage_completion+" %");
        return view;
    }
}
