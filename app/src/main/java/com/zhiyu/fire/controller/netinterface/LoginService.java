package com.zhiyu.fire.controller.netinterface;

import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.model.UserRequest;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/6/12 0012.
 * 登录Retrofit接口
 */

public class LoginService {

    private static WeakReference<ILoginService> serviceWeakReference;

    public static ILoginService getLoginService() {
        if (serviceWeakReference == null || serviceWeakReference.get() == null) {
            serviceWeakReference = new WeakReference<>(MyRetrofit
                    .getRetrofit().create(ILoginService.class));
        }
        return serviceWeakReference.get();
    }

    public interface ILoginService {
        @POST("api")
        @FormUrlEncoded
        Observable<UserRequest> login(@Field("action") int action, @Field("username")String fireManId, @Field("password")String password);
    }

}