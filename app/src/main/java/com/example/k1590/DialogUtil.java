package com.example.k1590;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogUtil {
    public static void showDialog(Context context, String pram1, String pram2){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);
        TextView title = view.findViewById(R.id.tv_title);
        TextView value = view.findViewById(R.id.tv_value);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        title.setText(pram1);
        value.setText(pram2);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void showPowerDialog(Context context, String pram1){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_3, null);
        TextView title = view.findViewById(R.id.tv_power_title);
        title.setText(pram1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showLightDialog(Context context, String pram1){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_4, null);
        TextView title = view.findViewById(R.id.tv_light_title);
        title.setText(pram1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
