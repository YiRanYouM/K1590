package com.example.k1590;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sp;
    private Button bt_login;
    private TextView tv_register;
    private DBHelper helper = null;
    private EditText edit_name, edit_pw;
    private final String AccessKeyID = "LTAI4G6wThjjzvRFRQ76NmNY";
    private final String AccessKeySecret = "7O4nsPVgdoXM56GJxUDJubmUNxU3re";
    private final String Region = "cn-shanghai";
    private final String ProductKey = "a11PkRH5FFV";
    private final String ProductSecret = "I1m2H8wvIdFOC1uL";
    private final String Domain = "iot.cn-shanghai.aliyuncs.com";
    //LTAI4G6wThjjzvRFRQ76NmNY
    //7O4nsPVgdoXM56GJxUDJubmUNxU3re
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        helper = new DBHelper(this, "eventDataBase", null, 1);
        initView();
        autoLogin();
    }

    private void autoLogin() {
        boolean status = sp.getBoolean("isLogin", false);
        if (status) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initView() {
        sp = SharePreferenceUtil.getSp(this);
        bt_login = findViewById(R.id.bt_login);
        tv_register = findViewById(R.id.tv_register);
        edit_name = findViewById(R.id.edit_name);
        edit_pw = findViewById(R.id.edit_pw);

        bt_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_login:
                login();
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    private void login() {
        String name = edit_name.getText().toString();
        String pw = edit_pw.getText().toString();
        if (name.isEmpty()){
            edit_name.setError("账号不能为空");
        }else if (pw.isEmpty()){
            edit_pw.setError("密码不能为空");
        }else {
           if (helper.is_login(name, pw)){
               sp.edit().putBoolean("isLogin",true).commit();
               sp.edit().putString("name",name).commit();
               Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
               //startActivity(new Intent(LoginActivity.this, MainActivity.class));
               RegisterDevice();
               //finish();
           }else {
               Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
           }
        }
    }

    public void RegisterDevice () {
        final DynamicRegisterByMqtt client = new DynamicRegisterByMqtt();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.register(ProductKey, ProductSecret, "test4");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
