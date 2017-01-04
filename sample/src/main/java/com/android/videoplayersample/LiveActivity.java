package com.android.videoplayersample;

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

import com.android.videoplayersample.bean.LiveBean;
import com.android.videoplayersample.net.ApiServiceUtils;
import com.github.jinsedeyuzhou.ijkplayer.play.PlayerManager;
import com.github.jinsedeyuzhou.ijkplayer.play.VPlayPlayer;

import java.util.List;

/**
 * Created by Berkeley on 10/27/16.
 */
public class LiveActivity extends FragmentActivity {

    private static final String TAG ="LiveActivity";
    private View rootView;
    private VPlayPlayer player;
//        private String liveUrl="http://120.55.238.158/api/live/users?lc=3000000000030010&cv=IK3.4.00_Android&cc=TG36008&ua=XiaomiMI5&uid=228032761&sid=202lSEzzUzSfALa0MctmHfzme7pKq5p7bn2Oi1Z6dfOKQQT1cNk&devi=861322035697784&imsi=460027380351530&imei=861322035697784&icc=898600e31614f1753512&conn=WIFI&vv=1.0.3-201610121413.android&aid=52f25a4c60eda7f1&osversion=android_23&mtid=254c6736f3bfe2333172a577198a8ab5&mtxid=d0ee073ec278&proto=4&smid=Dun%2FEZ%2FI5fUAYraBm2dTqK37varDJXR92GZgiy%2F%2B4fqF5eq%2FlxOMe%2BWJ1pNVqu0mKhskb%2FmOCNvCfL%2BbLUB%2B3jrQ&id=1477619133354399&start=0&count=20&s_sg=048cfa811fd52f52e383260dcec654e1&s_sc=100&s_st=1477619553";
    private String liveUrl="http://pull5.a8.com/live/1477621558925497.flv";

    private String title="剪短发心情好";
    private PowerManager.WakeLock wakeLock;

    private List<LiveBean> list;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.v(TAG,"list:"+list.toString());
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
        FrameLayout   live = (FrameLayout) findViewById(R.id.fl_live);


        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        player = PlayerManager.getPlayerManager().initialize(this);

        player.setLive(true);
        if (player.getParent()!=null)
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
