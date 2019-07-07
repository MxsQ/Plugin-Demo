package com.bf.qinx.hostofplugin.loadByHookInstrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;


import com.bf.qinx.hostofplugin.utils.StubHelper;

import java.lang.reflect.Method;

/**
 * Created by QinX on 2019-06-28.
 * <p>
 * Description :
 */
public class InstrumentationHook extends Instrumentation {

    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;
    private Method startActivityMethod;

    public static final String REAL_ACTIVITY_BANE = "activity_name";

    public InstrumentationHook(Instrumentation instrumentation, PackageManager packageManager) {
        mInstrumentation = instrumentation;
        mPackageManager = packageManager;

        initStartActivityMethod();
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        if (!StubHelper.checkActivity(who, intent)){
            intent.putExtra(REAL_ACTIVITY_BANE, intent.getComponent().getClassName());
            // 保存要启动的插件Activity的类明
            intent.setClassName(who, StubHelper.STUB_ACTIVITY);
        }

        try {
            return (ActivityResult) startActivityMethod.invoke(mInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {

        }

        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String startActivityName = intent.getStringExtra(REAL_ACTIVITY_BANE);
        if (!TextUtils.isEmpty(startActivityName)) {
            return super.newActivity(cl, startActivityName, intent);
        }

        return super.newActivity(cl, className, intent);
    }

    private void initStartActivityMethod() {
        try {
            startActivityMethod = Instrumentation.class.getDeclaredMethod(
                    "execStartActivity",
                    Context.class,
                    IBinder.class,
                    IBinder.class,
                    Activity.class,
                    Intent.class,
                    int.class,
                    Bundle.class);

        } catch (NoSuchMethodException e) {
        }
    }

}

