package com.github.jinsedeyuzhou.ijkplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/3/16.
 */
public class VPlayView extends RelativeLayout  {

    private Context mContext;
    private View rootView;
    private View controlbar;
    private IjkVideoView mVideoView;


    public VPlayView(Context context) {
        super(context);

        mContext = context;
        initData();
        initView();
        initAction();
    }

    public VPlayView(Context context, AttributeSet attrs, Context mContext) {
        super(context, attrs);
        this.mContext = mContext;
    }

    public VPlayView(Context context, AttributeSet attrs, int defStyleAttr, Context mContext) {
        super(context, attrs, defStyleAttr);
        this.mContext = mContext;
    }

    private void initData() {

    }

    private void initView() {
        /**
         * 主要布局
         */
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_player,this,true);
        controlbar = findViewById(R.id.player_controlbar);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);

        CustomMediaContoller customMediaContoller=new CustomMediaContoller(mContext,rootView);
        mVideoView.setMediaController(customMediaContoller);
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {

            }
        });




    }

    private void initAction() {

    }


}
