package com.github.jinsedeyuzhou.ijkplayer.play;

import android.content.Context;

/**
 *
 * 一个应用中只能有一个重新建立会出现问题，内容释放有问题
 */
public class PlayerManager {
    public static  PlayerManager videoPlayViewManage;
    private EMPlayer videoPlayView;

    private PlayerManager() {

    }

    public static  PlayerManager getPlayerManager() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new PlayerManager();
        }
        return videoPlayViewManage;
    }

    public EMPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new EMPlayer(context);
        }
        return videoPlayView;
    }

}
