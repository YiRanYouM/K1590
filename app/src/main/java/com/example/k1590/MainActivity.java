package com.example.k1590;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_light, bt_temp, bt_setting;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        sp = SharePreferenceUtil.getSp(this);
        bt_light = findViewById(R.id.bt_light);
        bt_temp = findViewById(R.id.bt_temp);
        bt_setting = findViewById(R.id.bt_setting);


        bt_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        bt_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuPop();
            }
        });
    }


    private void menuPop(){
        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        TextView add = view.findViewById(R.id.add);
        TextView dian = view.findViewById(R.id.dian);
        TextView light = view.findViewById(R.id.light);
        TextView logout = view.findViewById(R.id.logout);
        PopupWindow pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setContentView(view);
        add.setOnClickListener(this);
        dian.setOnClickListener(this);
        light.setOnClickListener(this);
        logout.setOnClickListener(this);
        View rootView = getLayoutInflater().inflate(R.layout.activity_main, null);

        pw.showAtLocation(rootView, Gravity.BOTTOM,0,0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout:
                sp.edit().putBoolean("isLogin", false).commit();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.add:
                break;
            case R.id.dian:
                break;
            case R.id.light:
                break;
            default:
                break;
        }
    }
}
