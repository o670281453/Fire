package com.zhiyu.fire.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.zhiyu.fire.MyApplication;
import com.zhiyu.fire.R;
import com.zhiyu.fire.controller.FragmentController.LoginFragmentController;
import com.zhiyu.fire.controller.MyRetrofit;
import com.zhiyu.fire.controller.netinterface.LoginService;
import com.zhiyu.fire.model.FireStation;
import com.zhiyu.fire.model.UserRequest;
import com.zhiyu.fire.model.WaterSource;
import com.zhiyu.fire.util.DBDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        BDLocationListener, View.OnClickListener,BaiduMap.OnMarkerClickListener,
        LocationListener, BaiduNaviManager.RoutePlanListener {

    private MainActivity mActivity;

    private static final String TAG = "Main";
    private MapView mMapView;
    private BaiduMap mMap;
    private FloatingActionButton mFab;

    private String username;
    private String password;

    private LocationClient mLocClient;
    //是否自动跟随
    private boolean autoBDCenter = true;
    private BDLocation mLastLocation;
    private boolean isFirstLoc = true;
    private String mStartAddr, mStopAddr;
    private Double mDstLng, mDstLat, mStartLng, mStartLat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setClickable(true);
        mFab = (FloatingActionButton) view.findViewById(R.id.fragment_main_add_fab);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
        initMap();
        mFab.setOnClickListener(this);
        view.findViewById(R.id.locate).setOnClickListener(this);
        DBDao dbDao = new DBDao(mActivity);
        List<WaterSource> waterSourceList = null;
        List<FireStation> fireStationList = null;
        List<Integer> areaList = getAreaList(null);
        try {
            waterSourceList = dbDao.getSource(areaList);
            fireStationList = dbDao.getStation(areaList);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(waterSourceList !=null) {
            initSourceOverlay(waterSourceList);
        }
        if(fireStationList !=null) {
            initStationOverlay(fireStationList);
        }
        mMap.setOnMarkerClickListener(this);
        initMapClickEvent();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initMap(){
        initDefault();
        // 开启定位图层
        mMap.setMyLocationEnabled(true);
        mMap.setMaxAndMinZoomLevel(19, 3);
        // 定位初始化
        mLocClient = new LocationClient(mActivity);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setNeedDeviceDirect(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            //当用户触摸地图时回调函数
            // 地图被用户手动滑动后,停止自动跟随模式
            @Override
            public void onTouch(MotionEvent event) {
                autoBDCenter = false;
            }
        });
        BNOuterLogUtil.setLogSwitcher(true);
        File dir = Environment.getExternalStorageDirectory();
        String name = getResources().getString(R.string.BaiduNaviDir);
        File f = new File(dir, name);
        if(f.exists() || f.mkdir()) {
            BaiduNaviManager.getInstance().init(mActivity,
                    dir.toString(), name,
                    mNaviInitListener, null, null, null);
        }
    }

    private void initDefault(){
        //设置地图默认中心点
        LatLng center = new LatLng(30.542809291954995,114.3272189510811);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(center)
                .zoom(13.5f)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mMap.setMapStatus(mMapStatusUpdate);
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
            case R.id.locate:
                autoBDCenter = true;
                if(mLastLocation == null)
                    return;
                moveToLocation(mLastLocation, -1);
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
        SharedPreferences preferences = mActivity.getSharedPreferences(mActivity
                .getString(R.string.user_file), Context.MODE_PRIVATE);
        username = preferences.getString("username", "");
        if (username.equals("")) {
            launchLoginFragment();
        } else {
            password = preferences.getString("password", "");
            autoLogin();
        }
        mMapView.onResume();
        mLocClient.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mLocClient.stop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        super.onDestroy();
        mMapView.onDestroy();
        ((MyApplication)mActivity.getApplication()).mLocation = null;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null || mMapView == null) {
            return;
        }
        Log.d("test",location.getLatitude() +"," + location.getLongitude());
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mMap.setMyLocationData(locData);
        mLastLocation = location;
        ((MyApplication)mActivity.getApplication()).mLocation = location;
        if (isFirstLoc) {
            isFirstLoc = false;
            moveToLocation(location, 18f);
        }
        if(autoBDCenter){
            moveToLocation(location, -1);
        }
    }

    private void moveToLocation(BDLocation location, float zoom) {
        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll);
        if(zoom > 0)
            builder.zoom(zoom);
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onJumpToNavigator() {
        Intent intent = new Intent(mActivity, BaiduGuideActivity.class);
        startActivity(intent);
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

    private void routeTo(Marker marker) {

        mStartLng = mLastLocation.getLongitude();
        mStartLat = mLastLocation.getLatitude();
        BNRoutePlanNode sNode = new BNRoutePlanNode(mStartLng,
                mStartLat, "当前位置", null,
                BNRoutePlanNode.CoordinateType.BD09LL);
        mStartAddr = mLastLocation.getStreet() + mLastLocation.getStreetNumber();

        mStopAddr = marker.getTitle();
        mDstLat = marker.getPosition().latitude;
        mDstLng = marker.getPosition().longitude;
        BNRoutePlanNode eNode = new BNRoutePlanNode(mDstLng, mDstLat,
                marker.getTitle(), null,
                BNRoutePlanNode.CoordinateType.BD09LL);
        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(mActivity, list, 1, true, this);
    }

    public void initSourceOverlay(List<WaterSource> list){
        mMap.clear();
        final BitmapDescriptor hydrant = BitmapDescriptorFactory.fromResource(R.drawable.hydrant);
        final BitmapDescriptor badhydrant = BitmapDescriptorFactory.fromResource(R.drawable.badhydrant);
        LatLng latLng = null;
        Marker marker = null;
        for(int i= 0; i<list.size(); i++){
//            latLng = new LatLng(30.542809291954995,114.3272189510811);
            latLng = new LatLng(list.get(i).latitude,list.get(i).longitude);
            OverlayOptions options;
            if(list.get(i).status == 1){
                options = new MarkerOptions()//
                        .position(latLng)// 设置marker的位置
                        .icon(hydrant)// 设置marker的图标
                        .zIndex(9)// 設置marker的所在層級
                        .draggable(false);// 设置手势拖拽
            }else {
                options = new MarkerOptions()//
                        .position(latLng)// 设置marker的位置
                        .icon(badhydrant)// 设置marker的图标
                        .zIndex(9)// 設置marker的所在層級
                        .draggable(false);// 设置手势拖拽
            }


            marker = (Marker) (mMap.addOverlay(options));
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);//类型为1表示水源
//                    bundle.putString("title", stationList.get(i).getTitle());
//                    bundle.putString("address", stationList.get(i).getAddress());
//                    bundle.putDouble("latitude", stationList.get(i).getLatitude());
//                    bundle.putDouble("longitude", stationList.get(i).getLongitude());
//                    bundle.putString("pid", stationList.get(i).getPid());
                    marker.setExtraInfo(bundle);
                }
    }
    public void initStationOverlay(List<FireStation> list){
        final BitmapDescriptor station = BitmapDescriptorFactory.fromResource(R.drawable.station);
        LatLng latLng = null;
        Marker marker = null;
        for(int i= 0; i<list.size(); i++){
            latLng = new LatLng(list.get(i).latitude,list.get(i).longitude);
            OverlayOptions  options = new MarkerOptions()//
                        .position(latLng)// 设置marker的位置
                        .icon(station)// 设置marker的图标
                        .zIndex(9)// 設置marker的所在層級
                        .draggable(false);// 设置手势拖拽
            marker = (Marker) (mMap.addOverlay(options));
            Bundle bundle = new Bundle();
            bundle.putInt("type",2);//类型为1表示队站
            marker.setExtraInfo(bundle);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getExtraInfo().get("type").toString().equals("1")) {//水源
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View view = inflater.inflate(R.layout.callout, null);
            TextView info = (TextView) view.findViewById(R.id.info);
            TextView record = (TextView) view.findViewById(R.id.record);
            TextView guide = (TextView) view.findViewById(R.id.guide);
            guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    routeTo(marker);
                }
            });
            InfoWindow mInfoWindow;
            view.setPadding(30, 20, 30, 50);
