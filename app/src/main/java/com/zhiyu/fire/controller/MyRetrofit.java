package com.zhiyu.fire.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.zhiyu.fire.model.UserRequest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 * Retrofit + RxJava二次封装的网络库
 */

public class MyRetrofit {

    public static final String BASE_URL = "http://172.16.4.147/";
    private static Retrofit sRetrofit;

    public static final int LOGIN = 0;
    public static final int CHANGE_PASSWORD = 1;

    public static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            synchronized (UserRequest.class) {
                if (sRetrofit == null) {
                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return sRetrofit;
    }

    //检查当前是否有网
    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

}