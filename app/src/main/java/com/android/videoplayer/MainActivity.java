package com.android.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.videoplayer.widget.media.AndroidMediaController;
import com.android.videoplayer.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

    private IjkVideoView mVideoView;
    private String videoPath="http://119.90.25.32/lxqncdn.miaopai.com/stream/7Wxk53bSqe5F3vbKE-XIKA__.mp4?ssig=9caef54a17d82d6ba49c2b47258d0ead&time_stamp=1477036039000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Settings settings = new Settings(getApplicationContext());
        mVideoView = (IjkVideoView) findViewById(R.id.videoView);

        AndroidMediaController  mMediaController = new AndroidMediaController(this, false);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView.setMediaController(mMediaController);
//        ijkVideo.setVideoURI(Uri.parse(videoPath));
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();
    }
}
