package com.github.jinsedeyuzhou.ijkplayer;

import android.content.Context;

/**
 * Created by Berkeley on 2/10/17.
 * 设置全局变量或者获取应用Context 嵌入的app中App中配置
 */

public class WYXApplication {

    private static Context mContext;
    private static WYXApplication mInstance;

    public static void initApp(Context context) {
        mContext = context;
        mInstance = new WYXApplication();


    }
    WYXApplication() {
        mInstance = this;
    }
    public static Context getAppContext() {
        return mContext;
    }

    public static synchronized WYXApplication getInstance() {
        if (null == mInstance) {
            mInstance = new WYXApplication();
        }
        return mInstance;
    }
}
