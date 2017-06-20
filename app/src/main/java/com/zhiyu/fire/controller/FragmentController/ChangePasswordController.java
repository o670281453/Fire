package com.zhiyu.fire.controller.FragmentController;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.zhiyu.fire.R;
import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.controller.netinterface.ChangePasswordService;
import com.zhiyu.fire.model.ChangePasswordRequest;
import com.zhiyu.fire.model.UserRequest;
import com.zhiyu.fire.ui.ChangePasswordFragment;
import com.zhiyu.fire.ui.MainActivity;
import com.zhiyu.fire.util.DialogUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/19 0019.
 * 修改密码界面控制器
 */

public class ChangePasswordController implements ChangePasswordFragment.IChangePassword {

    @Override
    public void change(final AppCompatActivity activity, final String password) {
        if (MyRetrofit.checkNetwork(activity.getApplicationContext())) {
            ChangePasswordService.getIChangePasswordService().change(MyRetrofit.CHANGE_PASSWORD,
                    UserRequest.getsUser().getToken(), password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ChangePasswordRequest>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ChangePasswordRequest cpr) {
                            if (cpr.getStatus() == 1) {
                                SharedPreferences.Editor editor =
                                        activity.getSharedPreferences(activity.getString(
                                                R.string.user_file), Context.MODE_PRIVATE).edit();
                                editor.putString("password", password);
                                editor.apply();
                                UserRequest.getsUser().setToken(cpr.getData().getToken());
                                MainActivity mainActivity = (MainActivity) activity;
                                mainActivity.removeFragment(MainActivity.CHANGE_PASSWORD);
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage("修改密码成功")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", (dialog, which) -> {
                                            dialog.dismiss();
                                            activity.onBackPressed();
                                        });
                                builder.create().show();
                            } else {
                                DialogUtil.showDialog(activity, "修改密码失败，请重试");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            DialogUtil.showDialog(activity, "网络请求错误请稍后再试");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            DialogUtil.showDialog(activity, "当前无网络，请联网后重试试");
        }
    }

}