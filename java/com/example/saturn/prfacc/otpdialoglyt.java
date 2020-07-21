package com.example.saturn.prfacc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.saturn.R;
import com.google.android.material.textfield.TextInputEditText;

public class otpdialoglyt extends AppCompatDialogFragment {

    public TextInputEditText pot;
    private otpdialoglistener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();

        View view=inflater.inflate(R.layout.activity_otpdialoglyt,null);
        builder.setView(view)
                .setTitle("Verify OTP")
                .setNegativeButton("Return", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String senddata=pot.getText().toString().trim();
                        listener.applyText(senddata);

                    }
                });
        pot=view.findViewById(R.id.otpinput);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(otpdialoglistener)context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+"must Implement Dialog Listener");
        }

    }

    public interface otpdialoglistener{
        void applyText(String top);
    }


}