package com.bf.qinx.hostofplugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bf.qinx.hostofplugin.loadByStaticProxy.ProxyActivity;

public class MainActivity extends Activity {

    private static final String PATCH_ACTIVITY = "com.bf.qinx.patchtest.PatchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCosplayActivity();

//                startPatchActivityFormAMS();

//                statPatchActivityFromInstrumentation();
            }
        });
    }



    private void startPatchActivityFormAMS(){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.bf.qinx.patchtest" , PATCH_ACTIVITY);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    private void statPatchActivityFromInstrumentation(){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(MainActivity.this , PATCH_ACTIVITY);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    private void startCosplayActivity(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ProxyActivity.class);
        intent.putExtra(ProxyActivity.PLUGIN_STUB, ProxyActivity.PLUGIN_CLASS_NAME);
        startActivity(intent);
    }
}
