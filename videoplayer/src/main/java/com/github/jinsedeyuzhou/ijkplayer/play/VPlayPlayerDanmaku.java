package com.github.jinsedeyuzhou.ijkplayer.play;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.BaseDanmakuConverter;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.BiliDanmukuParser;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.DanamakuAdapter;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.OnDanmakuListener;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;
import com.github.jinsedeyuzhou.ijkplayer.utils.NetworkUtils;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class VPlayPlayerDanmaku extends FrameLayout {

    private static final String TAG = "VPlayPlayer";
    private Context mContext;
    private Activity activity;


    private View controlbar;
    private SeekBar seekBar;
    private IjkVideoView mVideoView;
    private ImageView mVideoReplay;
    private ImageView mVideoPlay;
    private ImageView mVideoFullscreen;
    private ImageView mVideoFinish;
    private ProgressBar loading;
    private TextView currentTime;
    private RelativeLayout top_box;
    private TextView endTime;
    private LinearLayout mVideoNetTie;
    private TextView mVideoNetTieIcon;
    private LinearLayout mVideoStaus;
    private TextView mStatusText;
    private RelativeLayout mReplay;
    private LinearLayout gestureTouch;
    private LinearLayout gesture;
    private TextView mTvCurrent;
    private TextView mTvDuration;
    private ImageView mImageTip;
    private ProgressBar mProgressGesture;
    private MarqueeTextView topTitle;
    private ProgressBar bottomProgress;
    private ImageView mVideoFinishIcon;
    private View live_box;
    private ImageView mVideoLock;
    private ImageView mVideoShare;


    //屏幕宽度
    private int screenWidthPixels;
    //屏幕高度
    private int screenHeightPixels;
    //布局宽度
    public static int initHeight;
    //最大音量
    private int mMaxVolume;
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
    private boolean isLock;//是否锁屏

    //是否支持该设备
    private boolean playerSupport;
    //当前位置
    private int currentPosition;
    //是否是仅仅全屏
    private boolean fullScreenOnly;
    private long pauseTime;
    //播放状态
    private int status = PlayStateParams.STATE_IDLE;
    //是否允许移动播放
    private boolean isAllowModible;
    //是否开启网络监听
    private boolean isNetListener = true;
    //锁屏时是否是播放状态
    private boolean isAutoPause;
    private boolean mIsLand = false; // 是否是横屏
    private boolean mClick = false; // 是否点击
    private boolean mClickLand = true; // 点击进入横屏
    private boolean mClickPort = true; // 点击进入竖屏
    private String url;


    private OrientationEventListener orientationEventListener;
    private NetChangeReceiver changeReceiver;
    private OnClickOrientationListener onClickOrientationListener;

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PlayStateParams.MESSAGE_FADE_OUT:
//                    if (!hidden)
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
            int id = v.getId();
            if (id == R.id.app_video_fullscreen) {
                toggleFullScreen();
            } else if (id == R.id.app_video_play) {
                if (!NetworkUtils.isConnectionAvailable(mContext))
                    return;
                doPauseResume();
//                show(defaultTimeout);
            } else if (id == R.id.app_video_replay_icon) {
                if (!NetworkUtils.isConnectionAvailable(mContext))
                    return;
                doPauseResume();
            } else if (id == R.id.app_video_finish) {
                if (!onBackPressed()) {
                    activity.finish();
                }

            } else if (id == R.id.app_video_netTie_icon) {
                isAllowModible = true;
                if (currentPosition == 0) {
                    play(url);
                } else
                    doPauseResume();
                mVideoNetTie.setVisibility(View.GONE);
            } else if (id == R.id.app_video_lock) {
                if (isLock) {
                    isLock = false;
                    mVideoLock.setImageResource(R.drawable.video_unlock);
                } else {
                    isLock = true;
                    mVideoLock.setImageResource(R.drawable.video_lock);
                }
            } else if (id == R.id.app_video_share) {

            }
            //一下弹幕
            else if (id == R.id.tv_open_edit_danmaku) {

            } else if (id == R.id.iv_danmaku_control) {
                toggleDanmakuShow();
            } else if (id == R.id.iv_cancel_send) {

            } else if (id == R.id.iv_do_send) {

            }

        }
    };


    public VPlayPlayerDanmaku(Context context) {
        super(context);
        init(context);
    }


    public VPlayPlayerDanmaku(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VPlayPlayerDanmaku(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        activity = (Activity) mContext;
        initView();
        initAction();
        initMediaPlayer();
        initDanmaku();
    }


    private void initView() {
        View.inflate(mContext, R.layout.view_player, this);
        //播放控制
        live_box = findViewById(R.id.app_video_box);
        controlbar = findViewById(R.id.player_controlbar);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoReplay = (ImageView) findViewById(R.id.app_video_replay_icon);
        mVideoFinishIcon = (ImageView) findViewById(R.id.app_video_finish_icon);
        mReplay = (RelativeLayout) findViewById(R.id.app_video_replay);
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
        top_box = (RelativeLayout) findViewById(R.id.app_video_top_box);
        mVideoFinish = (ImageView) findViewById(R.id.app_video_finish);
        mVideoLock = (ImageView) findViewById(R.id.app_video_lock);
        mVideoShare = (ImageView) findViewById(R.id.app_video_share);
        topTitle = (MarqueeTextView) findViewById(R.id.app_video_title);

        //status
        mVideoStaus = (LinearLayout) findViewById(R.id.app_video_status);
        mStatusText = (TextView) findViewById(R.id.app_video_status_text);

        //网络提示,网络相关参数提示
        mVideoNetTie = (LinearLayout) findViewById(R.id.app_video_netTie);
        mVideoNetTieIcon = (TextView) findViewById(R.id.app_video_netTie_icon);

        //屏幕宽度
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        screenHeightPixels = activity.getResources().getDisplayMetrics().heightPixels;
//        initHeight =getHeight();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (initHeight == 0) {
            initHeight = getHeight();
            screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        }
    }


    private void initAction() {
        mVideoLock.setOnClickListener(onClickListener);
        mVideoShare.setOnClickListener(onClickListener);
        mVideoFinish.setOnClickListener(onClickListener);
        mVideoFullscreen.setOnClickListener(onClickListener);
        mVideoReplay.setOnClickListener(onClickListener);
        mVideoPlay.setOnClickListener(onClickListener);
        mVideoNetTieIcon.setOnClickListener(onClickListener);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());

        controlbar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("custommedia", "event");

                Rect seekRect = new Rect();
                seekBar.getHitRect(seekRect);
                //如果滑动在此高度内，此进度条生效，seekbar区域向上和向下拓展50像素
                if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {

                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    } else {
                        MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                                event.getAction(), x, y, event.getMetaState());
                        return seekBar.onTouchEvent(me);
                    }

                }
                return false;
            }
        });

        setKeepScreenOn(true);
        setClickable(true);
        setOnTouchListener(new OnTouchListener() {
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
    }

    private void initMediaPlayer() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e("VPlayPlayer", "loadLibraries error", e);
        }
        //需要更改为在引用的Application中使用   audioManager = (AudioManager) PlayerApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

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
                //释放内存
                Runtime.getRuntime().gc();
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

        orientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int rotation) {
                // 设置竖屏
                if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                    if (mClick) {
                        if (mIsLand && !mClickLand) {
                            return;
                        } else {
                            mClickPort = true;
                            mClick = false;
                            mIsLand = false;
                        }
                    } else {
                        if (mIsLand && !isLock) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            mIsLand = false;
                            mClick = false;
                        }
                    }
                }
                // 设置横屏
                else if (((rotation >= 230) && (rotation <= 310))) {
                    if (mClick) {
                        if (!mIsLand && !mClickPort) {
                            return;
                        } else {
                            mClickLand = true;
                            mClick = false;
                            mIsLand = true;
                        }
                    } else {
                        if (!mIsLand) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            mIsLand = true;
                            mClick = false;
                        }
                    }
                }
            }
        };
        orientationEventListener.enable();

        hideAll();
        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support));
        }
    }


    private void updatePausePlay() {
        if (mVideoView.isPlaying()) {
            mVideoPlay.setSelected(true);
        } else {
            mVideoPlay.setSelected(false);
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
            mVideoPlay.setSelected(true);
        } else if (mVideoView.isPlaying()) {
            statusChange(PlayStateParams.STATE_PAUSED);
            isAutoPause = true;
            mVideoView.pause();
            mVideoPlay.setSelected(false);
        } else {
            statusChange(PlayStateParams.STATE_PLAYING);
            mVideoView.start();
            mVideoPlay.setSelected(true);
//            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.MESSAGE_FADE_OUT),defaultTimeout);
        }
    }


    private void endVideo() {
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
            hideAll();
            endVideo();
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
            loading.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_PLAYING) {
            Log.d(TAG, "STATE_PLAYING");
            bottomProgress.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            isShowContoller = true;
        } else if (newStatus == PlayStateParams.STATE_PAUSED) {
            handler.removeMessages(PlayStateParams.MESSAGE_FADE_OUT);
//            isShowContoller = false;
        }


    }

    /**
     * 当竖横屏切换时处理视频窗口
     *
     * @param portrait
     */
    private void doOnConfigurationChanged(final boolean portrait) {

        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    ViewGroup.LayoutParams params = getLayoutParams();
                    if (null == params)
                        return;
                    if (portrait) {
                        Log.v(TAG, "initHeight" + initHeight);
                        params.height = initHeight;
                        setLayoutParams(params);
                        requestLayout();

                    } else {
                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                        params.height = Math.min(heightPixels, widthPixels);
                        setLayoutParams(params);
                        requestLayout();
                        Log.v(TAG, "initHeight" + 0);
                    }
                    updateFullScreenButton();
                }
            });

        }
    }

