package com.android.videoplayersample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.ijkplayer.play.VPlayPlayer;

/**
 * Created by Berkeley on 11/9/16.
 */
public class VideoViewActivity extends FragmentActivity {

    private FrameLayout layout_video;
    private VPlayPlayer player;
    private int mporit;
    private int initHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        player = (VPlayPlayer) findViewById(R.id.layout_video);
//        initHeight=layout_video.getLayoutParams().height;
//        if (player == null)
//            player =new VPlayPlayer(this);
//        if (player.getParent() != null)
//            ((ViewGroup) player.getParent()).removeAllViews();
        player.play("http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app");
//        layout_video.addView(player);
//        vPlayPlayer.setViewHeight(600);
//        player.start("http://119.90.25.48/record2.a8.com/mp4/1476696896120409.mp4");

//        mporit=layout_video.getLayoutParams().height;


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
//        if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
//        {
//            ViewGroup.LayoutParams params = layout_video.getLayoutParams();
//            int widthPixels = getResources().getDisplayMetrics().widthPixels;
//            params.height = initHeight;
//            layout_video.setLayoutParams(params);
//        }else
//        {
//            ViewGroup.LayoutParams params = layout_video.getLayoutParams();
//            int heightPixels =getResources().getDisplayMetrics().heightPixels;
//            int widthPixels = getResources().getDisplayMetrics().widthPixels;
//            params.height=widthPixels;
////            params.width=widthPixels;
//            layout_video.setLayoutParams(params);
//        }
    }
}
