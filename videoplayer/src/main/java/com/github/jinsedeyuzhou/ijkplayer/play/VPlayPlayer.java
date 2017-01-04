package com.github.jinsedeyuzhou.ijkplayer.play;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;
import com.github.jinsedeyuzhou.ijkplayer.utils.NetworkUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 * <p>
 * 两种方式控制屏幕变化
 * 1、设置view的宽高
 * 传递布局参数，设置布局参数
 * <p>
 * 2、设置父view的宽高
 * 在用的activity中更改添加布局的宽高
 */
public class VPlayPlayer extends RelativeLayout {

    private static final String TAG = "VPlayPlayer";
    private Context mContext;
    private Activity activity;
    private View rootView;
    private View controlbar;
    private SeekBar seekBar;
    private View liveBox;
    private IjkVideoView mVideoView;
    private ImageView mVideoReplay;
    private ImageView mVideoPlay;
    private ImageView mVideoFullscreen;
    private ImageView mVideoFinish;

    //屏幕宽度
    private int screenWidthPixels;
    //屏幕宽度
    public static int initHeight;
    private String url;

    //最大音量
    private  int mMaxVolume;
    private AudioManager audioManager;

    //是否显示
    private boolean isShowing;
    //是否是竖屏
    private boolean portrait;
    //亮度
    private float brightness = -1;
    //是否显示控制bar
    private boolean isShowContoller;
    //音量
    private int volume = -1;
    //新的位置
    private long newPosition = -1;
    //默认重复请求时间
    private long defaultRetryTime = 5000;

    //默认超时时间
    private int defaultTimeout = 3000;

    //播放总时长
    private long duration;

    private boolean instantSeeking;
    // 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
    private boolean isDragging;

    private boolean isLive;//是否为直播
    private boolean hidden; //暂停时不隐藏
    private boolean playerSupport;
    //当前位置
    private int currentPosition;
    //是否是仅仅全屏
    private boolean fullScreenOnly;
    //播放状态
    private int status = PlayStateParams.STATE_IDLE;
    private ProgressBar loading;
    private TextView currentTime;
    private LinearLayout top_box;
    private TextView endTime;
    private long pauseTime;
    private LinearLayout mVideoNetTie;
    private TextView mVideoNetTieIcon;
    private LinearLayout mVideoStaus;
    private TextView mStatusText;

    //是否允许移动播放
    private boolean isAllowModible;

    private ConnectionChangeReceiver changeReceiver;
    private LinearLayout mReplay;
    private LinearLayout gestureTouch;
    private LinearLayout gesture;
    private TextView mTvCurrent;
    private TextView mTvDuration;
    private ImageView mImageTip;
    private ProgressBar mProgressGesture;
    private TextView topTitle;




