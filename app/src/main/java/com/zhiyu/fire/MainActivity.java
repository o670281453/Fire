package com.zhiyu.fire;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;

public class MainActivity extends Activity implements
        BDLocationListener, View.OnClickListener,
        LocationListener,
        BaiduNaviManager.RoutePlanListener{

    private static final String TAG = "Main";
    private MapView mMapView;
    private BaiduMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    long firstExitTime;
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - firstExitTime < 2000) {// 两次按返回键的时间小于2秒就退出应用
            finish();
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT)
                    .show();
            firstExitTime = curTime;
        }
    }
    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onJumpToNavigator() {
//        Intent intent = new Intent(this, NaviView.class);
//        startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {
        Toast.makeText(this, "路径规划失败", Toast.LENGTH_SHORT).show();
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
