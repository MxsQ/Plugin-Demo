package com.bf.qinx.hostofplugin.loadByHookInstrumentation;

import android.app.Instrumentation;
import android.content.Context;

import com.bf.qinx.hostofplugin.utils.ReflectUtil;

/**
 * Created by QinX on 2019-06-29.
 * <p>
 * Description :
 */
public class InstrumentationHookHelper {

    public static void hookInstrumentation(Context context) throws Exception{
        // 获取ContextImpl.mMainThread类对象
        Object activityThread = ReflectUtil.getField( context, "mMainThread");
        // 获取ActivityThread.mInstrumentation
        Object mInstrumentation = ReflectUtil.getField( activityThread, "mInstrumentation");

        ReflectUtil.setField(
                activityThread,
                "mInstrumentation",
                new InstrumentationHook((Instrumentation) mInstrumentation, context.getPackageManager()));
    }
}
