package com.gtafe.assetsmanage.rfid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gtafe.assetsmanage.R;
import com.gtafe.assetsmanage.activities.AssertsDetailActivity;
import com.gtafe.assetsmanage.activities.BaseActivity;
import com.gtafe.assetsmanage.beans.EventBusBean;
import com.gtafe.assetsmanage.beans.GetInstrumentInfo;
import com.gtafe.assetsmanage.utils.Util;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android_serialport_api.M100_RFID_API;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by ZhouJF on 2018/1/4.
 */

@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
public class AssetsManageActivity extends BaseActivity {
    private static final String TAG = "AssetsManageActivity";

    private final static int REQUEST_CONNECT_DEVICE = 1; // 连接蓝牙
    private final static int REQUEST_SCAN = 2; // 宏扫描
    public static final String ASSERTSDETAIL = "assertsdetail";
    public static final String INTERFACE = "interface";

    final byte MSG_M100CMDRESP = 0X08;
    final byte MSG_M100DATARESP_INV = 0X0B;
    private boolean isRfidStart = false;

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号
    @BindView(R.id.connectrfid)
    Button mConnectrfid;
    public M100_RFID_API rfid = new M100_RFID_API();
    BluetoothDevice _device = null; // 蓝牙设备
    BluetoothSocket _socket = null; // 蓝牙通信socket
    boolean deviceConnectting = false;
    boolean neetStart = false;
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备

    @Override
    protected int setView() {
        return R.layout.activity_assetsmanage;
    }

    @Override
    protected void init() {
        ZXingLibrary.initDisplayOpinion(this);
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

    public void ModifyListView(String tagdata) {
        EventBusBean eventBusBean = new EventBusBean();
        eventBusBean.setMessage(tagdata);
        eventBusBean.setType(3);
        EventBus.getDefault().post(eventBusBean);
        Log.e(TAG, "ModifyListView: " + tagdata);

    }

    // 连接设备成功
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            String string = bundle.getString(CodeUtils.RESULT_STRING);
            if (string == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + string);
            RequestBody body = new FormBody.Builder().add("instNo", string).build();
            loadDataFromServer("api/Instrument/GetInstrumentInfo", body, 1);
            showToast(string);
            return;
        }
    }

    private void connectBlutooth(String address) {
        _device = mBtAdapter.getRemoteDevice(address);
        // 用服务号得到socket
        try {
            _socket = _device.createRfcommSocketToServiceRecord(UUID
                    .fromString(MY_UUID));
        } catch (IOException e) {

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

        }
        // 打开接收线程
        try {
            rfid.M100_SetBluetoothSocket(_socket, 1);// 设置蓝牙Socket 1
            rfid.M100SetMessageHandler(myHandler);
            Util.SetRFID(rfid);
            deviceConnectting = true;
            mConnectrfid.setText("已连接RFID设备");
            if (neetStart) {
                rfid.M100StartInvTag(200);
                EventBusBean eventBusBean = new EventBusBean();
                Log.e(TAG, "M100StartInvTag: 111");
                eventBusBean.setType(5);
                eventBusBean.setMessage("RFID设备开启成功2");
                EventBus.getDefault().post(eventBusBean);
                neetStart = false;
            }

        } catch (IOException e) {
            Toast.makeText(this,
                    getResources().getString(R.string.info_revfail),
                    Toast.LENGTH_SHORT).show();
            return;
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
                    int length = x6invdatabyte.length;
                    String hexString = "" + String.format("%1$02x", x6invdatabyte[length - 3]) + String.format("%1$02x", x6invdatabyte[length - 2]) + String.format("%1$02x", x6invdatabyte[length - 1]);
                    String DString = toD(hexString, 16);
                    System.out.println("ByteArray hexString:" + hexString);
                    System.out.println("ByteArray hexString:" + DString);
                    ModifyListView(DString);

                    break;
                case MSG_M100CMDRESP:
                    Bundle x6ver = msg.getData();
                    String x6HWver = x6ver.getString("HWver");
                    String x6SWver = x6ver.getString("SWver");
                    System.out.println("x6HWver: " + x6HWver);
                    System.out.println("x6SWver: " + x6SWver);
                    ShowDevVersion(x6HWver, x6SWver);

                    break;

            }

        }
    };

    // 关闭程序调用处理部分
    public void onDestroy() {
        super.onDestroy();
        deviceConnectting = false;
        if (_socket != null) // 关闭连接socket
            try {
                _socket.close();
            } catch (IOException e) {
            }
        // mBtAdapter.disable(); // 关闭蓝牙服务
    }

    public void onConnectButton() throws IOException {
        if (mBtAdapter.isEnabled() == false) { // 如果蓝牙服务不可用则提示
            mBtAdapter.enable();
        }

        // 如未连接设备则打开DeviceListActivity进行设备搜索
        if (_socket == null) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class); // 跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义


        } else {

            showToast("已连接RFID设备");

        }
        return;
    }


    @OnClick({R.id.connectrfid, R.id.start, R.id.scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.scan:
                startActivityForResult(new Intent(mContext, CaptureActivity.class), REQUEST_SCAN);
                break;
            case R.id.connectrfid:
                try {
                    //连接设备
                    neetStart = false;
                    onConnectButton();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start:
                if (_socket == null) {
                    showToast("请先连接RFID");
                    return;
                }
                if (isRfidStart) {
                    try {
                        rfid.M100StopInvTag();
                        isRfidStart = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        rfid.M100StartInvTag(200);
                        isRfidStart = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // 任意进制数转为十进制数
    public String toD(String a, int b) {//---------------------------a为16进制，b=16；三
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }

    @Override
    protected void onEventBusMain(EventBusBean eventBusBean) throws IOException {

        switch (eventBusBean.getType()) {
            case 0:
                connectBlutooth(eventBusBean.getMessage());
                break;
            case 1:
                if (deviceConnectting == true) {
                    rfid.M100StartInvTag(200);
                    eventBusBean.setType(5);
                    //  Log.e(TAG, "M100StartInvTag:222");
                    eventBusBean.setMessage("RFID设备开启成功1");
                    //   EventBus.getDefault().post(eventBusBean);
                } else {
                    neetStart = true;
                    onConnectButton();
                }
                break;
            case 2:
                if (_socket != null) {
                    if (deviceConnectting == true) {
                        rfid.M100StopInvTag();

                    }
                }
                break;
        }
    }

    @Override
    protected void loadDataFromServerSuccessful(String string, int tag) {
        super.loadDataFromServerSuccessful(string, tag);
        Gson gson = new Gson();
        if (tag == 1) {
            GetInstrumentInfo getInstrumentInfo = gson.fromJson(string, GetInstrumentInfo.class);
          //  showToast(getInstrumentInfo.getData().getInstrumentName());

            if (getInstrumentInfo != null) {
                Intent intent = new Intent(mContext, AssertsDetailActivity.class);

                intent.putExtra(ASSERTSDETAIL, getInstrumentInfo);

                startActivity(intent);
            }
        }
    }

    public int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("a")) {
            i = 10;
        }
        if (a.equals("b")) {
            i = 11;
        }
        if (a.equals("c")) {
            i = 12;
        }
        if (a.equals("d")) {
            i = 13;
        }
        if (a.equals("e")) {
            i = 14;
        }
        if (a.equals("f")) {
            i = 15;
        }
        return i;
    }

}
