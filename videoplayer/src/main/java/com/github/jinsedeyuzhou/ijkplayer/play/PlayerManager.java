package com.github.jinsedeyuzhou.ijkplayer.play;

import android.content.Context;

/**
 * Created by Berkeley on 12/15/16.
 */
public class PlayerManager {
    public static PlayerManager videoPlayViewManage;
    private VPlayPlayer videoPlayView;

    private PlayerManager() {

    }

    public static PlayerManager getPlayerManager() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new PlayerManager();
        }
        return videoPlayViewManage;
    }

    public VPlayPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new VPlayPlayer(context);
        }
        return videoPlayView;
    }

}
