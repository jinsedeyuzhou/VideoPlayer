package com.android.videoplayersample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.android.videoplayer.media.VPlayPlayer;

/**
 * Created by Berkeley on 10/27/16.
 */
public class VideoLiveActivity extends FragmentActivity {

    private View rootView;
    private VPlayPlayer player;
    private String liveUrl="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.giraffe_player, null);
        setContentView(rootView);
        player = new VPlayPlayer(this);
        player.onInfo(new VPlayPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {

            }
        }).onError(new VPlayPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {

            }
        }).onComplete(new Runnable() {
            @Override
            public void run() {

            }
        });


        player.setTitle("");




    }



    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
