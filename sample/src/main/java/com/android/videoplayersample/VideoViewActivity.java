package com.android.videoplayersample;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.ijkplayer.view.VPlayView;

/**
 * Created by Berkeley on 11/9/16.
 */
public class VideoViewActivity extends FragmentActivity {

    private FrameLayout layout_video;
    private VPlayView vPlayPlayer;
    private  int mporit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        layout_video = (FrameLayout) findViewById(R.id.layout_video);

        mporit=layout_video.getLayoutParams().height;
        vPlayPlayer = new VPlayView(this);
        vPlayPlayer.setViewHeight(600);
        vPlayPlayer.start("http://119.90.25.48/record2.a8.com/mp4/1476696896120409.mp4");
        layout_video.addView(vPlayPlayer);


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
        vPlayPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vPlayPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vPlayPlayer.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vPlayPlayer != null) {
            vPlayPlayer.onConfigurationChanged(newConfig);
            //方法二
//            if (newConfig.orientation== Configuration.ORIENTATION_PORTRAIT)
//            {
//                layout_video.getLayoutParams().height=mporit;
//            }
//            else
//            {
//                layout_video.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
//            }
        }
    }
}
