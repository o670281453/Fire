package com.zhiyu.fire.controller.FragmentController;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.zhiyu.fire.R;
import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.controller.netinterface.LoginService;
import com.zhiyu.fire.model.UserRequest;
import com.zhiyu.fire.ui.FragmentLifeCycle;
import com.zhiyu.fire.ui.LoginFragment;
import com.zhiyu.fire.util.DialogUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/14 0014.
 * 登录界面控制器
 */

public class LoginFragmentController implements FragmentLifeCycle, LoginFragment.ILoginInterface{

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void login(AppCompatActivity activity, String username, String password) {
        LoginService.getLoginService().login(MyRetrofit.LOGIN, username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserRequest>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UserRequest userRequest) {
                        if (userRequest.getStatus() == 1) {
                            userRequest.getUser().setUsername(username);
                            UserRequest.setsUser(userRequest.getUser());
                            SharedPreferences.Editor editor = activity.getSharedPreferences(
                                    activity.getString(R.string.user_file)
                                    , Context.MODE_PRIVATE).edit();
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.apply();
                            activity.onBackPressed();
                            Toast.makeText(activity, "登录成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "登录失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("登录", e.getMessage());
                        DialogUtil.showDialog(activity, "网络请求错误，请稍后再试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}