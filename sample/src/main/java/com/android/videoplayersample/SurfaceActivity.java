package com.android.videoplayersample;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.videoplayer.config.VideoIjkBean;
import com.android.videoplayer.media.VPlayPlayer;

import java.util.ArrayList;



/**
 * Created by Berkeley on 10/24/16.
 *
 */
public class SurfaceActivity extends FragmentActivity implements OnClickListener {
    private ArrayList<VideoIjkBean> videoBeens;
    private VPlayPlayer vPlayPlayer;
    private int index;
    private String url="http://119.90.25.48/record2.a8.com/mp4/1477100428026014.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        videoBeens = new ArrayList<>();

        VideoIjkBean videoBean = new VideoIjkBean();
        videoBean.setTitle("遛狗");
        videoBean.setStreamUrl("http://119.90.25.48/record2.a8.com/mp4/1477100428026014.mp4");
        videoBeens.add(videoBean);

        VideoIjkBean videoBean1 = new VideoIjkBean();
        videoBean1.setTitle("wuliao");
        videoBean1.setStreamUrl("http://119.90.25.48/record2.a8.com/mp4/1476696896120409.mp4");
        videoBeens.add(videoBean1);

        VideoIjkBean videoBean2 = new VideoIjkBean();
        videoBean2.setTitle("shabi");
        videoBean2.setStreamUrl("http://119.90.25.48/record2.a8.com/mp4/1476440343435698.mp4");
        videoBeens.add(videoBean2);


        vPlayPlayer = new VPlayPlayer(this);
        vPlayPlayer.onError(new VPlayPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                Toast.makeText(getApplicationContext(), "video play error", Toast.LENGTH_SHORT).show();

            }
        }).onInfo(new VPlayPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {

            }
        }).onComplete(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "video play completed", Toast.LENGTH_SHORT).show();
            }
        });


        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.pause).setOnClickListener(this);
        findViewById(R.id.forward).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.fullscreen).setOnClickListener(this);
        findViewById(R.id.toggleratis).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.source).setOnClickListener(this);
        findViewById(R.id.videolive).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.play) {
            vPlayPlayer.play(url);
            vPlayPlayer.setTitle(url);
            vPlayPlayer.setShowNavIcon(true);


        } else if (i == R.id.start) {
            vPlayPlayer.setUrl(url);
            vPlayPlayer.start();


        } else if (i == R.id.pause) {
            vPlayPlayer.pause();

        } else if (i == R.id.forward) {
            vPlayPlayer.forward(+0.2f);

        } else if (i == R.id.back) {
            vPlayPlayer.forward(-0.2f);

        } else if (i == R.id.toggleratis) {
            vPlayPlayer.toggleAspectRatio();


        } else if (i == R.id.fullscreen) {
            vPlayPlayer.toggleFullScreen();

        } else if (i == R.id.source) {
            url = "http://119.90.25.48/record2.a8.com/mp4/1476440343435698.mp4";

        }
        else if (i==R.id.videolive)
        {
            startActivity(new Intent(this,VideoLiveActivity.class));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayPlayer != null) {
            vPlayPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vPlayPlayer != null) {
            vPlayPlayer.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vPlayPlayer != null) {
            vPlayPlayer.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vPlayPlayer != null) {
            vPlayPlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (vPlayPlayer != null && vPlayPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
