package com.android.videoplayersample.danmaku;


import com.github.jinsedeyuzhou.ijkplayer.danmaku.BaseDanmakuData;

/**
 * Created by long on 2016/12/22.
 * 自定义弹幕数据
 */
public class DanmakuData extends BaseDanmakuData {

    // 用户名
    public String userName;
    // 用户等级
    public int userLevel;

    @Override
    public String toString() {
        return "DanmakuData{" +
                "userName='" + userName + '\'' +
                ", userLevel=" + userLevel +
                "} " + super.toString();
    }
}
