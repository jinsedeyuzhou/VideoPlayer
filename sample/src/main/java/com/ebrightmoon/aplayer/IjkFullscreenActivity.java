package com.ebrightmoon.aplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.github.jinsedeyuzhou.ijkplayer.play.WYXVideoPlayer;

public class IjkFullscreenActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4";
    private static final String IMAGE_URL = "http://vimg3.ws.126.net/image/snapshot/2016/11/C/T/VC628QHCT.jpg";
    private  WYXVideoPlayer mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPlayerView = new WYXVideoPlayer(this);
        setContentView(R.layout.activity_fullscreen);
         mPlayerView = (WYXVideoPlayer) findViewById(R.id.fullscreen);
        mPlayerView.play(VIDEO_URL);
        mPlayerView.setFullScreenOnly(true);
//        mPlayerView.init()
//                .alwaysFullScreen()
//                .enableOrientation()
//                .setVideoPath(VIDEO_URL)
//                .enableDanmaku()
//                .setDanmakuSource(getResources().openRawResource(R.raw.bili))
//                .setTitle("这是个跑马灯TextView，标题要足够长才会跑。-(゜ -゜)つロ 乾杯~")
//                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
