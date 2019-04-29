package com.lin.meet;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

import cn.bmob.v3.Bmob;

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "c6ba1e71088c7736254c53f1db9cd625");
        SDKInitializer.initialize(this);
        SDKInitializer.setHttpsEnable(true);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
