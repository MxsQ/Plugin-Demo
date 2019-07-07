package com.bf.qinx.hostofplugin.loadByHookAms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.bf.qinx.hostofplugin.utils.StubHelper;
import com.bf.qinx.hostofplugin.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by QinX on 2019-06-29.
 * <p>
 * Description :
 */
public class AMSHookHelper {

    public static void hook(Context context) throws Exception{
        replaceActivity(context);
        restoreActivity();
    }

    private static void replaceActivity(final Context context) throws Exception{

        // 通过ActivityManager获取AMS实例, , 26以上有效
        Class amClass = Class.forName("android.app.ActivityManager");
        Method getServiceMethod = amClass.getDeclaredMethod("getService");
        final Object iActivityManagerObje = getServiceMethod.invoke(null);
        Field iActivityManagerSingletonField = amClass.getDeclaredField("IActivityManagerSingleton");
        Object iActivityManagerSingletonObj = ReflectUtil.getStaticField(amClass, "IActivityManagerSingleton");

        // 获取 mInstance
        Class singleTonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        iActivityManagerSingletonField.setAccessible(true);

        // ams 实例
        final Object amsObj = ReflectUtil.getField(iActivityManagerSingletonObj, "mInstance");

        // 创建IActivityManager的代理
        Class<?> iamClass = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iamClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // hook startActivity
                        if (!"startActivity".equals(method.getName())){
                            return method.invoke(amsObj, args);
                        }

                        // 找到intent对象
                        int intentIndex= 0;
                        for (int i=0; i<args.length; i++){
                            if (args[i] instanceof Intent){
                                intentIndex = i;
                                break;
                            }
                        }

                        Intent realIntent = (Intent) args[intentIndex];

                        // 检查启动的Activity是否在宿主Manifest声明
                        if (StubHelper.checkActivity(context, realIntent)){
                            return method.invoke(amsObj, args);
                        }

                        // 使用占坑的Activity绕过AMS，替换Intent
                        Intent stubIntent = new Intent();
                        // SELF_PAK为插件APK包名
                        stubIntent.setComponent(new ComponentName(StubHelper.SELF_PAK, StubHelper.STUB_ACTIVITY));
                        stubIntent.putExtra(StubHelper.REAL_INTENT, realIntent);
                        args[intentIndex] = stubIntent;

                        return method.invoke(amsObj, args);
                    }
                }
        );

        // 代理ams
        mInstanceField.setAccessible(true);
        mInstanceField.set(iActivityManagerSingletonObj, proxy);
    }


    private static void restoreActivity() throws Exception{
        // 获取 ActivityThread
        Class atClass = Class.forName("android.app.ActivityThread");
        Object curAtObj = ReflectUtil.getStaticField(atClass, "sCurrentActivityThread");

        // 获取 ActivityThread 中的 handle , 即 mH
        final Handler mHObj = (Handler) ReflectUtil.getField(curAtObj, "mH");

        // 设置 Handler 的 mCallBack
        Class handlerClass = Handler.class;
        ReflectUtil.setField(mHObj, "mCallback", new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try{
                    int LAUNCH_ACTIVITY = 0;
                    Class hClass = Class.forName("android.app.ActivityThread$H");
                    LAUNCH_ACTIVITY = (int) ReflectUtil.getStaticField(hClass, "LAUNCH_ACTIVITY");

                    if (msg.what == LAUNCH_ACTIVITY){
                        // 恢复原来的intent
                        Intent intent = (Intent) ReflectUtil.getField(msg.obj, "intent");
                        Intent realIntent = intent.getParcelableExtra(StubHelper.REAL_INTENT);
                        if (realIntent != null){
                            intent.setComponent(realIntent.getComponent());
                        }
                    }
                } catch (Exception e){

                }
                mHObj.handleMessage(msg);
                return true;
            }
        });
    }
}
