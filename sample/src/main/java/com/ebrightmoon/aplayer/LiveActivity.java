package com.ebrightmoon.aplayer;

/**
 * Created by Berkeley on 12/16/16.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.videoplayersample.R;
import com.ebrightmoon.aplayer.bean.LiveBean;
import com.ebrightmoon.aplayer.net.ApiServiceUtils;
import com.github.jinsedeyuzhou.ijkplayer.play.WYXVideoPlayer;

import java.util.List;

/**
 * Created by Berkeley on 10/27/16.
 */
public class LiveActivity extends FragmentActivity {

    private static final String TAG = "LiveActivity";
    private View rootView;
    private WYXVideoPlayer player;
    private String liveUrl = "http://pull5.a8.com/live/1477621558925497.flv";

    private String title = "我是傻逼";
    private PowerManager.WakeLock wakeLock;

    private List<LiveBean> list;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.v(TAG, "list:" + list.toString());
            if (list.size() > 1) {
                liveUrl = list.get(1).getStream_addr();
                title = list.get(1).getName();
            }
            player.play(liveUrl);

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        FrameLayout live = (FrameLayout) findViewById(R.id.fl_live);


        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");


        player = new WYXVideoPlayer(this);

        player.setLive(true);
        if (player.getParent() != null)
            ((ViewGroup) player.getParent()).removeAllViews();
        live.removeAllViews();
        live.addView(player);

        new Thread() {
            @Override
            public void run() {
                //这里多有得罪啦，网上找的直播地址，如有不妥之处，可联系删除
                list = ApiServiceUtils.getLiveList();
                handler.sendEmptyMessage(0);
            }
        }.start();


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        if (wakeLock!=null)
            wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        if (wakeLock!=null)
         wakeLock.acquire();
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

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
