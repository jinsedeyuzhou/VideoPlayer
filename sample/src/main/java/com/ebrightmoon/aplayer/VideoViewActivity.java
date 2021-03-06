package com.ebrightmoon.aplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.ijkplayer.play.EMPlayerStandard;


/**
 * Created by Berkeley on 11/9/16.
 */
public class VideoViewActivity extends FragmentActivity {

    private FrameLayout layout_video;
    private EMPlayerStandard player;
    private int mporit;
    private int initHeight;
    private EditText mInputDamaku;
    private Button mSendDanmaku;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        player = (EMPlayerStandard) findViewById(R.id.layout_video);
//        player.setFullScreenOnly(true);
//        player.play("http://192.168.0.57:8089/bdpf/suggestpicture/download?fid=6599");
        player.play("http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4");
//        mInputDamaku = (EditText) findViewById(R.id.et_input_danmaku);
//        mSendDanmaku = (Button) findViewById(R.id.btn_send_danmaku);
//        mSendDanmaku.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(mInputDamaku.getText().toString()))
//                    player.addDanmaku(mInputDamaku.getText().toString(),false);
//                else
//                    Toast.makeText(getApplicationContext(),"请输入内容",Toast.LENGTH_LONG).show();
//            }
//        });
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
    }
}