    private OrientationEventListener orientationEventListener;

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PlayStateParams.MESSAGE_FADE_OUT:
                    if (!hidden)
                        hide(false);
                    break;
                case PlayStateParams.MESSAGE_HIDE_CENTER_BOX:
                    gestureTouch.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);

                    }
                    updatePausePlay();
                    break;
                case PlayStateParams.MESSAGE_RESTART_PLAY:
                    play(url);
                    break;
                case PlayStateParams.MESSAGE_HIDE_NETWORK:
                    mVideoNetTie.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.app_video_fullscreen) {
                toggleFullScreen();
            } else if (v.getId() == R.id.app_video_play) {
                if (!NetworkUtils.isConnectionAvailable(mContext))
                    return;
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.app_video_replay_icon) {
                doPauseResume();
            } else if (v.getId() == R.id.app_video_finish) {
                if (!fullScreenOnly && !portrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            } else if (v.getId() == R.id.app_video_netTie_icon) {
                isAllowModible = true;
                mVideoView.start();
                handler.sendEmptyMessage(PlayStateParams.MESSAGE_HIDE_NETWORK);
            }
        }
    };
    private ProgressBar bottomProgress;
    private ImageView mVideoFinishIcon;


    public VPlayPlayer(Context context) {
        this(context,null);

    }

    public VPlayPlayer(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VPlayPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        activity = (Activity) this.mContext;
        initView();
        initAction();
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public VPlayPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    private void initView() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport=true;
        } catch (Throwable e) {
            Log.e("GiraffePlayer", "loadLibraries error", e);
        }
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_player, this, true);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        changeReceiver = new ConnectionChangeReceiver();
//        mContext.registerReceiver(changeReceiver, intentFilter);
        //播放控制
        controlbar = findViewById(R.id.player_controlbar);
        initHeight = findViewById(R.id.player_controlbar).getHeight();

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //屏幕宽度
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoReplay = (ImageView) findViewById(R.id.app_video_replay_icon);
        mVideoFinishIcon = (ImageView) findViewById(R.id.app_video_finish_icon);
        mReplay = (LinearLayout) findViewById(R.id.app_video_replay);

        liveBox = findViewById(R.id.app_video_box);
        //进度条
        loading = (ProgressBar) findViewById(R.id.app_video_loading);
        //控制播放
        mVideoPlay = (ImageView) findViewById(R.id.app_video_play);
        mVideoFullscreen = (ImageView) findViewById(R.id.app_video_fullscreen);
        seekBar = (SeekBar) findViewById(R.id.app_video_seekBar);
        currentTime = (TextView) findViewById(R.id.app_video_currentTime);
        endTime = (TextView) findViewById(R.id.app_video_endTime);



        //新的进度条
        gestureTouch = (LinearLayout) findViewById(R.id.ll_gesture_touch);
        gesture = (LinearLayout) findViewById(R.id.ll_gesture);
        mTvCurrent = (TextView) findViewById(R.id.tv_current);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mImageTip = (ImageView) findViewById(R.id.image_tip);
        mProgressGesture = (ProgressBar) findViewById(R.id.progressbar_gesture);
        bottomProgress = (ProgressBar) findViewById(R.id.bottom_progressbar);

        //topbox
        top_box = (LinearLayout) findViewById(R.id.app_video_top_box);
        mVideoFinish = (ImageView) findViewById(R.id.app_video_finish);
        topTitle = (TextView) findViewById(R.id.app_video_title);

        //status
        mVideoStaus = (LinearLayout) findViewById(R.id.app_video_status);
        mStatusText = (TextView) findViewById(R.id.app_video_status_text);

        //网络提示
        mVideoNetTie = (LinearLayout) findViewById(R.id.app_video_netTie);
        mVideoNetTieIcon = (TextView) findViewById(R.id.app_video_netTie_icon);


    }

    private void initAction() {
        mVideoFinish.setOnClickListener(onClickListener);
        mVideoFullscreen.setOnClickListener(onClickListener);
        mVideoReplay.setOnClickListener(onClickListener);
        mVideoPlay.setOnClickListener(onClickListener);
        mVideoNetTieIcon.setOnClickListener(onClickListener);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());


        liveBox.setClickable(true);
        liveBox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;

                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }

                return false;
            }
        });


        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_START");
                        statusChange(PlayStateParams.STATE_PREPARING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示 下载速度
//                        Toaster.show("download rate:" + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;
                }
                onInfoListener.onInfo(what, extra);
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (getScreenOrientation()
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoView.setLayoutParams(layoutParams);
                }
                statusChange(PlayStateParams.STATE_PLAYBACK_COMPLETED);
                oncomplete.run();
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(PlayStateParams.STATE_ERROR);
                onErrorListener.onError(what, extra);
                return true;
            }
        });

        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };

        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support));
        }
        hideAll();
    }





    private void updatePausePlay() {
        if (mVideoView.isPlaying()) {
            mVideoPlay.setImageResource(R.drawable.ic_stop_white_24dp);
            hidden = false;
        } else {
            mVideoPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            hidden = true;
        }
    }

    private void showStatus(String statusText) {
        mVideoStaus.setVisibility(View.VISIBLE);
        mStatusText.setText(statusText);

    }

    private void doPauseResume() {
        if (status == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            mReplay.setVisibility(View.GONE);
            mVideoView.seekTo(0);
            mVideoView.start();
        } else if (mVideoView.isPlaying()) {
            statusChange(PlayStateParams.STATE_PAUSED);
            mVideoView.pause();
        } else {
            statusChange(PlayStateParams.STATE_PLAYING);
            mVideoView.start();
        }
        updatePausePlay();
    }


    private void endVideo()
    {
      Bitmap bitmap = mVideoView.getBitmap();
        if (bitmap != null) {
            mVideoFinishIcon.setImageBitmap(bitmap);
            mReplay.setVisibility(View.VISIBLE);
        }
    }
    private void statusChange(int newStatus) {
        status = newStatus;
        if (!isLive && newStatus == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            Log.d(TAG, "STATE_PLAYBACK_COMPLETED");
            endVideo();

            hideAll();
            isShowContoller = false;
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(9);



        } else if (newStatus == PlayStateParams.STATE_ERROR) {
            Log.d(TAG, "STATE_ERROR");


            hideAll();
            if (isLive) {
                showStatus(activity.getResources().getString(R.string.small_problem));
                if (defaultRetryTime > 0) {
                    handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_RESTART_PLAY, defaultRetryTime);
                }
            } else {
                showStatus(activity.getResources().getString(R.string.small_problem));
            }
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            handler.removeCallbacksAndMessages(null);
        } else if (newStatus == PlayStateParams.STATE_PREPARING) {
            Log.d(TAG, "STATE_PREPARING");
            hideAll();
            loading.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_PLAYING) {
            Log.d(TAG, "STATE_PLAYING");
            hideAll();
            bottomProgress.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            isShowContoller = true;
//            if (!NetworkUtils.isNetworkAvailable(mContext) && !isAllowModible) {
//                mVideoView.pause();
//                mVideoNetTie.setVisibility(View.VISIBLE);
//                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
//
//
//            }

        }


    }


    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        setLayoutParams(layoutParams);
                    }
                }
            });
            orientationEventListener.enable();
        }
    }


