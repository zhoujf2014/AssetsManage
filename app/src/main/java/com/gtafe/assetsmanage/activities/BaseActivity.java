package com.gtafe.assetsmanage.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gtafe.assetsmanage.utils.NetWorkUtil;
import com.gtafe.assetsmanage.utils.NetworkInterface;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * Created by ZhouJF on 2018/1/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected boolean isDebug = true;

    private static final int SECCESSFUL = 1;
    private static final int FAILURE = 2;


    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setView());
        //  EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        mContext = this;
        init();
    }

    protected abstract void init();

    protected abstract int setView();

    protected abstract void activityEnd();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityEnd();
        //    EventBus.getDefault().unregister(this);
    }

    protected void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void showLog(String TAG, String msg) {
        if (isDebug) {
            Log.e("APPNAME:assetsmanage " + TAG, msg);
        }
    }

    protected void showToast(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    protected void loadDataFromServer(String url) {

        new NetWorkUtil(netInterface).loadDataFromServer(url);
    }

    private NetworkInterface netInterface = new NetworkInterface() {
        @Override
        public void onLoadDataSuccese(final Response response) {
            String string = null;
            try {
                string = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String finalString = string;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                        loadDataFromServerSuccessful(finalString);
                }
            });
        }

        @Override
        public void onLoadDataFail(final String s) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataFromServerFail(s);
                }
            });
        }


    };

    protected void loadDataFromServerFail(String s) {

    }

    protected void loadDataFromServerSuccessful(String string) {

    }
}