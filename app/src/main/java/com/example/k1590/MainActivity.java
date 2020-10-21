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
    private TextView tv_ji, tv_deng;
    private SharedPreferences sp;
    private PopupWindow pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        sp = SharePreferenceUtil.getSp(this);
        bt_light = findViewById(R.id.bt_light);
        bt_temp = findViewById(R.id.bt_temp);
        bt_setting = findViewById(R.id.bt_setting);
        tv_deng = findViewById(R.id.tv_deng);
        tv_ji = findViewById(R.id.tv_ji);

        bt_light.setOnClickListener(this);
        bt_temp.setOnClickListener(this);
        tv_ji.setOnClickListener(this);
        tv_deng.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

    }


    private void menuPop(){
        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        TextView add = view.findViewById(R.id.add);
        TextView dian = view.findViewById(R.id.dian);
        TextView light = view.findViewById(R.id.light);
        TextView logout = view.findViewById(R.id.logout);
        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
                startActivity(new Intent(this, PowerActivity.class));
                pw.dismiss();
                break;
            case R.id.light:
                startActivity(new Intent(this, LightActivity.class));
                pw.dismiss();
                break;
            case R.id.tv_deng:
                startActivity(new Intent(this, LightActivity.class));
                break;
            case R.id.tv_ji:
                startActivity(new Intent(this, PowerActivity.class));
                break;
            case R.id.bt_light:

                break;
            case R.id.bt_temp:

                break;
            case R.id.bt_setting:
                menuPop();
                break;
            default:
                break;
        }
    }
}
