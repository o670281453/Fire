package com.zhiyu.fire.model;

/**
 * Created by Administrator on 2017/6/8 0008.
 * 水源数据模型类
 */

public class WaterSource extends ViewModel {

    public int id;//服务端编号
    public double longitude;//经度
    public double latitude;//纬度
    public int status;//水源损毁状态
    public int areaId;//所属划分格子编号

    public WaterSource(){}

    public WaterSource(int id, double longitude, double latitude, int status, int areaId) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.areaId = areaId;
    }
    @Override
    public void mark() {

    }

}