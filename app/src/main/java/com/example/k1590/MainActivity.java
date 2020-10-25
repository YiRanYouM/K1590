package com.example.k1590;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cn.mtjsoft.www.gridpager.GridPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_light, bt_temp;
    private TextView tv_ji, tv_deng;
    private GridPager pager;
    private SharedPreferences sp;
    private PopupWindow pw;
    private DBHelper helper;
    private String user_name;
    private ImageView iv_setting;
    private List<ThingBean> thingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        helper = new DBHelper(this,"eventDataBase", null, 1);

        sp = SharePreferenceUtil.getSp(this);
        bt_light = findViewById(R.id.bt_light);
        bt_temp = findViewById(R.id.bt_temp);
        iv_setting = findViewById(R.id.iv_setting);
        tv_deng = findViewById(R.id.tv_deng);
        tv_ji = findViewById(R.id.tv_ji);
        pager = findViewById(R.id.grid_total);

        bt_light.setOnClickListener(this);
        bt_temp.setOnClickListener(this);
        tv_ji.setOnClickListener(this);
        tv_deng.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        user_name = sp.getString("name", null);

    }

    @Override
    protected void onStart() {
        super.onStart();

        thingList = helper.QueryAllThing(user_name);
        pager.setDataAllCount(thingList.size())
                .setItemBindDataListener(new GridPager.ItemBindDataListener() {
                    @Override
                    public void BindData(ImageView imageView, TextView textView, int position) {
                        if (thingList.get(position).getType().equals("0")) {
                            imageView.setImageResource(R.drawable.ic_lightbulb);
                        }else if (thingList.get(position).getType().equals("1")){
                            imageView.setImageResource(R.drawable.ic_power);
                        }

                        textView.setText(thingList.get(position).getName());
                    }
                })
                .setGridItemClickListener(new GridPager.GridItemClickListener() {
                    @Override
                    public void click(int position) {

                    }
                })
                .show();
    }

    private void menuPop(){
        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        TextView add = view.findViewById(R.id.add);
        TextView dian = view.findViewById(R.id.dian);
        TextView light = view.findViewById(R.id.light);
        TextView logout = view.findViewById(R.id.logout);
        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setContentView(view);
        pw.setBackgroundDrawable(new ColorDrawable());
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
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
                Intent intent4 = new Intent(this, AddActivity.class);
                intent4.putExtra("name", user_name);
                startActivity(intent4);
                pw.dismiss();
                break;
            case R.id.dian:
                Intent intent = new Intent(this, PowerActivity.class);
                intent.putExtra("name", user_name);
                startActivity(intent);
                pw.dismiss();
                break;
            case R.id.light:
                Intent intent1 = new Intent(this, LightActivity.class);
                intent1.putExtra("name", user_name);
                startActivity(intent1);
                pw.dismiss();
                break;
            case R.id.tv_deng:
                Intent intent3 = new Intent(this, LightActivity.class);
                intent3.putExtra("name", user_name);
                startActivity(intent3);
                break;
            case R.id.tv_ji:
                Intent intent2 = new Intent(this, PowerActivity.class);
                intent2.putExtra("name", user_name);
                startActivity(intent2);
                break;
            case R.id.bt_light:
                DialogUtil.showDialog(this, "当前光照","");
                break;
            case R.id.bt_temp:
                DialogUtil.showDialog(this, "当前温度","");
                break;
            case R.id.iv_setting:
                menuPop();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pw != null && pw.isShowing()){
            pw.dismiss();
        }
    }
}
