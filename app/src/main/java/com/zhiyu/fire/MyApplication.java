package com.zhiyu.fire;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
/**
 * Created by Administrator on 2017/6/9 0009.
 * Application类
 */

public class MyApplication extends Application {

    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;

    private static int mode;//用于表示此时应用处于离线状态还是在线状态

    // 百度定位最后一次定位得到的位置,用于搜索界面
    public BDLocation mLocation;
    public static MyApplication sApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mode = ONLINE;
        SDKInitializer.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        MyApplication.mode = mode;
    }

}