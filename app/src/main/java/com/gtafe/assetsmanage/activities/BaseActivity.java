package com.gtafe.assetsmanage.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.gtafe.assetsmanage.beans.EventBusBean;
import com.gtafe.assetsmanage.utils.NetWorkUtil;
import com.gtafe.assetsmanage.utils.NetworkInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ZhouJF on 2018/1/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected Context mContext;
    protected boolean isDebug = true;

    private static final int SECCESSFUL = 1;
    private static final int FAILURE = 2;


    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setView());
         EventBus.getDefault().register(this);
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
          EventBus.getDefault().unregister(this);
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

    protected void loadDataFromServer(String url, RequestBody body, int tag) {

        new NetWorkUtil(netInterface).loadDataFromServer(url,body,tag);
    }

    private NetworkInterface netInterface = new NetworkInterface() {
        @Override
        public void onLoadDataSuccese(final Response response, final int tag) {
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
                    loadDataFromServerSuccessful(finalString,tag);
                }
            });
        }

        @Override
        public void onLoadDataFail(final String s, final int tag) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataFromServerFail(s,tag);
                }
            });
        }
    };

    protected void loadDataFromServerFail(String s, int tag) {
        Log.e(TAG, "loadDataFromServerFail: "+s );
    }

    protected void loadDataFromServerSuccessful(String string, int tag) {

    }
    /**
     * 动态的设置状态栏  实现沉浸式状态栏
     */
    protected void initState() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      /*      getWindow().addFlags(WindowManager.LayoutParams.Flag_T);
            //透明导航栏---虚拟键冲突
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

//            LinearLayout linear_bar = (LinearLayout) findViewById(R.id.ll_bar);
            mBaseBinding.llBar.setVisibility(View.VISIBLE);
            //获取到状态栏的高度
            int statusHeight = getStatusBarHeight();
            //动态的设置隐藏布局的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBaseBinding.llBar.getLayoutParams();
            params.height = statusHeight;
            mBaseBinding.llBar.setLayoutParams(params);*/
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusBean eventBusBean) {
        try {
            onEventBusMain(eventBusBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected  void onEventBusMain(EventBusBean eventBusBean) throws IOException {

    }
}