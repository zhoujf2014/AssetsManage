package com.gtafe.assetsmanage.rfid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gtafe.assetsmanage.R;
import com.gtafe.assetsmanage.activities.BaseActivity;
import com.gtafe.assetsmanage.rfid.DeviceListActivity;
import com.gtafe.assetsmanage.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import android_serialport_api.M100_RFID_API;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ZhouJF on 2018/1/4.
 */

public class AssetsManageActivity extends BaseActivity {
    private static final String TAG = "AssetsManageActivity";

    private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄

    final byte MSG_M100CMDRESP = 0X08;
    final byte MSG_M100DATARESP_INV = 0X0B;

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号
    @BindView(R.id.connectrfid)
    Button mConnectrfid;

    private ListView Taglist;
    public M100_RFID_API rfid = new M100_RFID_API();
    BluetoothDevice _device = null; // 蓝牙设备
    BluetoothSocket _socket = null; // 蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;
    boolean bCycles = false;
    boolean goon = false;
    int taglenth = 0;
    boolean isLen = false;

    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备

    @Override
    protected int setView() {
        return R.layout.activity_assetsmanage;
    }

    @Override
    protected void init() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter.isEnabled() == false) {
            mBtAdapter.enable();
        }
    }

    @Override
    protected void activityEnd() {

    }

    public void ShowDevVersion(String HWver, String SWver) {
        Toast toast = Toast.makeText(this, "HardWareVer = " + HWver + "\n\n\n"
                + "SoftWareVer = " + SWver, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void ModifyListView(String tagdata) throws IOException {
        Log.e(TAG, "ModifyListView: "+tagdata );
      /*  HashMap<String, String> maptemp = new HashMap<String, String>();
        HashMap<String, String> mapnew = new HashMap<String, String>();
        boolean inTheList = false;

        Iterator<HashMap<String, String>> it = list.iterator(); // 迭代器
        // 遍历ArrayList
        while (it.hasNext()) {
            maptemp = it.next();
            if (tagdata.equals(maptemp.get("TagData"))) // 存在于ArrayList中
            {
                maptemp.put("TagData", tagdata);
                maptemp.put("CountNum",
                        "" + (Integer.parseInt(maptemp.get("CountNum")) + 1));
                list.set(list.indexOf(maptemp), maptemp);
                inTheList = true;
            }

        }
        if (inTheList == false) {
            mapnew.put("TagData", tagdata);
            mapnew.put("CountNum", "1");
            list.add(mapnew);
            int n = list.size() - 1;
            tagall.setText("" + n);

        }
*/
        //list 为结果集合

    }

    // 接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄

                    _device = mBtAdapter.getRemoteDevice(address);
                    // 用服务号得到socket
                    try {
                        _socket = _device.createRfcommSocketToServiceRecord(UUID
                                .fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast toast = Toast.makeText(
                                this,
                                getResources().getString(R.string.act_btconn)
                                        + getResources().getString(
                                        R.string.act_btconnfail),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout toastView = (LinearLayout) toast.getView();
                        ImageView imageCodeProject = new ImageView(
                                getApplicationContext());
                        imageCodeProject.setImageResource(R.drawable.icon_r);
                        toastView.addView(imageCodeProject, 0);
                        toast.show();
                    }
                    // 连接socket
                    try {
                        _socket.connect();
                        Toast toast = Toast.makeText(
                                this,
                                getResources().getString(R.string.act_btconn)
                                        + _device.getName()
                                        + getResources().getString(
                                        R.string.act_btconngood),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout toastView = (LinearLayout) toast.getView();
                        ImageView imageCodeProject = new ImageView(
                                getApplicationContext());
                        imageCodeProject.setImageResource(R.drawable.icon);
                        toastView.addView(imageCodeProject, 0);
                        toast.show();


                    } catch (IOException e) {
                        try {
                            _socket.close();
                            _socket = null;
                            Toast toast = Toast.makeText(
                                    this,
                                    getResources().getString(R.string.act_btconn)
                                            + getResources().getString(
                                            R.string.act_btconnfail),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastView = (LinearLayout) toast.getView();
                            ImageView imageCodeProject = new ImageView(
                                    getApplicationContext());
                            imageCodeProject.setImageResource(R.drawable.icon_r);
                            toastView.addView(imageCodeProject, 0);
                            toast.show();

                        } catch (IOException ee) {
                            Toast toast = Toast.makeText(
                                    this,
                                    getResources().getString(R.string.act_btconn)
                                            + getResources().getString(
                                            R.string.act_btconnfail),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastView = (LinearLayout) toast.getView();
                            ImageView imageCodeProject = new ImageView(
                                    getApplicationContext());
                            imageCodeProject.setImageResource(R.drawable.icon_r);
                            toastView.addView(imageCodeProject, 0);
                            toast.show();
                        }
                        return;
                    }
                    // 打开接收线程
                    try {
                        rfid.M100_SetBluetoothSocket(_socket, 1);// 设置蓝牙Socket 1
                        rfid.M100SetMessageHandler(myHandler);
                        Util.SetRFID(rfid);
                    } catch (IOException e) {
                        Toast.makeText(this,
                                getResources().getString(R.string.info_revfail),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    // 消息处理队列
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_M100DATARESP_INV:
                    Bundle x6b4 = msg.getData();
                    String x6EPCstr4 = x6b4.getString("InvData");
                    byte[] x6invdatabyte = x6b4.getByteArray("InvDatabyte");
                    String x6invtt = "";
                    for (int i = 0; i < x6invdatabyte.length; i++) {
                        x6invtt += String.format("%1$02x", x6invdatabyte[i]);
                    }
                    System.out.println("ByteArray INV:" + x6invtt);
                    System.out.println("handle INV: " + x6EPCstr4);

                    try {
                        ModifyListView(x6EPCstr4);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case MSG_M100CMDRESP:
                    Bundle x6ver = msg.getData();
                    String x6HWver = x6ver.getString("HWver");
                    String x6SWver = x6ver.getString("SWver");

                    System.out.println("x6HWver: " + x6HWver);
                    System.out.println("x6SWver: " + x6SWver);
                    ShowDevVersion(x6HWver, x6SWver);

                    break;
                default:
                    break;
            }

        }
    };

    // 关闭程序调用处理部分
    public void onDestroy() {
        super.onDestroy();
        if (_socket != null) // 关闭连接socket
            try {
                _socket.close();
            } catch (IOException e) {
            }
        mBtAdapter.disable(); // 关闭蓝牙服务
    }

    public void onConnectButton() throws IOException {
        if (mBtAdapter.isEnabled() == false) { // 如果蓝牙服务不可用则提示
            Toast toast = Toast.makeText(this, R.string.info_nobt,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView imageCodeProject = new ImageView(getApplicationContext());
            imageCodeProject.setImageResource(R.drawable.icon_r);
            toastView.addView(imageCodeProject, 0);
            toast.show();
            return;
        }

        // 如未连接设备则打开DeviceListActivity进行设备搜索
        if (_socket == null) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class); // 跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
            mConnectrfid.setText("点击断开");

        } else {
            rfid.M100_CloseBluetoothSocket();// DisConnected
            _socket.close();
            _socket = null;
            mConnectrfid.setText("点击连接");

            // btn.setImageResource(R.drawable.icon_r);
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.connectrfid)
    public void onViewClicked() {
        try {
            onConnectButton();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
