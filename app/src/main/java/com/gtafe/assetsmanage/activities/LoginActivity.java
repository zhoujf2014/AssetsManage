package com.gtafe.assetsmanage.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gtafe.assetsmanage.R;
import com.gtafe.assetsmanage.beans.EventBusBean;
import com.gtafe.assetsmanage.rfid.AssetsManageActivity;
import com.gtafe.assetsmanage.utils.ClearEditText;
import com.gtafe.assetsmanage.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.gtafe.assetsmanage.utils.Constant.PACKGE;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.user)
    ClearEditText mUser;
    @BindView(R.id.pwd)
    ClearEditText mPwd;
    @BindView(R.id.remenber)
    AppCompatCheckBox mRemenber;
    @BindView(R.id.login)
    Button mLogin;

    public SharedPreferences mSp;
    @BindView(R.id.ip_port)
    TextView mIpPort;
    public String mIp_port;


    @Override
    protected int setView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        initState();
        mSp = getSharedPreferences(PACKGE, MODE_PRIVATE);
        mIp_port = mSp.getString(Constant.IP_PORT, "10.1.34.23:808");
        mIpPort.setText("服务器地址：" + mIp_port);
        String passWord = mSp.getString(Constant.PASSWORD, null);
        String userName = mSp.getString(Constant.USERNAME, null);
        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(passWord)) {
            return;
        }
        mPwd.setText(passWord);
        mUser.setText(userName);
        RequestBody body = new FormBody.Builder().add("userName", userName).add("userPwd", passWord).build();
        loadDataFromServer("dataapi/user/checkUserLogin", body, 1);

    }

    @Override
    protected void activityEnd() {

    }

    @Override
    protected void loadDataFromServerSuccessful(String string, int tag) {
        super.loadDataFromServerSuccessful(string, tag);
        if (tag == 1) {
            if (string.contains("true")) {
                startActivity(new Intent(mContext, AssetsManageActivity.class));
                finish();
            } else {
                showToast("用户名或者密码错误");
            }
        }
        showToast(string);
    }

    @Override
    protected void loadDataFromServerFail(String s, int tag) {
        if (tag == 1) {
            showToast("服务器异常，请检查服务器");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login, R.id.ip_port})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ip_port:
                View dialog_ip_port = View.inflate(mContext, R.layout.dialog_ipport_edit, null);
                final ClearEditText edit_ip = (ClearEditText) dialog_ip_port.findViewById(R.id.dialog_edit);
                edit_ip.setText(mIp_port);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("请输入服务器IP和端口号")
                        .setCancelable(false)
                        .setView(dialog_ip_port)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ip_port = edit_ip.getText().toString().trim();
                                mIp_port = ip_port;
                                mIpPort.setText("服务器地址：" + mIp_port);

                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                 builder.show();
                break;
            case R.id.login:


                String userName = mUser.getText().toString().trim();
                String password = mPwd.getText().toString().trim();
                if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(password)) {
                    showToast("用户名或者密码不能为空！");
                    return;
                }
                SharedPreferences.Editor edit = mSp.edit();
                edit.putString(Constant.IP_PORT, mIp_port);
                edit.putString(Constant.PASSWORD, password);
                edit.putString(Constant.USERNAME, userName);
                edit.commit();

                Constant.HEAD = "http://" + mIp_port + "/";
                Log.e(TAG, "onViewClicked: "+mIpPort );
//测试
                startActivity(new Intent(mContext, AssetsManageActivity.class));
                finish();


                RequestBody body = new FormBody.Builder().add("userName", userName).add("userPwd", password).build();
                loadDataFromServer("dataapi/user/checkUserLogin", body, 1);
                break;
        }
    }
}
