package com.example.k1590;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_register;
    private TextView tv_login;
    private DBHelper helper = null;
    private EditText edit_name,  edit_pw, edit_pw_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        helper = new DBHelper(this, "eventDataBase", null, 1);
        initView();
    }

    private void initView() {
        bt_register = findViewById(R.id.bt_register);
        tv_login = findViewById(R.id.tv_login);
        edit_name = findViewById(R.id.edit_rg_name);
        edit_pw = findViewById(R.id.edit_rg_pw);
        edit_pw_again = findViewById(R.id.edit_pw_again);

        bt_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_register:
                register();
                break;
            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void register(){
        String name = edit_name.getText().toString();
        String pw = edit_pw.getText().toString();
        String pw_again = edit_pw_again.getText().toString();
        if (name.isEmpty()){
            edit_name.setError("账号不能为空");
        }else if (pw.isEmpty()){
            edit_pw.setError("密码不能为空");
        }else if (pw_again.isEmpty()){
            edit_pw_again.setError("请再次输入密码");
        }else if (!pw.equals(pw_again)){
            edit_pw_again.setError("两次输入密码不一致");
        }else {
            UserBean ub = new UserBean();
            ub.setName(name);
            ub.setPassword(pw);
            ub.setTou(null);
            helper.insert(ub);
            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }
}
