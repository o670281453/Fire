package com.zhiyu.fire.model;

/**
 * Created by Administrator on 2017/6/8 0008.
 * 队站数据模型类
 */

public class FireStation extends ViewModel {
    public int id;//服务端编号
    public double longitude;//经度
    public double latitude;//纬度
    public int areaId;//所属划分格子编号

    public FireStation(){}
    public FireStation(int id, double longitude, double latitude, int areaId) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.areaId = areaId;
    }
    @Override
    public void mark() {

    }

}