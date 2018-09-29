package com.item.text;

import android.app.Application;

import com.lzy.okgo.OkGo;

import org.xutils.x;

/**
 * Created by wuzongjie on 2018/9/27
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // 这个OkGo需要的
        OkGo.getInstance().init(this);
        // 这个是xutils需要的
        x.Ext.init(this);
        // 是否打开log方便调试用的
        x.Ext.setDebug(true);
    }
}
