package com.gtafe.assetsmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gtafe.assetsmanage.R;
import com.gtafe.assetsmanage.rfid.AssetsManageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.rb_student)
    RadioButton mRbStudent;
    @BindView(R.id.login)
    Button mLogin;
    @BindView(R.id.rg)
    RadioGroup mRg;

    @Override
    protected int setView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        loadDataFromServer("http://www.baidu.com");
    }

    @Override
    protected void activityEnd() {

    }

    @Override
    protected void loadDataFromServerSuccessful(String string) {
        super.loadDataFromServerSuccessful(string);
        showToast(string);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login)
    public void onViewClicked() {
        switch (mRg.getCheckedRadioButtonId()) {
            case R.id.rb_student:
                break;
            case R.id.rb_teacher:
                startActivity(new Intent(mContext,AssetsManageActivity.class));
                break;
        }
    }
}
