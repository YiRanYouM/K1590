package com.example.k1590;

import androidx.appcompat.app.AppCompatActivity;
import cn.mtjsoft.www.gridpager.GridPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import static com.example.k1590.MainActivity.publishMessage;

public class PowerActivity extends AppCompatActivity {
    private GridPager pager;
    private DBHelper helper;
    private List<ThingBean> powerList = new ArrayList<>();
    private String name;
    private SharedPreferences sp;
    private String speed_add, speed_reduce, ration_add, ration_reduce, ration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);

        name = getIntent().getStringExtra("name");

        helper = new DBHelper(this,"eventDataBase", null, 1);
        sp = SharePreferenceUtil.getSp(this);

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


    @Override
    protected void onStart() {
        super.onStart();

        speed_add = sp.getString("speed_add", "/d/");
        speed_reduce = sp.getString("speed_reduce", "/c/");
        ration_add = sp.getString("ration_add", "/e/");
        ration_reduce = sp.getString("ration_reduce", "/f/");
        ration = sp.getString("ration", "/b/");
    }

    public void showPowerDialog(Context context, String pram1){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_3, null);
        TextView title = view.findViewById(R.id.tv_power_title);
        Button bt_ok = view.findViewById(R.id.bt_power_ok);
        ImageView iv_speed_add = view.findViewById(R.id.iv_speed_add);
        ImageView iv_speed_reduce = view.findViewById(R.id.iv_speed_reduce);
        ImageView iv_ration_add = view.findViewById(R.id.iv_ration_add);
        ImageView iv_ration_reduce = view.findViewById(R.id.iv_ration_reduce);
        Switch sw_ration = view.findViewById(R.id.sw_ration);
        title.setText(pram1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        iv_speed_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order = String.format("{'speed':'%s'}", speed_add);
                publishMessage(order);
            }
        });

        iv_speed_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order = String.format("{'speed':'%s'}", speed_reduce);
                publishMessage(order);
            }
        });

        iv_ration_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order = String.format("{'ration':'%s'}", ration_add);
                publishMessage(order);
            }
        });

        iv_ration_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order = String.format("{'ration':'%s'}", ration_reduce);
                publishMessage(order);
            }
        });


        sw_ration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String order = String.format("{'ration':'%s'}", ration);
                publishMessage(order);
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
