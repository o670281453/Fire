package com.zhiyu.fire.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.zhiyu.fire.MyApplication;
import com.zhiyu.fire.R;
import com.zhiyu.fire.controller.FragmentController.LoginFragmentController;
import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.controller.netinterface.LoginService;
import com.zhiyu.fire.model.UserRequest;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        BDLocationListener, View.OnClickListener,
        LocationListener, BaiduNaviManager.RoutePlanListener {

    private MainActivity mActivity;

    private static final String TAG = "Main";
    private MapView mMapView;
    private BaiduMap mMap;
    private FloatingActionButton mFab;

    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        SharedPreferences preferences = mActivity.getSharedPreferences(mActivity
                .getString(R.string.user_file), Context.MODE_PRIVATE);
        username = preferences.getString("username", "");
        if (username.equals("")) {
            launchLoginFragment();
        } else {
            password = preferences.getString("password", "");
            autoLogin();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setClickable(true);
        mFab = (FloatingActionButton) view.findViewById(R.id.fragment_main_add_fab);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
        mFab.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void autoLogin() {
        if (MyRetrofit.checkNetwork(mActivity.getApplicationContext())) {
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
                                SharedPreferences.Editor editor = mActivity.getSharedPreferences(
                                        mActivity.getString(R.string.user_file),
                                        Context.MODE_PRIVATE).edit();
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.apply();
                            } else {
                                SharedPreferences.Editor editor = mActivity.getSharedPreferences(
                                        mActivity.getString(R.string.user_file),
                                        Context.MODE_PRIVATE).edit();
                                editor.clear();
                                editor.apply();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                                        .setMessage("身份验证失败")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", (dialog, which) -> launchLoginFragment());
                                builder.create().show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e("登录", e.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                                    .setMessage("身份验证失败，您有以下选项")
                                    .setCancelable(false)
                                    .setPositiveButton("重试", (dialog, which) -> autoLogin())
                                    .setPositiveButton("手动登录", (dialog, which) -> launchLoginFragment())
                                    .setPositiveButton("进入离线模式", (dialog, which) ->
                                            MyApplication.setMode(MyApplication.OFFLINE));
                            builder.create().show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setMessage("当前无网络，是否进入离线模式？")
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> MyApplication.setMode(MyApplication.OFFLINE))
                        .setPositiveButton("取消", ((dialog, which) -> mActivity.finish()));
            builder.create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_main_add_fab:
                final String[] items = {"添加新水源", "添加新队站"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                        .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            mActivity.setFragment(new AddWaterSourceFragment());
                            break;
                        case 1:
                            mActivity.setFragment(new AddFireStationFragment());
                            break;
                        default:
                            break;
                    }
                }).setTitle("添加新位置");
                builder.create().show();
                break;
            default:
                break;
        }
    }

    private void launchLoginFragment() {
        if (mActivity.isFragmentExist(MainActivity.LOGIN)) {
            mActivity.setFragment(mActivity.getFragment(MainActivity.LOGIN));
        } else {
            LoginFragment loginFragment = new LoginFragment();
            LoginFragmentController controller = new LoginFragmentController();
            loginFragment.setFragmentLifeCycle(controller);
            loginFragment.setILoginInterface(controller);
            mActivity.putFragment(MainActivity.LOGIN, loginFragment);
            mActivity.setFragment(loginFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setFragmentStatus(MainActivity.MAIN);
        mActivity.invalidateOptionsMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onJumpToNavigator() {
//        Intent intent = new Intent(this, NaviView.class);
//        startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {
        Snackbar.make(mFab, "路径规划失败", Snackbar.LENGTH_SHORT).show();
    }

    private void initSetting(){
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);//设置日夜模式
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);// 设置是否双屏显示
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Novice);// 设置导航播报模式
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);//设置省电模式
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_OFF);// 是否开启路况
    }

    BaiduNaviManager.NaviInitListener mNaviInitListener = new BaiduNaviManager.NaviInitListener() {
        @Override
        public void onAuthResult(int status, String msg) {
            if (0 == status) {
                Log.d(TAG, "key校验成功!");
            } else {
                Log.d(TAG, "key校验失败, " + msg);
            }
        }

        public void initSuccess() {
            Log.d(TAG, "百度导航引擎初始化成功");
            initSetting();
        }

        public void initStart() {
            Log.d(TAG, "百度导航引擎初始化开始");
        }

        public void initFailed() {
            Log.e(TAG, "百度导航引擎初始化失败");
        }
    };

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}