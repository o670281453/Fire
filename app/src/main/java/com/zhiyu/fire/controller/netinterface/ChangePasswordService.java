package com.zhiyu.fire.controller.netinterface;

import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.model.ChangePasswordRequest;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/6/19 0019.
 * 修改密码的Retrofit接口
 */

public class ChangePasswordService {

    private static WeakReference<IChangePasswordService> weakReference;

    public static IChangePasswordService getIChangePasswordService() {
        if (weakReference == null || weakReference.get() == null) {
            IChangePasswordService service = MyRetrofit.getRetrofit().create(IChangePasswordService.class);
            weakReference = new WeakReference<>(service);
        }
        return weakReference.get();
    }

    public interface IChangePasswordService {
        @POST("api")
        @FormUrlEncoded
        Observable<ChangePasswordRequest> change(@Field("action") int action, @Field("token")String token, @Field("password")String password);
    }

}