package com.lin.meet.main.fragment.Find;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lin.meet.MyApplication;
import com.lin.meet.R;

public class Find extends Fragment {
    private static final String TAG = "定位测试 ";
    private BaiduMapOptions options;
    private View mView = null;
    private MapView mapView = null;
    private BaiduMap map;
    private LocationClient locationClient;
    private double latitude = 0,longitude = 0;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find, container, false);
        initView(mView);
        return mView;
    }
    
    public class MylocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mapView == null){
                return;
            }
            Log.d(TAG, "onReceiveLocation: "+location.getLatitude()+"  "+location.getLongitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            map.setMyLocationData(locData);
            TestFlag(latitude-50,longitude-50);
            if(Math.abs(location.getLatitude()-latitude)>=50||Math.abs(location.getLongitude()-longitude)>=50){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng lng = new LatLng(29,114);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lng);
                map.animateMapStatus(update);
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View view){
        mapView = (MapView)view.findViewById(R.id.baiduView);
        map = mapView.getMap();
        locationClient = new LocationClient(getContext());
        map.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MylocationListener());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18f);
        map.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        locationClient.start();
        TestFlag(1,1);
    }

    @Override
    public void onDestroy() {
        locationClient.stop();
        map.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void TestFlag(double d1,double d2){
        Log.d(TAG, "TestFlag: 标记");
        LatLng point = new LatLng(29,114);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromView(LayoutInflater.from(getContext()).inflate(R.layout.temp_test_view,null));
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .perspective(true)
                .icon(bitmap);
        map.addOverlay(option);
    }
}
