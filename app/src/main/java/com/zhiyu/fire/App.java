package com.zhiyu.fire;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;

public class App extends Application {
    public static App sApp;
    // 百度定位最后一次定位得到的位置,用于搜索界面
    public BDLocation mLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
