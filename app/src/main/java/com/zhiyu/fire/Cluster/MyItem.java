package com.zhiyu.fire.Cluster;

import android.os.Bundle;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.zhiyu.fire.R;
import com.zhiyu.fire.model.ViewModel;

/**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private ViewModel mViewModel;
        private LatLng mPosition;
        private Bundle mBundle;

        public MyItem(ViewModel viewModel) {
//            mPosition = latLng;
            mViewModel = viewModel;
            mBundle = null;
        }
        public MyItem(ViewModel viewModel, Bundle bundle) {
//            mPosition = latLng;
            this.mViewModel = viewModel;
            this.mPosition = new LatLng(mViewModel.latitude, mViewModel.longitude);
            this.mBundle = bundle;
        }
        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            //自定义view
//            View view;
//            view = LayoutInflater.from(context).inflate(R.layout.item_blue, null);
//            TextView textView = (TextView) view.findViewById(R.id.icon_title);
//            textView.setText(title + "");
//            return BitmapDescriptorFactory.fromView(view);
            int iconId = R.drawable.station;
            if(mBundle!=null){
                if(1 == (mBundle.getInt("status"))) {
                    iconId = R.drawable.hydrant;
                } else if(0 == (mBundle.getInt("status"))) {//if("002".contentEquals(mBundle.getString("status"))
                    iconId = R.drawable.badhydrant;
                }
            }

            return BitmapDescriptorFactory
                    .fromResource(iconId);
        }

        public Bundle getBundle(){
            return mBundle;
        }

    }