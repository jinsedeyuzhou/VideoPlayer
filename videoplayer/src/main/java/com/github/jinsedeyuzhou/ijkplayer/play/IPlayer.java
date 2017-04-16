package com.github.jinsedeyuzhou.ijkplayer.play;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 4/13/17.
 */

public interface IPlayer {



    void setOnClickOrientationListener(IPlayer.OnClickOrientationListener var1);
    void setOnErrorListener(IPlayer.OnErrorListener var1);
    void setOnInfoListener(IPlayer.OnInfoListener var1);
    void setCompletionListener(IPlayer.CompletionListener var1);
    void setOnNetChangeListener(IPlayer.OnNetChangeListener var1);
    public interface OnClickOrientationListener {
        void landscape();

        void portrait();
    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }


    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }


    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }

    public interface OnNetChangeListener {
        // wifi
        void onWifi();

        // 手机
        void onMobile();

        // 网络断开
        void onDisConnect();

        // 网路不可用
        void onNoAvailable();
    }


}
