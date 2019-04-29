package com.lin.meet.topic;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

public class MyLocationListener extends BDAbstractLocationListener {
    public String province;
    public String city;
    public String district;
    private locationCallback callback;
    @Override
    public void onReceiveLocation(BDLocation location) {
        if(location!=null){
            province = location.getProvince();
            city = location.getCity();
            district = location.getDistrict();
            callback.stop();
        }
    }

    MyLocationListener(locationCallback callback){
        this.callback = callback;
    }

    public interface locationCallback{
        void stop();
    }
}
