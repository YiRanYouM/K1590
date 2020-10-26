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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LightActivity extends AppCompatActivity {
    private GridPager pager;
    private DBHelper helper;
    private List<ThingBean> lightList = new ArrayList<>();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        name = getIntent().getStringExtra("name");

        helper = new DBHelper(this,"eventDataBase", null, 1);

        lightList = helper.getThingByType(name,"0");

        pager = findViewById(R.id.grid_light);

        pager.setDataAllCount(lightList.size())
                .setItemBindDataListener(new GridPager.ItemBindDataListener() {
                    @Override
                    public void BindData(ImageView imageView, TextView textView, int position) {
                        imageView.setImageResource(R.drawable.ic_lightbulb);
                        textView.setText(lightList.get(position).getName());
                    }
                })
                .setGridItemClickListener(new GridPager.GridItemClickListener() {
                    @Override
                    public void click(int position) {
                        showLightDialog(LightActivity.this, lightList.get(position).getName());
                    }
                })
                .show();
    }

    public static void showLightDialog(Context context, String pram1){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_4, null);
        TextView title = view.findViewById(R.id.tv_light_title);
        ImageView add = view.findViewById(R.id.iv_light_add);
        ImageView reduce = view.findViewById(R.id.iv_light_reduce);
        Button bt_ok = view.findViewById(R.id.bt_light_ok);
        Button bt_cancel = view.findViewById(R.id.bt_light_cancel);

        title.setText(pram1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

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
