package com.xuchengpu.myproject.myproject.app;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 许成谱 on 2017/1/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志...
    }
}
