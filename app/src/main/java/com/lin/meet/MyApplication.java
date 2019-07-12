package com.lin.meet;

import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.youngfeng.snake.Snake;

import org.litepal.LitePalApplication;

import cn.bmob.v3.Bmob;

public class MyApplication extends LitePalApplication {
    private HttpProxyCacheServer proxy;
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "c6ba1e71088c7736254c53f1db9cd625");
        SDKInitializer.initialize(this);
        SDKInitializer.setHttpsEnable(true);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        Snake.init(this);
    }

    public static HttpProxyCacheServer getProxy(Context context){
        MyApplication app = (MyApplication)context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
