package com.github.jinsedeyuzhou.ijkplayer;

import android.content.Context;

/**
 * Created by Berkeley on 2/10/17.
 * 设置全局变量或者获取应用Context 嵌入的app中App中配置
 */

public class VPlayerApplication {

    private static Context mContext;
    private static VPlayerApplication mInstance;

    public static void initApp(Context context) {
        mContext = context;
        mInstance = new VPlayerApplication();


    }
    VPlayerApplication() {
        mInstance = this;
    }
    public static Context getAppContext() {
        return mContext;
    }

    public static synchronized VPlayerApplication getInstance() {
        if (null == mInstance) {
            mInstance = new VPlayerApplication();
        }
        return mInstance;
    }
}
