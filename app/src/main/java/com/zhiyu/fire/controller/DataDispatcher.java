package com.zhiyu.fire.controller;

/**
 * Created by Administrator on 2017/6/8 0008.
 * 资源调度器
 */

public class DataDispatcher {

    private NetInterface mNetInterface;
    private CacheInterface mCacheInterface;

    private static DataDispatcher mDefault;

    private DataDispatcher() {}

    public static DataDispatcher getDefault() {
        if (mDefault == null) {
            synchronized (DataDispatcher.class) {
                if (mDefault == null) {
                    mDefault = new DataDispatcher();
                }
            }
        }
        return mDefault;
    }

    public void setInterface(NetInterface netInterface, CacheInterface cacheInterface) {
        mNetInterface = netInterface;
        mCacheInterface = cacheInterface;
    }

    public interface NetInterface<T> {
        T getData();
    }

    public interface CacheInterface<T> {
        T getData();
    }

}