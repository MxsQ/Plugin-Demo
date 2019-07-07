package com.bf.qinx.hostofplugin.loadByStaticProxy;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by QinX on 2019-06-30.
 * <p>
 * Description :
 */
public abstract class BasePluginActivity {

    // 宿主
    protected Activity mHost;

    public void proxy(Activity host){
        mHost = host;
    }

    public void setContentView(int layoutId){
        mHost.setContentView(layoutId);
    }

    protected abstract void onCreate(Bundle savedInstanceState);

    protected void onStart(){};

    protected void onRestart(){};

    protected void onResume(){};

    protected void onPause(){};

    protected void onStop(){};

    protected void onDestroy(){};
}