//    /**
////     *
////     * @param portrait
////     */
//    private void doOnConfigurationChanged(final boolean portrait) {
//
//        if (mVideoView != null && !fullScreenOnly) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tryFullScreen(!portrait);
//                    if (portrait) {
//                        Log.v(TAG, "initHeight" + initHeight);
//                        if (!isLive)
//                            liveBox.getLayoutParams().height = initHeight;
//
//                    } else {
//                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
//                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
//                        liveBox.getLayoutParams().height = Math.min(heightPixels, widthPixels);
////                        liveBox.getLayoutParams().width=widthPixels;
//                        Log.v(TAG, "initHeight" + 0);
//                    }
//                    updateFullScreenButton();
//                }
//            });
//            orientationEventListener.enable();
//        }
//    }


    private void tryFullScreen(boolean fullScreen) {
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    private void setFullScreen(boolean fullScreen) {
        if (activity != null) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }


    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        //点击
        private boolean firstTouch;
        //音量控制
        private boolean volumeControl;
        //进度条
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            mVideoView.toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);

        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / mVideoView.getWidth());
                }
            } else {
                float percent = deltaY / mVideoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }


            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp:" + isShowing);
            if (isShowing || isLive) {
                hide(false);
            } else {
                show(defaultTimeout);
            }
            return true;
        }
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            mVideoStaus.setVisibility(View.GONE);//移动时隐藏掉状态image
            int newPosition = (int) ((duration * progress * 1.0) / 1000);
            String time = generateTime(newPosition);
            if (instantSeeking) {
                mVideoView.seekTo(newPosition);
            }
            currentTime.setText(time);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            if (instantSeeking) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking) {
                mVideoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);
        }
    };


    private void hideAll() {
        Log.d(TAG, "hideAll");
        mReplay.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        top_box.setVisibility(View.GONE);
        mVideoFullscreen.setVisibility(View.INVISIBLE);
        mVideoStaus.setVisibility(View.GONE);
        showBottomControl(false);

//        onControlPanelVisibilityChangeListener.change(false);
    }

