package com.tpnet.processordemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Litp on 2017/11/7.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        SDKInitializer.initialize(this);

    }
}
