package com.github.jinsedeyuzhou.ijkplayer.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/3/16.
 */
public class VPlayView extends RelativeLayout {

    private Context mContext;
    private Activity activity;
    private View rootView;
    private View controlbar;
    private IjkVideoView mVideoView;
    private CustomMediaContoller customMediaContoller;

    private Handler handler = new Handler();

    private boolean portrait;
    private View toolbar;
//    private OrientationEventListener orientationEventListener;


    public VPlayView(Context context) {
        super(context);

        mContext = context;
        activity = (Activity) context;
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
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_player, this, true);
        /**
         * 播放控制布局
         */
        controlbar = findViewById(R.id.player_controlbar);
        /**
         * IjkVideoView
         */
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        toolbar = findViewById(R.id.app_video_top_box);


        customMediaContoller = new CustomMediaContoller(mContext, rootView);
        mVideoView.setMediaController(customMediaContoller);
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                controlbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                if (customMediaContoller.getScreenOrientation()
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    setLayoutParams(layoutParams);
                }
                if (oncomplete != null) {
                    oncomplete.run();
                }
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                onErrorListener.onError(what, extra);
                return true;
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                onInfoListener.onInfo(what, extra);
                return true;
            }
        });


    }

    /**
     * 方法一，传递参数更改布局宽高
     * @param height
     */
    public void setViewHeight(int height)
    {
        findViewById(R.id.app_video_box).getLayoutParams().height=height;
        customMediaContoller.initHeight=height;
    }

    private void initAction() {
//        orientationEventListener = new OrientationEventListener(mContext) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
//                    //竖屏
//                    if (portrait) {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
//                    if (!portrait) {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                }
//            }
//        };
    }

    public void setTitle(String str) {
        customMediaContoller.setTopTitle(str);
    }

    public void hideNavIcon() {
        customMediaContoller.setShowNavIcon(false);
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    public void start(String path) {
        Uri uri = Uri.parse(path);
        if (customMediaContoller != null)
            customMediaContoller.start();
        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }

    public void start() {
        if (mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }


    public void seekTo(int msec) {
        mVideoView.seekTo(msec);
    }


    public void onConfigurationChanged(final Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        customMediaContoller.doOnConfigurationChanged(portrait);
    }

//
//    public void doOnConfigurationChanged(final boolean portrait) {
//        if (mVideoView != null) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    setFullScreen(!portrait);
//                    if (portrait) {
//                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                        Log.e("handler", "400");
//                        setLayoutParams(layoutParams);
//                        requestLayout();
//                    } else {
//                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
//                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
//                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                        layoutParams.height = heightPixels;
//                        layoutParams.width = widthPixels;
//                        Log.e("handler", "height==" + heightPixels + "\nwidth==" + widthPixels);
//                        setLayoutParams(layoutParams);
//                    }
//
//                }
//            });
//            orientationEventListener.enable();
//        }
//    }



    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }


    public void onDestroy() {
//        orientationEventListener.disable();
        customMediaContoller.onDestroy();
    }

    public void onResume() {
//        mVideoView.start();
        customMediaContoller.onResume();
    }

    public void onPause() {
//        mVideoView.pause();
        customMediaContoller.onPause();
    }


//    private void setFullScreen(boolean fullScreen) {
//        if (mContext != null && mContext instanceof Activity) {
//            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
//            if (fullScreen) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                ((Activity) mContext).getWindow().setAttributes(attrs);
//                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            } else {
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                ((Activity) mContext).getWindow().setAttributes(attrs);
//                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            }
//        }
//
//    }


    public long getPalyPostion() {
        return mVideoView.getCurrentPosition();
    }

    public void release() {
        mVideoView.release(true);
    }

    public int VideoStatus() {
        return mVideoView.getCurrentStatue();
    }


    public interface OnErrorListener {
        void onError(int what, int extra);
    }


    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(int what, int extra) {
        }
    };
    private Runnable oncomplete = new Runnable() {
        @Override
        public void run() {

        }
    };
    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };

}
