package com.bf.qinx.hostofplugin.merge;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.bf.qinx.hostofplugin.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by QinX on 2019-06-28.
 * <p>
 * Description :
 */
public class Plugin {
    private static final String TAG = "Plugin";

    // apk 名
//     private static final String PLUGIN_APK_NAME = "plugin.apk";
    private static final String PLUGIN_APK_NAME = "cosplay_plugin.apk";

    private static Resources mPluginResources ;

    /**
     * 载入插件dex
     * @param context
     * @param classLoader
     * @throws Exception
     */
    public static void loadPluginDex(Application context, ClassLoader classLoader) throws Exception{

        // 获取插件apk
        String apkPath =  getPatchApkPath(context);
        File apkFile = new File(apkPath);

        // 创建安装插件的Classloader
        DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getAbsolutePath(), null, null,classLoader);

        // 获取BaseDexClassLoader.dexPathList
        Object pluginDexPatchList = ReflectUtil.getField(dexClassLoader, "pathList");
        // 获取DexFile.dexElements
        Object pluginDexElements =  ReflectUtil.getField(pluginDexPatchList, "dexElements");

        // 通过反射获取宿主 dexPathList
        Object hostDexPatchList = ReflectUtil.getField(classLoader, "pathList");
        // 通过反射获取宿主 dexElements
        Object hostDexElements =  ReflectUtil.getField( hostDexPatchList, "dexElements");

        // 合并dexElements
        Object array = combineArray(hostDexElements, pluginDexElements);
        ReflectUtil.setField( hostDexPatchList, "dexElements", array);

        // 载入资源文件
        loadPluginResources(context);
    }

    private static Object combineArray(Object host, Object plugin) throws Exception{
        Class<?> componentType = host.getClass().getComponentType();
        int hostSize = Array.getLength(host);
        int pluginSize = Array.getLength(plugin);
        int k = hostSize + pluginSize;

        Object bindArray = Array.newInstance(componentType, k);
        System.arraycopy(plugin, 0, bindArray, 0,pluginSize);
        System.arraycopy(host, 0, bindArray, pluginSize, hostSize);

        return bindArray;

    }

    /**
     * 加载资源文件，并进行替换
     * @param application
     * @throws Exception
     */
    public static void loadPluginResources(Application application) throws Exception{
        AssetManager assetManager = AssetManager.class.newInstance();
        // 获取 AssetManager.addAssetPath() 方法
        Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
        // 载入插件的资源
        addAssetPath.invoke(assetManager, getPatchApkPath(application));

        // 创建新的Resource对象
        Resources merResource = new Resources(assetManager,
                application.getBaseContext().getResources().getDisplayMetrics(),
                application.getBaseContext().getResources().getConfiguration());

        // 替换 ContextImpl.mResources
        ReflectUtil.setField(
                application.getBaseContext(),
                "mResources",
                merResource);

        // 获取 ContextImpl 中的 LoadedApk 类型的 mPackageInfo
        Field mPackageInfoField = application.getBaseContext().getClass().getDeclaredField("mPackageInfo");
        mPackageInfoField.setAccessible(true);
        Object packageInfoO = mPackageInfoField.get(application.getBaseContext());
        // 替换 mPackageInfo.mResources
        ReflectUtil.setField(packageInfoO, "mResources", merResource);

        // 替换 ContextImpl 中的 Resources.Theme
        Field themeField = application.getBaseContext().getClass().getDeclaredField("mTheme");
        themeField.setAccessible(true);
        themeField.set(application.getBaseContext(), null);
    }


    private static String getPatchApkPath(Context context){
            return context.getExternalCacheDir().getAbsolutePath() +
                "/" + PLUGIN_APK_NAME;
    }

    public static Resources getPluginResources() {
        return mPluginResources;
    }

}
