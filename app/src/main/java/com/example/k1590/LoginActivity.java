package com.example.k1590;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttConfigure;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttInitParams;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sp;
    private Button bt_login;
    private TextView tv_register;
    private DBHelper helper = null;
    private EditText edit_name, edit_pw;
    public static final String ProductKey = "a11PkRH5FFV";
    public static final String ProductSecret = "I1m2H8wvIdFOC1uL";
    private String deviceToken = null;
    public static String deviceSecret = null;
    public static String deviceName = "Test5";
    private String clientId = null;

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

        deviceToken = sp.getString("deviceToken", null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
        if (name.isEmpty()) {
            edit_name.setError("账号不能为空");
        } else if (pw.isEmpty()) {
            edit_pw.setError("密码不能为空");
        } else {
            if (helper.is_login(name, pw)) {
                sp.edit().putBoolean("isLogin", true).commit();
                sp.edit().putString("name", name).commit();
                registerDevice();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 动态注册
     * 只有pk dn ps 的时候 需要先动态注册获取ds，然后使用pk+dn+ds进行初始化建联，如果一开始有ds则无需执行动态注册
     * 动态注册之后需要将 ds保存起来，下次应用重新启动的时候，直接拿上次的ds进行初始化建联。
     * 如果需要应用卸载之后仍然可以使用ds建联，需要第一次动态初始化将ds保存到非应用目录，确保卸载应用之后ds仍然存在。
     * 如果动态注册之后，应用卸载了，没有保存ds的话，重新安装执行动态注册是会失败的。
     * 注意：动态注册成功，设备上线之后，不能再次执行动态注册，云端会返回已主动注册。
     */
    private void registerDevice() {
        if (!TextUtils.isEmpty(ProductKey) && !TextUtils.isEmpty(ProductSecret) && TextUtils.isEmpty(deviceToken)) {
            // docs: https://help.aliyun.com/document_detail/132111.html?spm=a2c4g.11186623.6.600.4e073f827Y7a8y
            MqttInitParams initParams = new MqttInitParams(ProductKey, ProductSecret, deviceName, deviceSecret, MqttConfigure.MQTT_SECURE_MODE_TLS);

            initParams.registerType = "regnwl"; // 一型一密免白

            LinkKit.getInstance().deviceDynamicRegister(this, initParams, new IOnCallListener() {
                @Override
                public void onSuccess(com.aliyun.alink.linksdk.channel.core.base.ARequest request, com.aliyun.alink.linksdk.channel.core.base.AResponse response) {
                    Log.i("Login", "onSuccess() called with: request = [" + request + "], response = [" + response + "]");
                        // response.data is byte array
                    try {
                        String responseData = new String((byte[]) response.data);
                        JSONObject jsonObject = JSONObject.parseObject(responseData);
                        String pk = jsonObject.getString("productKey");
                        String dn = jsonObject.getString("deviceName");
                        // 一型一密免白返回
                        String ci = jsonObject.getString("clientId");
                        String dt = jsonObject.getString("deviceToken");

                        clientId = ci;
                        deviceToken = dt;

                        // 持久化 clientId & deviceToken 初始化建联的时候需要
                        // 这里仅为测试代码，请将认证信息持久化到外部存储，确保app清除缓存或者卸载重装后仍能取到
                        if ((!TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(deviceToken))) {
                            sp.edit().putString("clientId", clientId).commit();
                            sp.edit().putString("deviceToken", deviceToken).commit();
                            destroyRegisterConnect();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            destroyRegisterConnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        destroyRegisterConnect();
                    }

                }

                @Override
                public void onFailed(com.aliyun.alink.linksdk.channel.core.base.ARequest request, com.aliyun.alink.linksdk.channel.core.base.AError error) {
                    Log.w("TAG", "onFailed() called with: request = [" + request + "], error = [" + error + "]");
                    destroyRegisterConnect();
                }

                @Override
                public boolean needUISafety() {
                    return false;
                }
            });
        }else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    /**
     * 注意该接口不能在 动态注册回调线程里面调用，mqtt 通道会报 Disconnecting is not allowed from a callback method (32107)
     */
    private void destroyRegisterConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LinkKit.getInstance().stopDeviceDynamicRegister(10 * 1000, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d("Login", "onSuccess() called with: iMqttToken = [" + iMqttToken + "]");
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.w("Login", "onFailure() called with: iMqttToken = [" + iMqttToken + "], throwable = [" + throwable + "]");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
