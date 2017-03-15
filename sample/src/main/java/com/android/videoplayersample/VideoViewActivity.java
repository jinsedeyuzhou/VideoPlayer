package com.android.videoplayersample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.ijkplayer.play.VPlayPlayerDanmaku;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Berkeley on 11/9/16.
 */
public class VideoViewActivity extends FragmentActivity {

    private FrameLayout layout_video;
    private VPlayPlayerDanmaku player;
    private int mporit;
    private int initHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        player = (VPlayPlayerDanmaku) findViewById(R.id.layout_video);
        player.play("http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app");
        player = (VPlayPlayerDanmaku) findViewById(R.id.layout_video);
        InputStream stream = null;
        try {
            stream = getAssets().open("custom.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.play("http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app");

//        player.setDanmakuCustomParser(new DanmakuParser(), DanmakuLoader.instance(), DanmakuConverter.instance());
//        player.setDanmakuSource(stream);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (null!=player&&player.handleVolumeKey(keyCode))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (player.onBackPressed())
            return;

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != player)
            player.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != player)
            player.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }
}