//    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener = new OnControlPanelVisibilityChangeListener() {
//        @Override
//        public void change(boolean isShowing) {
//
//        }
//    };




    /**
     * 播放面板控制
     *
     * @param show
     */
    private void showBottomControl(boolean show) {

        mVideoPlay.setVisibility(show ? View.VISIBLE : View.GONE);
        currentTime.setVisibility(show ? View.VISIBLE : View.GONE);
        endTime.setVisibility(show ? View.VISIBLE : View.GONE);
        seekBar.setVisibility(show ? View.VISIBLE : View.GONE);
        controlbar.setVisibility(show ? View.VISIBLE : View.GONE);
        bottomProgress.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    /**
     * 切换全屏
     */
    public void toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateFullScreenButton();
    }

    /**
     * 更新全屏按钮
     */
    private void updateFullScreenButton() {
        Log.v(TAG, getScreenOrientation() + "");
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            mVideoFullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white_36dp);
        } else {
            mVideoFullscreen.setImageResource(R.drawable.ic_fullscreen_white_24dp);
        }
    }



    /**
     * 手势结束
     */
    private void endGesture() {

        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(PlayStateParams.MESSAGE_HIDE_CENTER_BOX);
        handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_HIDE_CENTER_BOX, 500);

    }

    public void hide() {
        Log.d(TAG, "hide:" + isShowing);

    }

    public void hide(boolean force) {

        if (force || isShowing) {
            showBottomControl(false);
            top_box.setVisibility(View.GONE);
            mVideoFullscreen.setVisibility(View.INVISIBLE);
            isShowing = false;
//            onControlPanelVisibilityChangeListener.change(false);
        }

    }




    @Override
    public void setEnabled(boolean enabled) {

    }


    public void show(int timeout) {
        Log.d(TAG, "show timeout:" + isShowing);
        if (!isShowContoller)
            return;
        if (!isShowing) {
            top_box.setVisibility(View.VISIBLE);
            if (!isLive) {
                showBottomControl(true);
            }
            if (!fullScreenOnly) {
                mVideoFullscreen.setVisibility(View.VISIBLE);
            }
            isShowing = true;
        }
        updatePausePlay();

        handler.removeMessages(PlayStateParams.MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.MESSAGE_FADE_OUT), timeout);
        }
    }

    public void show() {
    }


    /**
     * time to String
     *
     * @param time
     * @return
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 获取屏幕位置
     *
     * @return
     */
    public int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        hide(true);

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }

        if (gestureTouch.getVisibility() == View.GONE) {
            gestureTouch.setVisibility(View.VISIBLE);
            gesture.setVisibility(View.GONE);
            mImageTip.setImageResource(R.drawable.player_video_volume);
        }
        mProgressGesture.setProgress(i);

    }

    /**
     * 滑动改变播放时间
     *
     * @param percent
     */
    private void onProgressSlide(float percent) {
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);


        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {

            if (gestureTouch.getVisibility() == View.GONE) {
                gestureTouch.setVisibility(View.VISIBLE);
                gesture.setVisibility(View.VISIBLE);
            }
            mImageTip.setImageResource(showDelta > 0 ? R.drawable.forward_icon : R.drawable.backward_icon);
            String current = generateTime(newPosition);
            mTvCurrent.setText(current + "/");
            mTvDuration.setText(generateTime(duration));
            mProgressGesture.setProgress(duration <= 0 ? 0 : (int) (newPosition * 100 / duration));

        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        if (gestureTouch.getVisibility() == View.GONE) {
            gestureTouch.setVisibility(View.VISIBLE);
            gesture.setVisibility(View.GONE);
            mImageTip.setImageResource(R.drawable.player_video_light);
        }
        mProgressGesture.setProgress((int) (lpa.screenBrightness * 100));
        activity.getWindow().setAttributes(lpa);

    }

    /**
     * 设置seekbar进度
     *
     * @return
     */
    private long setProgress() {

        Log.v(TAG,"setProgress");
        if (isDragging) {
            return 0;
        }

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
                bottomProgress.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
            bottomProgress.setSecondaryProgress(percent*10);
        }

        this.duration = duration;
        currentTime.setText(generateTime(position));
        endTime.setText(generateTime(this.duration));
        return position;
    }


    //=========================全屏和大小屏判定===================================
    public static final int FULLSCREEN_ID = 33797;
    public static final int TINY_ID = 33798;