//                    location.setBackgroundResource(R.drawable.popup);
            view.setBackgroundResource(R.drawable.dialog);

            // 将marker所在的经纬度的信息转化成屏幕上的坐标
            final LatLng ll = marker.getPosition();
            Point p = mMap.getProjection().toScreenLocation(ll);
            p.y -= 47;
            LatLng llInfo = mMap.getProjection().fromScreenLocation(p);
            mInfoWindow = new InfoWindow(view, llInfo, 1);
            // 显示InfoWindow
            mMap.showInfoWindow(mInfoWindow);
        }
        if (marker.getExtraInfo().get("type").toString().equals("2")) {//队站
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View view = inflater.inflate(R.layout.callout, null);
            TextView info = (TextView) view.findViewById(R.id.info);
            TextView record = (TextView) view.findViewById(R.id.record);
            record.setVisibility(View.GONE);
            TextView guide = (TextView) view.findViewById(R.id.guide);
            guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    routeTo(marker);
                }
            });
            InfoWindow mInfoWindow;
            view.setPadding(30, 20, 30, 50);
//                    location.setBackgroundResource(R.drawable.popup);
            view.setBackgroundResource(R.drawable.dialog);

            // 将marker所在的经纬度的信息转化成屏幕上的坐标
            final LatLng ll = marker.getPosition();
            Point p = mMap.getProjection().toScreenLocation(ll);
            p.y -= 47;
            LatLng llInfo = mMap.getProjection().fromScreenLocation(p);
            mInfoWindow = new InfoWindow(view, llInfo, 1);
            // 显示InfoWindow
            mMap.showInfoWindow(mInfoWindow);
        }
        return true;
    }

    /**
     * 根据当前位置计算要显示的区域编号集合
     * @param location
     * @return
     */
    private List<Integer> getAreaList(BDLocation location){
        List<Integer> areaList = new ArrayList<>();//要显示的区域的id编号集合，这里只是随便添加的
        areaList.add(1);
        areaList.add(2);
        areaList.add(3);
        return areaList;
    }

    /**
     * 百度地图点击事件
     */
    private void initMapClickEvent(){
        mMap.setOnMapClickListener(new BaiduMap.OnMapClickListener(){

            @Override
            public boolean onMapPoiClick(MapPoi arg0){
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0){
                mMap.hideInfoWindow();
            }
        });
    }
}