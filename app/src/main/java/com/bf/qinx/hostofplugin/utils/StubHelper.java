package com.bf.qinx.hostofplugin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by QinX on 2019-06-29.
 * <p>
 * Description :
 */
public class StubHelper {

    public static final String REAL_INTENT = "real_intent";

    public static final String SELF_PAK = "com.bf.qinx.hostofplugin";

    public static final String STUB_ACTIVITY = "com.bf.qinx.hostofplugin.StubActivity";

    /**
     * 检查Activity有没有在宿主中声明
     * @param intent
     * @return
     */
    public static boolean checkActivity(Context context, Intent intent){
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityInfos = pkgInfo.activities;

            String activityName = intent.getComponent().getClassName();

            for (ActivityInfo info : activityInfos){
                if (activityName.equalsIgnoreCase(info.name)){
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