//    public void startWindowTiny() {
//        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
//
//        ViewGroup vp = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
//        View old = vp.findViewById(TINY_ID);
//        if (old != null) {
//            vp.removeView(old);
//        }
//        if (textureViewContainer.getChildCount() > 0) {
//            textureViewContainer.removeAllViews();
//        }
//        try {
//            Constructor<IJKVideoPlayer> constructor = (Constructor<IJKVideoPlayer>) IJKVideoPlayer.this.getClass().getConstructor(Context.class);
//            IJKVideoPlayer mIJKVideoPlayer = constructor.newInstance(getContext());
//            mIJKVideoPlayer.setId(TINY_ID);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(400, 400);
//            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//            vp.addView(mIJKVideoPlayer, lp);
//            mIJKVideoPlayer.setUp(url, IJKVideoPlayerStandard.SCREEN_WINDOW_TINY, objects);
//            mIJKVideoPlayer.setUiWitStateAndScreen(currentState);
//            mIJKVideoPlayer.addTextureView();
//            IJKVideoPlayerManager.putListener(mIJKVideoPlayer);
//
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }



    //====================对外提供的方法==========================================

    public boolean isShowing() {
        return isShowing;
    }
    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = mVideoView.getCurrentPosition();
        } else {
            /**直播*/
            currentPosition = -1;
        }
        return currentPosition;
    }
//
    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        duration = mVideoView.getDuration();
        return duration;
    }


    /**
     * 监听全屏跟非全屏
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    /**
     * 右上角图片
     *
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        mVideoFinish.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    /**
     * @param isLive 设置是否直播
     */
    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }
    /**
     * is player support this device
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }
    /**
     * 快进
     *
     * @param percent
     */
    public void forward(float percent) {
        if (isLive || percent > 1 || percent < -1) {
            return;
        }
        onProgressSlide(percent);
        showBottomControl(true);
        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        endGesture();
    }


    public boolean onBackPressed() {
        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }


    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.MESSAGE_FADE_OUT);
    }

    public void play(String url) {
        if (playerSupport) {
            loading.setVisibility(View.VISIBLE);
            mVideoView.setVideoPath(url);
            mVideoView.start();
        }

    }

    public void start(String  path) {
        Uri uri = Uri.parse(path);
        hideAll();
        loading.setVisibility(View.VISIBLE);
//        isShowContoller = false;
        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();

        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }



    public void onPause() {
        Log.d(TAG, "onPause" + status);

        pauseTime = System.currentTimeMillis();
        show(0);//把系统状态栏显示出来
        if (status == PlayStateParams.STATE_PLAYING) {
            mVideoView.pause();
            if (!isLive) {
                currentPosition = mVideoView.getCurrentPosition();
            }
//            statusChange(PlayStateParams.STATE_PAUSED);
        }

    }

    public int getStatus() {
        return status;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy" + status);
        orientationEventListener.disable();
        handler.removeCallbacksAndMessages(null);
        mVideoView.stopPlayback();
    }

    public void onResume() {
        Log.d(TAG, "onResume" + status);
        orientationEventListener.enable();
        mVideoView.resume();

        pauseTime = 0;
        if (status == PlayStateParams.STATE_PLAYING) {
            if (isLive) {
                mVideoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    mVideoView.seekTo(currentPosition);
                }
            }
//            statusChange(PlayStateParams.STATE_PLAYING);
            mVideoView.start();

        }
    }

    //=====================对外提供接口===========================================


    public interface OnErrorListener {
        void onError(int what, int extra);
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

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };

    //=====================================网络状态改变广播类==============================================

    /**
     * 网络改变监听
     */
    private class ConnectionChangeReceiver extends BroadcastReceiver {
//        private final String TAG = ConnectionChangeReceiver.class.getSimpleName();

        private boolean isWifi;
        private boolean isMobile;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "网络状态改变");
            //获得网络连接服务
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            //获取wifi连接状态
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            //判断是否正在使用wifi网络
            if (wifi == NetworkInfo.State.CONNECTED) {
                isWifi = true;
            } else
                isWifi = false;
            //获取GPRS状态
            NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            //判断是否在使用GPRS网络
            if (state == NetworkInfo.State.CONNECTED) {
                isMobile = true;
            } else
                isMobile = false;
            //如果没有连接成功

            Log.d(TAG, "isWifi:" + isWifi + "isMobile:" + isMobile);
            if (!isWifi && isMobile) {
                mVideoView.pause();
                mVideoNetTie.setVisibility(View.VISIBLE);

                loading.setVisibility(View.GONE);
            } else if (!isWifi && !isMobile) {
                mVideoView.pause();
                Toast.makeText(context, "当前网络无连接", Toast.LENGTH_SHORT).show();
            }


        }

    }



}