//    public void doOnConfigurationChanged(final boolean portrait) {
//        if (mVideoView != null) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tryFullScreen(!portrait);
//                    if (portrait) {
//                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                        layoutParams.height = initHeight;
////                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
////                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                        setLayoutParams(layoutParams);
//                        requestLayout();
//                    } else {
//                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
//                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
//                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                        layoutParams.height = screenWidthPixels;
////                        layoutParams.width = widthPixels;
//                        setLayoutParams(layoutParams);
//                    }
//                    updateFullScreenButton();
//                }
//            });
//
//        }
//    }


    private void tryFullScreen(boolean fullScreen) {
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    //次动画会导致actionbar再度显示
                    supportActionBar.setShowHideAnimationEnabled(false);
                    supportActionBar.hide();
                } else {
                    supportActionBar.setShowHideAnimationEnabled(false);
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
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
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
            //横屏下拦截事件
            if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return true;
            } else {
                return super.onDown(e);
            }

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

    }


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
        mClick = true;
        if (!mIsLand) {
            if (onClickOrientationListener != null) {
                onClickOrientationListener.landscape();
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mIsLand = true;
            mClickLand = false;
        } else {
            if (onClickOrientationListener != null) {
                onClickOrientationListener.portrait();
            }
            if (!isLock) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mIsLand = false;
                mClickPort = false;
            }
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
//        if (!isShowContoller)
//            return;
        if (force || isShowing) {
            showBottomControl(false);
            top_box.setVisibility(View.GONE);
            mVideoFullscreen.setVisibility(View.INVISIBLE);
            isShowing = false;
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
     * 处理音量键，避免外部按音量键后导航栏和状态栏显示出来退不回去的状态
     *
     * @param keyCode
     * @return
     */
    public boolean handleVolumeKey(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setVolume(true);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            setVolume(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 递增或递减音量，量度按最大音量的 1/15
     *
     * @param isIncrease 递增或递减
     */
    private void setVolume(boolean isIncrease) {
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (isIncrease) {
            curVolume += mMaxVolume / 15;
        } else {
            curVolume -= mMaxVolume / 15;
        }
        if (curVolume > mMaxVolume) {
            curVolume = mMaxVolume;
        } else if (curVolume < 0) {
            curVolume = 0;
        }
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
        // 变更进度条
        if (gestureTouch.getVisibility() == View.GONE) {
            gestureTouch.setVisibility(View.VISIBLE);
            gesture.setVisibility(View.GONE);
            mImageTip.setImageResource(R.drawable.player_video_volume);
        }
        mProgressGesture.setProgress((curVolume * 100 / mMaxVolume));
        endGesture();
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

        Log.v(TAG, "setProgress");
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
            bottomProgress.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;
        currentTime.setText(generateTime(position));
        endTime.setText(generateTime(this.duration));
        return position;
    }

    //============================弹幕=============================================
    private DanmakuView mDanmakuView;

    // 弹幕显示/隐藏按钮
    private ImageView mIvDanmakuControl;
    // 弹幕编辑布局打开按钮
    private TextView mTvOpenEditDanmaku;
    // 弹幕编辑布局
    private View mEditDanmakuLayout;
    //弹幕编辑内容空间
    private EditText mEtDanmakuContent;
    //弹幕取消
    private ImageView mIvCancelSend;
    //弹幕发送
    private ImageView mIvDoSend;
    //选项参数布局
    private View mDanmakuOptionsBasic;
    //更多基础布局
    private View mDanmakuMoreOptions;
    //当前颜色
    private RadioButton mDanmakuCurColor;
    //更多颜色按钮
    private ImageView mDanmakuMoreColorIcon;
    //字体大小按钮
    private RadioGroup mDanmakuTextSizeOptions;
    //弹幕类型按钮
    private RadioGroup mDanmakuTypeOptions;
    //可选颜色按钮
    private RadioGroup mDanmakuColorOptions;


    // 弹幕控制相关
    private DanmakuContext mDanmakuContext;
    // 弹幕解析器
    private BaseDanmakuParser mDanmakuParser;
    // 弹幕加载器
    private ILoader mDanmakuLoader;
    // 弹幕数据转换器
    private BaseDanmakuConverter mDanmakuConverter;
    // 弹幕监听器
    private OnDanmakuListener mDanmakuListener;
    // 弹幕颜色
    private int mDanmakuTextColor = Color.WHITE;
    // 弹幕字体大小
    private float mDanmakuTextSize = PlayStateParams.INVALID_VALUE;
    // 弹幕类型
    private int mDanmakuType = BaseDanmaku.TYPE_SCROLL_RL;
    // 弹幕基础设置布局的宽度
    private int mBasicOptionsWidth = PlayStateParams.INVALID_VALUE;
    // 弹幕更多颜色设置布局宽度
    private int mMoreOptionsWidth = PlayStateParams.INVALID_VALUE;
    // 弹幕要跳转的目标位置，等视频播放再跳转，不然老出现只有弹幕在动的情况
    private long mDanmakuTargetPosition = PlayStateParams.INVALID_VALUE;
    // 是否使能弹幕
    private boolean mIsEnableDanmaku = false;
    private long mDanmakuStartSeekPosition = -1;
    //是否展示弹幕
//    private boolean mDanmaKuShow = true;

    /**
     * 初始化布局
     */
    private void initDanmaku() {
        mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);

        //控制弹幕
        mTvOpenEditDanmaku = (TextView) findViewById(R.id.tv_open_edit_danmaku);
        mIvDanmakuControl = (ImageView) findViewById(R.id.iv_danmaku_control);

        //编辑弹幕
        mEditDanmakuLayout = findViewById(R.id.ll_edit_danmaku);
        mEtDanmakuContent = (EditText) findViewById(R.id.et_danmaku_content);
        mIvCancelSend = (ImageView) findViewById(R.id.iv_cancel_send);
        mIvDoSend = (ImageView) findViewById(R.id.iv_do_send);
        mIvDanmakuControl.setOnClickListener(onClickListener);
        mTvOpenEditDanmaku.setOnClickListener(onClickListener);
        mIvCancelSend.setOnClickListener(onClickListener);
        mIvDoSend.setOnClickListener(onClickListener);
        // 这些为弹幕配置处理
        int oneBtnWidth = getResources().getDimensionPixelOffset(R.dimen.danmaku_input_options_color_radio_btn_size);
        // 布局宽度为每个选项卡宽度 * 12 个，有12种可选颜色
        mMoreOptionsWidth = oneBtnWidth * 12;
        mDanmakuOptionsBasic = findViewById(R.id.input_options_basic);
        mDanmakuMoreOptions = findViewById(R.id.input_options_more);
        mDanmakuMoreOptions.setOnClickListener(onClickListener);
        mDanmakuCurColor = (RadioButton) findViewById(R.id.input_options_color_current);
        mDanmakuMoreColorIcon = (ImageView) findViewById(R.id.input_options_color_more_icon);
        mDanmakuTextSizeOptions = (RadioGroup) findViewById(R.id.input_options_group_textsize);
        mDanmakuTypeOptions = (RadioGroup) findViewById(R.id.input_options_group_type);
        mDanmakuColorOptions = (RadioGroup) findViewById(R.id.input_options_color_group);
        mDanmakuTextSizeOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.input_options_small_textsize) {
                    mDanmakuTextSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f) * 0.7f;
                } else if (checkedId == R.id.input_options_medium_textsize) {
                    mDanmakuTextSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
                }
            }
        });
        mDanmakuTypeOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.input_options_rl_type) {
                    mDanmakuType = BaseDanmaku.TYPE_SCROLL_RL;
                } else if (checkedId == R.id.input_options_top_type) {
                    mDanmakuType = BaseDanmaku.TYPE_FIX_TOP;
                } else if (checkedId == R.id.input_options_bottom_type) {
                    mDanmakuType = BaseDanmaku.TYPE_FIX_BOTTOM;
                }
            }
        });
        mDanmakuColorOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 取的是 tag 字符串值，需转换为颜色
                String color = (String) findViewById(checkedId).getTag();
                mDanmakuTextColor = Color.parseColor(color);
                mDanmakuCurColor.setBackgroundColor(mDanmakuTextColor);
            }
        });



    }

    /**
     * 使能弹幕功能
     *
     * @return
     */
    public VPlayPlayerDanmaku enableDanmaku() {
        mIsEnableDanmaku = true;
        loadDanmaku();
        return this;
    }

    /**
     * 加载弹幕
     */
    private void loadDanmaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        DanamakuAdapter danamakuAdapter = new DanamakuAdapter(mDanmakuView);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
