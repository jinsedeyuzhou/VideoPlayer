package com.android.videoplayersample;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.ijkplayer.play.PlayerManager;
import com.github.jinsedeyuzhou.ijkplayer.play.VPlayPlayer;

/**
 * Created by Berkeley on 11/9/16.
 */
public class VideoViewActivity extends FragmentActivity {

    private FrameLayout layout_video;
    private VPlayPlayer player;
    private int mporit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        layout_video = (FrameLayout) findViewById(R.id.layout_video);
        player = PlayerManager.getPlayerManager().initialize(this);
        if (player.getParent() != null)
            ((ViewGroup) player.getParent()).removeAllViews();
        layout_video.addView(player);
//        vPlayPlayer.setViewHeight(600);
//        player.start("http://119.90.25.48/record2.a8.com/mp4/1476696896120409.mp4");
        player.start("http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app");
//        mporit=layout_video.getLayoutParams().height;


    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
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
        if (null != player)
            player.onDestroy();
        player=null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
