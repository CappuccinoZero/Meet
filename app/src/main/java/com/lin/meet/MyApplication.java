package com.lin.meet;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
