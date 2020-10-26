package com.example.k1590;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cn.mtjsoft.www.gridpager.GridPager;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //常量
    public static final int STATUS_CONNECT = 0x11;
    private Button bt_light, bt_temp;
    private TextView tv_ji, tv_deng;
    private GridPager pager;
    private SharedPreferences sp;
    private PopupWindow pw;
    private DBHelper helper;
    private String user_name;
    private ImageView iv_setting;
    private List<ThingBean> thingList = new ArrayList<>();
    //蓝牙地址
    static String BlueToothAddress = null;
    // 蓝牙客户端socket
    private BluetoothSocket mSocket;
    // 设备
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    //客户端线程
    private ClientThread mClientThread;
    //读取消息线程
    private ReadThread mReadThread;

    private StringBuffer rep_info = new StringBuffer();

    InputStream is = null;

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


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String address = BlueToothAddress;
        //判断要连接的蓝牙地址是否为空，不为空则开始连接
        if (!TextUtils.isEmpty(address)) {
            //蓝牙连接配对
            mDevice = mBluetoothAdapter.getRemoteDevice(address);
            //创建客户端线程对象
            mClientThread = new ClientThread();
            //启动线程
            mClientThread.start();

        }
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
        TextView delete = view.findViewById(R.id.delete);
        TextView dian = view.findViewById(R.id.dian);
        TextView light = view.findViewById(R.id.light);
        TextView logout = view.findViewById(R.id.logout);
        TextView search = view.findViewById(R.id.search);
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
        search.setOnClickListener(this);
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
            case R.id.search:
                startActivity(new Intent(this, DeviceActivity.class));
                finish();
                pw.dismiss();
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
                String light_order = sp.getString("light_order", null);
                showDialog(this, "当前光照","");
                break;
            case R.id.bt_temp:
                String temp_order = sp.getString("temp_order", null);
                showDialog(this, "当前温度","");
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


    /* ͣ停止客户端连接 */
    private void shutdownClient() {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mClientThread != null) {
            mClientThread.interrupt();
            mClientThread = null;
        }
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }

    // 发送数据
    private void sendMessageHandle(String msg) {
        if (mSocket == null) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = mSocket.getOutputStream();
            os.write(msg.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 读取数据
    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            try {
                is = mSocket.getInputStream();
                while (true) {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        if (buf_data != null) {
                            String s = new String(buf_data,"UTF-8");
                            Message msg = new Message();
                            msg.obj = s;
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    //线程间消息处理（主线程和子线程中消息处理）
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String info = (String) msg.obj;
            switch (msg.what){
                case STATUS_CONNECT:
                    //显示toast
                    Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:

                    break;
                case 2:

                    break;
            }
        }
    };

    // 客户端线程
    private class ClientThread extends Thread {
        public void run() {
            try {
                //客户端与服务端连接
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                //给主线程发送状态信息
                Message msg = new Message();
                msg.obj = "请稍候，正在连接服务器:" + BlueToothAddress;
                msg.what = STATUS_CONNECT;
                handler.sendMessage(msg);

                mSocket.connect();

                msg = new Message();
                msg.obj = "已经连接上服务端！可以接收信息。";
                msg.what = STATUS_CONNECT;
                handler.sendMessage(msg);
                // 启动接受数据线程
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = STATUS_CONNECT;
                handler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭客户端连接
        shutdownClient();
        handler.removeCallbacksAndMessages(null);
        if (pw != null && pw.isShowing()){
            pw.dismiss();
        }
    }
}
