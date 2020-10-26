package com.example.k1590;

import androidx.appcompat.app.AppCompatActivity;
import cn.mtjsoft.www.gridpager.GridPager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PowerActivity extends AppCompatActivity {
    private GridPager pager;
    private DBHelper helper;
    private List<ThingBean> powerList = new ArrayList<>();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);

        name = getIntent().getStringExtra("name");

        helper = new DBHelper(this,"eventDataBase", null, 1);

        powerList = helper.getThingByType(name,"1");
        pager = findViewById(R.id.grid_power);

        pager.setDataAllCount(powerList.size())
                .setItemBindDataListener(new GridPager.ItemBindDataListener() {
                    @Override
                    public void BindData(ImageView imageView, TextView textView, int position) {
                        imageView.setImageResource(R.drawable.ic_power);
                        textView.setText(powerList.get(position).getName());
                    }
                })
                .setGridItemClickListener(new GridPager.GridItemClickListener() {
                    @Override
                    public void click(int position) {
                        showPowerDialog(PowerActivity.this, powerList.get(position).getName());
                    }
                })
                .show();
    }


    public static void showPowerDialog(Context context, String pram1){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_3, null);
        TextView title = view.findViewById(R.id.tv_power_title);
        Button bt_ok = view.findViewById(R.id.bt_power_ok);
        Button bt_cancel = view.findViewById(R.id.bt_power_cancel);
        ImageView iv_speed_add = view.findViewById(R.id.iv_speed_add);
        ImageView iv_speed_reduce = view.findViewById(R.id.iv_speed_reduce);
        ImageView iv_ration_add = view.findViewById(R.id.iv_ration_add);
        ImageView iv_ration_reduce = view.findViewById(R.id.iv_ration_reduce);
        title.setText(pram1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        iv_speed_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        iv_speed_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        iv_ration_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        iv_ration_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
