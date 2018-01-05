package com.gtafe.assetsmanage.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gtafe.assetsmanage.activities.LoginActivity;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZhouJF on 2018/1/4.
 */

public class NetWorkUtil {
    private OkHttpClient mOkHttpClient;
    private NetworkInterface mNetworkInterface;



    public NetWorkUtil(NetworkInterface networkInterface) {
        mNetworkInterface = networkInterface;
    }


    public void loadDataFromServer(String url) {

        mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                mNetworkInterface.onLoadDataFail("访问网络失败");

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.e("NetWorkUtil", "获取数据成功了");
                    Log.e("NetWorkUtil", "response.code()==" + response.code());
                    Log.e("NetWorkUtil", "response.body().string()==" + response.body());
                    mNetworkInterface.onLoadDataSuccese(response);

                } else {
                    mNetworkInterface.onLoadDataFail("返回数据为空");


                }
            }
        });
    }
}
