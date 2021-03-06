package com.ebrightmoon.aplayer;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Berkeley on 2/13/17.
 */

public class VideoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
