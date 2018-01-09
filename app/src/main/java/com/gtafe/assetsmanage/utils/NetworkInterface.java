package com.gtafe.assetsmanage.utils;

import okhttp3.Response;

/**
 * Created by ZhouJF on 2018/1/4.
 */

public interface NetworkInterface {

    void onLoadDataSuccese(Response response,int tag);
    void onLoadDataFail(String s,int tag);
}
