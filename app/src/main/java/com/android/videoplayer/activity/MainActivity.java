package com.android.videoplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.videoplayer.R;
import com.android.videoplayer.media.AndroidMediaController;
import com.android.videoplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private IjkVideoView mVideoView;
    private String videoPath = "http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
    private Button mBtnIJK;
    private Button mBtnSurface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

//        Settings settings = new Settings(getApplicationContext());
        mVideoView = (IjkVideoView) findViewById(R.id.videoView);

        AndroidMediaController mMediaController = new AndroidMediaController(this, false);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView.setMediaController(mMediaController);
//        ijkVideo.setVideoURI(Uri.parse(videoPath));
        mVideoView.setVideoPath(videoPath);
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mVideoView.start();
            }
        });
//        mVideoView.start();
    }

    private void initView() {

        mBtnIJK = (Button) findViewById(R.id.btn_ijk);
        mBtnIJK.setOnClickListener(this);

        mBtnSurface = (Button) findViewById(R.id.btn_surfaceview);
        mBtnSurface.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        System.out.print("执行了没");
        switch (view.getId()) {
            case R.id.btn_ijk:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_surfaceview:
                System.out.print("执行了没");
                intent = new Intent(getApplicationContext(), SurfaceActivity.class);
                startActivity(intent);
                break;
        }
    }
}