//                .setCacheStuffer(new SpannedCacheStuffer(), danamakuAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmakuParser == null) {
            mDanmakuParser = new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    if (mDanmakuView != null&&mVideoView.isPlaying()) {
                        mDanmakuView.start();
                        if (getDanmakuStartSeekPosition() != -1) {
                            resolveDanmakuSeek(getDanmakuStartSeekPosition());
                            setDanmakuStartSeekPosition(-1);
                        }
                        toggleDanmakuShow();
                    }
                }
            });


            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {

                @Override
                public boolean onDanmakuClick(IDanmakus danmakus) {
                    Log.d("DFM", "onDanmakuClick: danmakus size:" + danmakus.size());
                    BaseDanmaku latest = danmakus.last();
                    if (null != latest) {
                        Log.d("DFM", "onDanmakuClick: text of latest danmaku:" + latest.text);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onViewClick(IDanmakuView view) {
                    return false;
                }
            });

            mDanmakuView.prepare(mDanmakuParser, mDanmakuContext);
//            mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }

    }

    /**
     * 弹幕偏移
     */
    private void resolveDanmakuSeek(long time) {
        if (mDanmakuView!= null && mDanmakuView.isPrepared()) {
            mDanmakuView.seekTo(time);
        }
    }

    /**
     * 切换弹幕的显示/隐藏
     */
    private void toggleDanmakuShow() {
        if (mIvDanmakuControl.isSelected()) {
            showOrHideDanmaku(true);
        } else {
            showOrHideDanmaku(false);
        }
    }

    /**
     * 显示/隐藏弹幕
     *
     * @param isShow 是否显示
     * @return
     */
    public void showOrHideDanmaku(boolean isShow) {
        if (isShow) {
            mIvDanmakuControl.setSelected(false);
            mDanmakuView.show();
        } else {
            mIvDanmakuControl.setSelected(true);
            mDanmakuView.hide();
        }
    }



    /**
     * 设置弹幕资源，默认资源格式需满足 bilibili 的弹幕文件格式，
     * 配合{@link #setDanmakuCustomParser}来进行自定义弹幕解析方式，{@link #setDanmakuCustomParser}必须先调用
     *
     * @param stream 弹幕资源
     * @return
     */
    public VPlayPlayerDanmaku setDanmakuSource(InputStream stream) {
        if (stream == null) {
            return this;
        }
        if (!mIsEnableDanmaku) {
            throw new RuntimeException("Danmaku is disable, use enableDanmaku() first");
        }
        if (mDanmakuLoader == null) {
            mDanmakuLoader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        }
        try {
            mDanmakuLoader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        IDataSource<?> dataSource = mDanmakuLoader.getDataSource();
        if (mDanmakuParser == null) {
            mDanmakuParser = new BiliDanmukuParser();
        }
        mDanmakuParser.load(dataSource);
        return this;
    }

    /**
     * 设置弹幕资源，默认资源格式需满足 bilibili 的弹幕文件格式，
     * 配合{@link #setDanmakuCustomParser}来进行自定义弹幕解析方式，{@link #setDanmakuCustomParser}必须先调用
     *
     * @param uri 弹幕资源
     * @return
     */
    public VPlayPlayerDanmaku setDanmakuSource(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return this;
        }
        if (!mIsEnableDanmaku) {
            throw new RuntimeException("Danmaku is disable, use enableDanmaku() first");
        }
        if (mDanmakuLoader == null) {
            mDanmakuLoader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        }
        try {
            mDanmakuLoader.load(uri);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        IDataSource<?> dataSource = mDanmakuLoader.getDataSource();
        if (mDanmakuParser == null) {
            mDanmakuParser = new BiliDanmukuParser();
        }
        mDanmakuParser.load(dataSource);
        return this;
    }

    /**
     * 自定义弹幕解析器，配合{@link #setDanmakuSource}使用，先于{@link #setDanmakuSource}调用
     *
     * @param parser    解析器
     * @param loader    加载器
     * @param converter 转换器
     * @return
     */
    public VPlayPlayerDanmaku setDanmakuCustomParser(BaseDanmakuParser parser, ILoader loader, BaseDanmakuConverter converter) {
        mDanmakuParser = parser;
        mDanmakuLoader = loader;
        mDanmakuConverter = converter;
        return this;
    }


    /**
     * 发射弹幕
     *
     * @param text   内容
     * @param isLive 是否直播
     * @return  弹幕数据
     */
    public void addDanmaku(String text, boolean isLive) {
        if (!mIsEnableDanmaku) {
            throw new RuntimeException("Danmaku is disable, use enableDanmaku() first");
        }
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(mContext, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(mDanmakuType);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        if (mDanmakuTextSize == PlayStateParams.INVALID_VALUE) {
            mDanmakuTextSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        }
        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.isLive = isLive;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.textSize = mDanmakuTextSize;
        danmaku.textColor = mDanmakuTextColor;
        danmaku.underlineColor = Color.GREEN;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
        mDanmakuView.addDanmaku(danmaku);

        if (mDanmakuListener != null) {
            if (mDanmakuConverter != null) {
                mDanmakuListener.onDataObtain(mDanmakuConverter.convertDanmaku(danmaku));
            } else {
                mDanmakuListener.onDataObtain(danmaku);
            }
        }
    }
  /**
     * 发射弹幕
     *  图文混排
     * @param text   内容和图片
     * @param isLive 是否直播
     * @return  弹幕数据
     */
    public void addDanmaKuShowTextAndImage(String text, Drawable drawable, boolean isLive) {
        if (!mIsEnableDanmaku) {
            throw new RuntimeException("Danmaku is disable, use enableDanmaku() first");
        }
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(mContext, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(mDanmakuType);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        if (mDanmakuTextSize == PlayStateParams.INVALID_VALUE) {
            mDanmakuTextSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        }
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(text,drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        danmaku.isLive = isLive;
        mDanmakuView.addDanmaku(danmaku);

        if (mDanmakuListener != null) {
            if (mDanmakuConverter != null) {
                mDanmakuListener.onDataObtain(mDanmakuConverter.convertDanmaku(danmaku));
            } else {
                mDanmakuListener.onDataObtain(danmaku);
            }
        }
    }

    private SpannableStringBuilder createSpannable(String text,Drawable drawable) {
//        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }


    public BaseDanmakuParser getParser() {
        return mDanmakuParser;
    }

    public DanmakuContext getDanmakuContext() {
        return mDanmakuContext;
    }

    public IDanmakuView getDanmakuView() {
        return mDanmakuView;
    }

    public long getDanmakuStartSeekPosition() {
        return mDanmakuStartSeekPosition;
    }

    public void setDanmakuStartSeekPosition(long danmakuStartSeekPosition) {
        this.mDanmakuStartSeekPosition = danmakuStartSeekPosition;
    }


    // 可以在当前activity中 利用系统布局R.ANDROID.CONTENT 加载视频，设置全屏和小屏，但是列表会会导致抖动，需要延迟加载或者设置动画

    //=========================全屏和大小屏===================================
    public static final int FULLSCREEN_ID = 33797;
    public static final int TINY_ID = 33798;


    //====================对外提供的方法==========================================
    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
        isAutoPause = false;
        mIsLand = false; // 是否是横屏
        mClick = false; // 是否点击
        mClickLand = true; // 点击进入横屏
        mClickPort = true; // 点击进入竖屏
        bottomProgress.setProgress(0);
        seekBar.setProgress(0);
    }

    public void release() {
        if (mVideoView != null)
            mVideoView.release(true);
        isAutoPause = false;
        mIsLand = false; // 是否是横屏
        mClick = false; // 是否点击
        mClickLand = true; // 点击进入横屏
        mClickPort = true; // 点击进入竖屏
        bottomProgress.setProgress(0);
        seekBar.setProgress(0);
    }

    /**
     * 获得某个控件
     *
     * @param ViewId
     * @return
     */
    public View getView(int ViewId) {
        return activity.findViewById(ViewId);
    }

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
     *
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
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

            if (!isLock) {
                mIsLand = false; // 是否是横屏
                mClick = false; // 是否点击
                mClickLand = true; // 点击进入横屏
                mClickPort = true; // 点击进入竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
            return true;

        } else {

            return false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

            if (!isLock) {
                mIsLand = false; // 是否是横屏
                mClick = false; // 是否点击
                mClickLand = true; // 点击进入横屏
                mClickPort = true; // 点击进入竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }


    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.MESSAGE_FADE_OUT);
        showBottomControl(isShowContoller);
    }

    public void setVideoPath(String path) {
        Uri uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);
        if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
            unregisterNetReceiver();
        } else {
            // 注册网路变化的监听
            registerNetReceiver();
        }
    }

    public void play(String url) {
        this.url = url;
        play(url, 0);
    }

    public void play(String url, int position) {
        this.url = url;
        if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
            unregisterNetReceiver();
        } else {
            // 注册网路变化的监听
            registerNetReceiver();
        }

        if (!isAllowModible && isNetListener && NetworkUtils.getNetworkType(mContext) < 7 && NetworkUtils.getNetworkType(mContext) > 3) {
            mVideoNetTie.setVisibility(View.VISIBLE);
        } else {
            if (playerSupport) {
                loading.setVisibility(View.VISIBLE);
                mVideoView.setVideoPath(url);
                if (isLive) {
                    mVideoView.seekTo(0);
                } else {
                    mVideoView.seekTo(position);
                }
                mVideoView.start();

            }
        }

    }

    public void pause() {
        mVideoPlay.setSelected(false);
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    public void start() {
        if (!isAllowModible && isNetListener && NetworkUtils.getNetworkType(mContext) < 7 && NetworkUtils.getNetworkType(mContext) > 3) {
            mVideoNetTie.setVisibility(View.VISIBLE);
        } else {
            if (playerSupport) {
                loading.setVisibility(View.VISIBLE);
                if (isLive) {
                    mVideoView.seekTo(0);
                    mVideoView.start();
                }
                if (status == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
                    mReplay.setVisibility(View.GONE);
                    mVideoView.seekTo(0);
                    mVideoView.start();
                    mVideoPlay.setSelected(true);
                } else if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                    mVideoPlay.setSelected(true);
                } else {
                    mVideoView.stopPlayback();
                    mVideoView.start();
                }

            }
        }

    }


    public void onPause() {
        Log.d(TAG, "onPause" + status);

        pauseTime = System.currentTimeMillis();
        show(0);//把系统状态栏显示出来
        if (status == PlayStateParams.STATE_PLAYING) {
            mVideoView.pause();
            isAutoPause = true;
            if (!isLive) {
                currentPosition = mVideoView.getCurrentPosition();
            }
            statusChange(PlayStateParams.STATE_PAUSED);
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }

    }

    public int getStatus() {
        return status;
    }

    private void reset() {
        isLive = false;
        isAutoPause = false;
        mIsLand = false; // 是否是横屏
        mClick = false; // 是否点击
        mClickLand = true; // 点击进入横屏
        mClickPort = true; // 点击进入竖屏
        bottomProgress.setProgress(0);
        seekBar.setProgress(0);
        isShowContoller = false;
        mVideoView.stopPlayback();
        mVideoView.release(true);

    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy" + status);
        orientationEventListener.disable();
        unregisterNetReceiver();
        handler.removeCallbacksAndMessages(null);
        reset();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }

    }

    public void onResume() {
        Log.d(TAG, "onResume" + status);
        pauseTime = 0;
        if (status == PlayStateParams.STATE_PLAYING) {

            if (isLive) {
                mVideoView.seekTo(0);
            } else if (isAutoPause) {
                {
                    if (currentPosition > 0) {
                        mVideoView.seekTo(currentPosition);
                    }
                }
                mVideoView.start();
                isAutoPause = false;
                statusChange(PlayStateParams.STATE_PLAYING);
            }

        }

        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    //=====================对外提供接口===========================================


    public interface OnClickOrientationListener {
        void landscape();

        void portrait();
    }


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

    private OnNetChangeListener onNetChangeListener;

    public void setOnNetChangeListener(OnNetChangeListener onNetChangeListener) {
        this.onNetChangeListener = onNetChangeListener;
    }

    public interface OnNetChangeListener {
        // wifi
        void onWifi();

        // 手机
        void onMobile();

        // 网络断开
        void onDisConnect();

        // 网路不可用
        void onNoAvailable();
    }

    /**
     * 注册网络监听器
     */
    private void registerNetReceiver() {
        if (changeReceiver == null) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            changeReceiver = new NetChangeReceiver();
            mContext.registerReceiver(changeReceiver, filter);
        }
    }

    /**
     * 销毁网络监听器
     */
    private void unregisterNetReceiver() {
        if (changeReceiver != null) {
            mContext.unregisterReceiver(changeReceiver);
            changeReceiver = null;
        }
    }

    /**
     * 网络改变监听
     */
    private class NetChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "网络状态改变");
//            if (onNetChangeListener == null) {
//                return;
//            }
            if (NetworkUtils.getNetworkType(activity) == 3) {// 网络是WIFI
//                onNetChangeListener.onWifi();
            } else if (!isAllowModible && NetworkUtils.getNetworkType(activity) > 3
                    && NetworkUtils.getNetworkType(activity) < 7) {// 网络不是手机网络或者是以太网
                // TODO 更新状态是暂停状态
                statusChange(PlayStateParams.STATE_PAUSED);
                mVideoView.pause();
                currentPosition = mVideoView.getCurrentPosition();
                onPause();
                loading.setVisibility(View.GONE);
//                onNetChangeListener.onMobile();
                mVideoNetTie.setVisibility(View.VISIBLE);

            } else if (NetworkUtils.getNetworkType(activity) == 1) {// 网络链接断开
                Toast.makeText(mContext, "网路已断开", Toast.LENGTH_SHORT).show();
                onPause();
//                onNetChangeListener.onDisConnect();
            } else {
                Toast.makeText(mContext, "未知网络", Toast.LENGTH_SHORT).show();
//                onNetChangeListener.onNoAvailable();
            }

        }

    }


}