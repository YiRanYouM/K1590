package com.example.k1590;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.zip.Inflater;

public class DialogUtil {
    public static void showDialog(Context context, String pram1, String pram2){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);
        TextView title = view.findViewById(R.id.tv_title);
        TextView value = view.findViewById(R.id.tv_value);
        title.setText(pram1);
        value.setText(pram2);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
