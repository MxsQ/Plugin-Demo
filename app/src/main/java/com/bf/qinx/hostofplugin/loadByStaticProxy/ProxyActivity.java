package com.bf.qinx.hostofplugin.loadByStaticProxy;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.bf.qinx.hostofplugin.utils.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyActivity extends Activity {

    // 插件 Activity实例
    private Object mPluginActivity;
    // 插件 Activity类名
    private String mPluginClassName;
    // 生命周期方法回调
    private Map<String, Method> mLifecycleMethods = new HashMap<>();

    public static final String PLUGIN_STUB = "plugin_stub";

    // 插件Activity类名
    public static final String PLUGIN_CLASS_NAME = "com.bf.qinx.cosplayplugin.PluginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取插件类名
        mPluginClassName = getIntent().getStringExtra(PLUGIN_STUB);

        // 代理插件
        proxyPluginActivity();
        // 执行插件Activity.onCreate()
        invokeLifecycleMethod("onCreate", new Object[]{savedInstanceState});
    }

    /**
     * 代理插件Activity
     */
    private void proxyPluginActivity() {
        try{
            // 获取插件Activity
            Class<?> clazz = Class.forName(mPluginClassName);
            Constructor<?> con = clazz.getConstructor(new Class[]{});
            mPluginActivity = con.newInstance(new Object[]{});

            // 触发插件的hook点，建立链接
            Method proxyMethod = clazz.getMethod("proxy", new Class[]{Activity.class});
            proxyMethod.setAccessible(true);
            proxyMethod.invoke(mPluginActivity, new Object[]{ this });

            proxyLifecycle(clazz);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 代理插件Activity的生命周期
     * @param activityClass
     */
    private void proxyLifecycle(Class<?> activityClass) {
        try{
            // 拿到插件Activity的生命周期方法，存储，以之后使用
            Method onCreate = activityClass.getDeclaredMethod("onCreate", new Class[]{ Bundle.class });
            onCreate.setAccessible(true);
            mLifecycleMethods.put("onCreate", onCreate);

            String[] lifecycleMethods = new String[]{
                    "onRestart",
                    "onStart",
                    "onResume",
                    "onPause",
                    "onStop",
                    "onDestroy"
            };
            for (String methodName : lifecycleMethods){
                Method method = ReflectUtil.getMethod(mPluginActivity, methodName, new Class[]{});

                if (method != null){
                    method.setAccessible(true);
                    mLifecycleMethods.put(methodName, method);
                }
            }
        } catch (Exception e){
            Log.d("xx", "proxyLifecycle: " + e.getMessage());
        }
    }


    @Override
    public Resources getResources() {
        return (getApplication() != null && getApplication().getResources()!= null)
                ? getApplication().getResources()
                : super.getResources();
    }

    /**
     * 代理插件Activity的各大生命周期
     */
    private void invokeLifecycleMethod(String methodName, Object[] args){
        try{
            Object[] methodArgs = args;
            if (methodArgs == null){
                methodArgs = new Object[]{};
            }
            Method method = mLifecycleMethods.get(methodName);
            if (method != null){
                method.invoke(mPluginActivity, methodArgs);
            }
        } catch (Exception e){
            Log.d("xx", "invokeLifcycleMethod: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        invokeLifecycleMethod("onStart", null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        invokeLifecycleMethod("onRestart", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invokeLifecycleMethod("onResume", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        invokeLifecycleMethod("onPause", null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        invokeLifecycleMethod("onStop", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        invokeLifecycleMethod("onDestroy", null);
    }

}
