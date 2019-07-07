package com.bf.qinx.hostofplugin.merge;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.bf.qinx.hostofplugin.loadByHookAms.AMSHookHelper;
import com.bf.qinx.hostofplugin.loadByHookInstrumentation.InstrumentationHookHelper;

/**
 * Created by QinX on 2019-06-28.
 * <p>
 * Description :
 */
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {

            /**
             * 方式一：自行管理Instrumentation
             */
//            InstrumentationHookHelper.hookInstrumentation(base);
            /**
             * 方式二：代理AMS
             */
//            AMSHookHelper.hook(base);
            /**
             * 方式三：静态代理
             */
            // 没错，方式三这里什么都不用写 //


            Plugin.loadPluginDex(this, getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        return Plugin.getPluginResources() == null
                ? super.getResources()
                : Plugin.getPluginResources();
    }
}
