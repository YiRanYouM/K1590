package com.example.k1590;

import androidx.appcompat.app.AppCompatActivity;
import cn.mtjsoft.www.gridpager.GridPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttConfigure;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.tools.AError;
import java.util.ArrayList;
import java.util.List;
import static com.example.k1590.LoginActivity.ProductKey;
import static com.example.k1590.LoginActivity.deviceName;
import static com.example.k1590.LoginActivity.deviceSecret;

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
    /* 自动Topic, 用于上报消息 */
    static final private String PUB_TOPIC = "/" + ProductKey + "/" + deviceName + "/user/update";
    /* 自动Topic, 用于接受消息 */
    final private String SUB_TOPIC = "/" + ProductKey + "/" + deviceName + "/user/get";
    private String deviceToken = null;
    private String clientId = null;

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
        deviceToken = sp.getString("deviceToken", null);
        clientId = sp.getString("clientId", null);

        LinkKit.getInstance().registerOnPushListener(notifyListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = ProductKey;// 产品类型
        deviceInfo.deviceName = deviceName;// 设备名称
        deviceInfo.deviceSecret = deviceSecret;// 设备密钥

        MqttConfigure.deviceToken = deviceToken;
        MqttConfigure.clientId = clientId;

        IoTMqttClientConfig clientConfig = new IoTMqttClientConfig(ProductKey, deviceName, deviceSecret);

        LinkKitInitParams params = new LinkKitInitParams();
        params.deviceInfo = deviceInfo;
        params.mqttClientConfig = clientConfig;

        /**
         * 设备初始化建联
         * onError 初始化建联失败，需要用户重试初始化。如因网络问题导致初始化失败。
         * onInitDone 初始化成功
         */
        LinkKit.getInstance().init(this, params, new ILinkKitConnectListener() {
            @Override
            public void onError(AError error) {
                // 初始化失败 error包含初始化错误信息
                System.out.println("00000000000000000000");
            }

            @Override
            public void onInitDone(Object data) {
                // 初始化成功 data 作为预留参数
                System.out.println("1111111111111222222222222222");
                subscribeTopic(SUB_TOPIC);
            }
        });

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
                        String data = null;
                        String order = thingList.get(position).getOrder();
                        if (thingList.get(position).getType().equals("0")) {
                            data = String.format("{'led':'%s'}", order);
                        }else if (thingList.get(position).getType().equals("1")){
                            data = String.format("{'power':'%s'}", order);
                        }
                        publishMessage(data);
                    }
                })
                .show();
    }

    private void menuPop(){
        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        TextView add = view.findViewById(R.id.add);
        TextView delete = view.findViewById(R.id.delete);
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
        delete.setOnClickListener(this);
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
            case R.id.delete:
                Intent intent5 = new Intent(this, DeleteActivity.class);
                intent5.putExtra("name", user_name);
                startActivity(intent5);
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
                String light_order = sp.getString("light_order", "/j/");
                String data_light = String.format("{'light_key':'%s'}",light_order);
                publishMessage(data_light);
                break;
            case R.id.bt_temp:
                String temp_order = sp.getString("temp_order", "/i/");
                String data = String.format("{'temp':'%s'}",temp_order);
                publishMessage(data);
                break;
            case R.id.iv_setting:
                menuPop();
                break;
            default:
                break;
        }
    }

    private void showDialog(Context context, String pram1, String pram2){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);
        final TextView title = view.findViewById(R.id.tv_title);
        TextView value = view.findViewById(R.id.tv_value);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        Button bt_save = view.findViewById(R.id.bt_save);
        final EditText edt_order = view.findViewById(R.id.edit_one_order);
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
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order = edt_order.getText().toString();
                if (!TextUtils.isEmpty(order)) {
                    if (title.equals("当前光照")){
                        sp.edit().putString("light_order", order).commit();
                    }else {
                        sp.edit().putString("temp_order", order).commit();
                    }
                }

                dialog.dismiss();
            }
        });
    }

    IConnectNotifyListener notifyListener = new IConnectNotifyListener() {
        @Override
        public void onNotify(String connectId, String topic, AMessage aMessage) {
            // 云端下行数据回调
            // connectId 连接类型 topic 下行 topic; aMessage 下行数据
            // 数据解析如下：
            //String pushData = new String((byte[]) aMessage.data);
            //System.out.println("pushData---->"+pushData);
            // pushData 示例  {"method":"thing.service.test_service","id":"123374967","params":{"vv":60},"version":"1.0.0"}
            // method 服务类型； params 下推数据内容
            if (topic.equals(SUB_TOPIC)) {
                String pushData = new String((byte[]) aMessage.data);
                System.out.println("pushData---->" + pushData);
                JSONObject object = JSONObject.parseObject(pushData);
                String temp_value = object.getString("tem");
                if (temp_value != null) {
                    showDialog(MainActivity.this, "当前温度", temp_value);
                }

                String light_value = object.getString("light");
                if (light_value != null){
                    showDialog(MainActivity.this, "当前光照",light_value);
                }
            }
        }

        @Override
        public boolean shouldHandle(String connectId, String topic) {
            // 选择是否不处理某个 topic 的下行数据
            // 如果不处理某个topic，则onNotify不会收到对应topic的下行数据
            System.out.println("handel---->"+topic);
            return true; //TODO 根基实际情况设置
        }

        @Override
        public void onConnectStateChange(String connectId, ConnectState connectState) {
            // 对应连接类型的连接状态变化回调，具体连接状态参考 SDK ConnectState
        }
    };

    /**
     * 订阅主题
     * @param topic
     */
    public void subscribeTopic(String topic) {
        MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
        // subTopic 替换成用户自己需要订阅的 topic
        subscribeRequest.topic = topic;
        subscribeRequest.isSubscribe = true;
        subscribeRequest.qos = 0; // 支持0或者1
        LinkKit.getInstance().subscribe(subscribeRequest, new IConnectSubscribeListener() {
            @Override
            public void onSuccess() {
                // 订阅成功
                System.out.println("订阅成功");
            }

            @Override
            public void onFailure(AError aError) {
                // 订阅失败
            }
        });
    }

    /**
     * 向默认的主题/user/update发布消息
     * @param data 消息载荷
     */
    public static void publishMessage(String data) {
        // 发布
        MqttPublishRequest request = new MqttPublishRequest();
        request.isRPC = false;
        // topic 替换成用户自己需要发布的 topic
        request.topic = PUB_TOPIC;
        // 设置 qos
        request.qos = 0;
        // data 替换成用户需要发布的数据 json String
        //示例 属性上报 {"id":"160865432","method":"thing.event.property.post","params":{"LightSwitch":1},"version":"1.0"}
        request.payloadObj = data;
        LinkKit.getInstance().publish(request, new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
                // 发布成功
                Log.i("MainActivity", "publish succeed!");
            }

            @Override
            public void onFailure(ARequest aRequest, AError aError) {
                // 发布失败
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pw != null && pw.isShowing()){
            pw.dismiss();
        }

        LinkKit.getInstance().unRegisterOnPushListener(notifyListener);
    }
}
