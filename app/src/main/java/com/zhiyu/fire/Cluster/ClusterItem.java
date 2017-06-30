package com.zhiyu.fire.Cluster;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by xiaow on 2017/6/23 11:57.
 */

public interface ClusterItem {
    LatLng getPosition();

    BitmapDescriptor getBitmapDescriptor();
}
