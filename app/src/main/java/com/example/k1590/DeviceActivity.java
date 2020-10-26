package com.example.k1590;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceActivity extends AppCompatActivity {
    private ListView mListView;
    private ImageView iv_back;
    //数据
    private ArrayList<DeviceBean> mDatas;
    private Button mBtnSearch;
    private ChatListAdapter mAdapter;
    //蓝牙适配器
    private BluetoothAdapter mBtAdapter;

    private BluetoothDevice mBluetoothDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device);
        initDatas();
        initViews();
        registerBroadcast();
        init();
    }

    //初始化数据
    private void initDatas() {
        mDatas = new ArrayList<DeviceBean>();
        mAdapter = new ChatListAdapter(this, mDatas);
        //获取蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 列出所有的蓝牙设备
     */
    private void init() {
        Log.i("tag", "mBtAdapter=="+ mBtAdapter);
        if (mBtAdapter == null){
            return;
        }
        //根据适配器得到所有的设备信息
        Set<BluetoothDevice> deviceSet = mBtAdapter.getBondedDevices();
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                mDatas.add(new DeviceBean(device.getName() + "\n" + device.getAddress(), true));
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);
            }
        } else {
            mDatas.add(new DeviceBean("没有配对的设备", true));
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mDatas.size() - 1);
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        //设备被发现广播
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, discoveryFilter);

        // 设备发现完成
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, foundFilter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        mListView = findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(mDeviceClickListener);

        iv_back = findViewById(R.id.iv_back);
        mBtnSearch = findViewById(R.id.start_search);
        mBtnSearch.setOnClickListener(mSearchListener);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * 搜索监听
     */
    private View.OnClickListener mSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (mBtAdapter == null){
                return;
            }
            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
                mBtnSearch.setText("重新搜索");
            } else {
                mDatas.clear();
                mAdapter.notifyDataSetChanged();

                init();

                /* 开始搜索 */
                mBtAdapter.startDiscovery();
                mBtnSearch.setText("停止搜索");
            }
        }
    };

    /**
     * 点击设备监听
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            DeviceBean bean = mDatas.get(position);
            String info = bean.message;
            final String address = info.substring(info.length() - 17);
            MainActivity.BlueToothAddress = address;

            AlertDialog.Builder stopDialog = new AlertDialog.Builder(DeviceActivity.this);
            stopDialog.setTitle("连接");//标题
            stopDialog.setMessage(bean.message);
            stopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mBtAdapter.cancelDiscovery();
                    mBtnSearch.setText("重新搜索");
                    mBluetoothDevice = mBtAdapter.getRemoteDevice(address);
                    if (mBluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        Method creMethod = null;
                        try {
                            creMethod = BluetoothDevice.class
                                    .getMethod("createBond");

                            Log.e("TAG", "开始配对");
                            creMethod.invoke(mBluetoothDevice);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else {
                        startActivity(new Intent(DeviceActivity.this, MainActivity.class));
                        finish();
                    }

                    dialog.cancel();
                }
            });
            stopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //BluetoothActivity.BlueToothAddress = null;
                    dialog.cancel();
                }
            });
            stopDialog.show();
        }
    };

    /**
     * 设备广播
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获得设备信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果绑定的状态不一样
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDatas.add(new DeviceBean(device.getName() + "\n" + device.getAddress(), false));
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mDatas.size() - 1);
                }
                // 如果搜索完成了
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (mListView.getCount() == 0) {
                    mDatas.add(new DeviceBean("没有发现蓝牙设备", false));
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mDatas.size() - 1);
                }
                mBtnSearch.setText("重新搜索");
            }else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int status = device.getBondState();
                switch (status) {
                    case BluetoothDevice.BOND_BONDED:
                        startActivity(new Intent(DeviceActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        }
    };

    //activity生命周期中onStart（）方法，当activity启动时被调用
    @Override
    public void onStart() {
        super.onStart();
        //如果蓝牙未打开，则弹出打开蓝牙授权窗口
        if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);
        }
    }

    //activity生命周期，当activity销毁时被调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }
}
