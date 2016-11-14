package com.github.jinsedeyuzhou.ijkplayer.view;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.media.IMediaController;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 * <p/>
 * 两种方式控制屏幕变化
 * 1、设置view的宽高
 * 传递布局参数，设置布局参数
 * <p/>
 * 2、设置父view的宽高
 * 在用的activity中更改添加布局的宽高
 */
public class CustomMediaContoller implements IMediaController {

    private static final String TAG = "CustomMediaContoller";
    private Context mContext;
    private Activity activity;
    private View rootView;
    private final View controlbar;
    //    private final View toolbar;
    private final View gesture;
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
    private final int mMaxVolume;
    private final AudioManager audioManager;

    //是否显示
    private boolean isShowing;
    //是否是竖屏
    private boolean portrait;
    //亮度
    private float brightness = -1;
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

    private boolean isLive = false;//是否为直播
    private boolean hidden; //暂停时不隐藏
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
    private LinearLayout volume_box;
    private LinearLayout brightness_box;
    private LinearLayout fastForward_box;
    private TextView fastForward;
    private TextView fastForward_target;
    private TextView fastForward_all;
    private ImageView volume_icon;
    private TextView video_volume;
    private TextView video_brightness;
    private TextView topTitle;
    private long pauseTime;

    public int getStatus() {
        return status;
    }

    private boolean playerSupport;


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
//                    qr.id(R.id.app_video_volume_box).gone();
                    volume_box.setVisibility(View.GONE);
//                    qr.id(R.id.app_video_brightness_box).gone();
                    brightness_box.setVisibility(View.GONE);
//                    qr.id(R.id.app_video_fastForward_box).gone();
                    fastForward_box.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShowing) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                        updatePausePlay();
                    }
                    break;
                case PlayStateParams.MESSAGE_RESTART_PLAY:
//                    play(url);
                    break;
            }
        }
    };
    private LinearLayout mVideoStaus;
    private TextView mStatusText;


    public CustomMediaContoller(Context context, View rootView) {
        this.mContext = context;
        activity = (Activity) context;
        this.rootView = rootView;
        this.playerSupport = true;

        //播放控制
        controlbar = rootView.findViewById(R.id.player_controlbar);
        //toolbar
//        toolbar = rootView.findViewById(R.id.player_toolbar);
        //触摸上下亮度和音量
        gesture = rootView.findViewById(R.id.player_touch_gesture);

//        initHeight = rootView.findViewById(R.id.app_video_box).getHeight();
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        initView();
        initAction();
    }

    private void initView() {
        //屏幕宽度
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        //布局高度


        mVideoView = (IjkVideoView) rootView.findViewById(R.id.video_view);
        mVideoReplay = (ImageView) rootView.findViewById(R.id.app_video_replay_icon);

        liveBox = rootView.findViewById(R.id.app_video_box);
        //进度条
        loading = (ProgressBar) rootView.findViewById(R.id.app_video_loading);
        //控制播放
        mVideoPlay = (ImageView) rootView.findViewById(R.id.app_video_play);
        mVideoFullscreen = (ImageView) rootView.findViewById(R.id.app_video_fullscreen);
        seekBar = (SeekBar) rootView.findViewById(R.id.app_video_seekBar);
        currentTime = (TextView) rootView.findViewById(R.id.app_video_currentTime);
        endTime = (TextView) rootView.findViewById(R.id.app_video_endTime);

        //音量 亮度 快进
        volume_box = (LinearLayout) rootView.findViewById(R.id.app_video_volume_box);
        brightness_box = (LinearLayout) rootView.findViewById(R.id.app_video_brightness_box);
        fastForward_box = (LinearLayout) rootView.findViewById(R.id.app_video_fastForward_box);


        fastForward = (TextView) rootView.findViewById(R.id.app_video_fastForward);
        fastForward_target = (TextView) rootView.findViewById(R.id.app_video_fastForward_target);
        fastForward_all = (TextView) rootView.findViewById(R.id.app_video_fastForward_all);

        volume_icon = (ImageView) rootView.findViewById(R.id.app_video_volume_icon);
        video_volume = (TextView) rootView.findViewById(R.id.app_video_volume);
        video_brightness = (TextView) rootView.findViewById(R.id.app_video_brightness);


//        $.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");

//        $.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
////        $.id(R.id.app_video_brightness_box).gone();
////        $.id(R.id.app_video_volume_box).visible();
////        $.id(R.id.app_video_volume_box).visible();
//        $.id(R.id.app_video_volume).text(s).visible();


        //topbox
        top_box = (LinearLayout) rootView.findViewById(R.id.app_video_top_box);
        mVideoFinish = (ImageView) rootView.findViewById(R.id.app_video_finish);
        topTitle = (TextView) rootView.findViewById(R.id.app_video_title);

        //status
        mVideoStaus = (LinearLayout) rootView.findViewById(R.id.app_video_status);
        mStatusText = (TextView) rootView.findViewById(R.id.app_video_status_text);


    }

    private void initAction() {
        mVideoFinish.setOnClickListener(onClickListener);
        mVideoFullscreen.setOnClickListener(onClickListener);
        mVideoReplay.setOnClickListener(onClickListener);
        mVideoPlay.setOnClickListener(onClickListener);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());


        liveBox.setClickable(true);
        liveBox.setOnTouchListener(new View.OnTouchListener() {
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

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(PlayStateParams.STATE_PLAYBACK_COMPLETED);
//                oncomplete.run();
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(PlayStateParams.STATE_ERROR);
//                onErrorListener.onError(what, extra);
                return true;
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(PlayStateParams.STATE_PREPARING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示 下载速度
//                        Toaster.show("download rate:" + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;
                }
//                onInfoListener.onInfo(what, extra);
                return false;
            }
        });


        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
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

        hideAll();
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support));
        }
    }

    public boolean onBackPressed() {
        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }


    public void play(String url) {
        if (playerSupport) {
//            qr.id(R.id.app_video_loading).visible();
            loading.setVisibility(View.VISIBLE);
            mVideoView.setVideoPath(url);
            mVideoView.start();
        }
    }

    public void start() {
        mVideoView.start();
    }

    public void setTopTitle(String str) {
        topTitle.setText(str);
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
        }
        statusChange(PlayStateParams.STATE_PAUSED);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy" + status);
        orientationEventListener.disable();
        handler.removeCallbacksAndMessages(null);
        mVideoView.stopPlayback();
    }

    public void onResume() {
        Log.d(TAG, "onResume" + status);

        pauseTime = 0;
        if (status == PlayStateParams.STATE_PAUSED) {
            if (isLive) {
                mVideoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    mVideoView.seekTo(currentPosition);
                }
            }
            mVideoView.start();
        }
        statusChange(PlayStateParams.STATE_PLAYING);
    }

    /**
     * @param isLive 设置是否直播
     */
    public void isLive(boolean isLive) {
        this.isLive = isLive;
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

    /**
     * 右上角图片
     *
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        mVideoFinish.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void updatePausePlay() {
        if (mVideoView.isPlaying()) {
//            qr.id(R.id.app_video_play).image(R.drawable.ic_stop_white_24dp);
            mVideoPlay.setImageResource(R.drawable.ic_stop_white_24dp);
            hidden = false;
        } else {
//            qr.id(R.id.app_video_play).image(R.drawable.ic_play_arrow_white_24dp);
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
//            qr.id(R.id.app_video_replay).gone();
            mVideoReplay.setVisibility(View.GONE);
            mVideoView.seekTo(0);
            mVideoView.start();
        } else if (mVideoView.isPlaying()) {
            statusChange(PlayStateParams.STATE_PAUSED);
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
        updatePausePlay();
    }


    private void statusChange(int newStatus) {
        status = newStatus;
        if (!isLive && newStatus == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            hideAll();
            mVideoReplay.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_ERROR) {
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            hideAll();
            if (isLive) {
                showStatus(activity.getResources().getString(R.string.small_problem));
                if (defaultRetryTime > 0) {
                    handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_RESTART_PLAY, defaultRetryTime);
                }
            } else {
                showStatus(activity.getResources().getString(R.string.small_problem));
            }
        } else if (newStatus == PlayStateParams.STATE_PREPARING) {
            hideAll();
            loading.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_PLAYING) {
            hideAll();
        }

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.app_video_fullscreen) {
                toggleFullScreen();
            } else if (v.getId() == R.id.app_video_play) {
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.app_video_replay_icon) {
                mVideoView.seekTo(0);
                mVideoView.start();
                doPauseResume();
            } else if (v.getId() == R.id.app_video_finish) {
                if (!fullScreenOnly && !portrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            }
        }
    };

    /**
     * 方法一
     *
     * @param portrait
     */
    public void doOnConfigurationChanged(final boolean portrait) {

        if (mVideoView != null && !fullScreenOnly) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        Log.v(TAG, "initHeight" + initHeight);
                        liveBox.getLayoutParams().height = initHeight;

                    } else {
                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                        liveBox.getLayoutParams().height = Math.min(heightPixels, widthPixels);
//                        liveBox.getLayoutParams().width=widthPixels;
                    }
                    updateFullScreenButton();
                }
            });
            orientationEventListener.enable();
        }
    }

//方法二
//    public void doOnConfigurationChanged(final boolean portrait) {
//        if (mVideoView != null) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tryFullScreen(!portrait);
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

    /**
     * is player support this device
     *
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }


    private void hideAll() {
        mVideoReplay.setVisibility(View.GONE);
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
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
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

    @Override
    public void hide() {
        Log.d(TAG, "hide:" + isShowing);

    }

    public void hide(boolean force) {

        if (force || isShowing) {
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            showBottomControl(false);
//            $.id(R.id.app_video_top_box).gone();
//            $.id(R.id.app_video_fullscreen).invisible();
//            toolbar.setVisibility(View.GONE);
            top_box.setVisibility(View.GONE);
            mVideoFullscreen.setVisibility(View.INVISIBLE);
            isShowing = false;
//            onControlPanelVisibilityChangeListener.change(false);
        }

    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {

    }

    @Override
    public void show(int timeout) {
        Log.d(TAG, "show timeout:" + isShowing);
        if (!isShowing) {
//            $.id(R.id.app_video_top_box).visible();
            top_box.setVisibility(View.VISIBLE);
            if (!isLive) {
                showBottomControl(true);
            }
            if (!fullScreenOnly) {
//                $.id(R.id.app_video_fullscreen).visible();
                mVideoFullscreen.setVisibility(View.VISIBLE);
            }
            isShowing = true;
//            onControlPanelVisibilityChangeListener.change(true);
        }
        updatePausePlay();
        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(PlayStateParams.MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.MESSAGE_FADE_OUT), timeout);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void showOnce(View view) {

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
        // 显示
        volume_icon.setImageResource(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
//        $.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
//        $.id(R.id.app_video_brightness_box).gone();
        brightness_box.setVisibility(View.GONE);
//        $.id(R.id.app_video_volume_box).visible();
        volume_box.setVisibility(View.VISIBLE);
//        $.id(R.id.app_video_volume_box).visible();

        video_volume.setText(s);
        video_volume.setVisibility(View.VISIBLE);
//        $.id(R.id.app_video_volume).text(s).visible();
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
//            $.id(R.id.app_video_fastForward_box).visible();
            fastForward_box.setVisibility(View.VISIBLE);
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            $.id(R.id.app_video_fastForward).text(text + "s");
            fastForward.setText(text + "s");
//            $.id(R.id.app_video_fastForward_target).text(generateTime(newPosition) + "/");
            fastForward_target.setText(generateTime(newPosition) + "/");
//            $.id(R.id.app_video_fastForward_all).text(generateTime(duration));
            fastForward_all.setText(generateTime(duration));
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
//        $.id(R.id.app_video_brightness_box).visible();
        brightness_box.setVisibility(View.VISIBLE);
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
//        $.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
        video_brightness.setText(((int) (lpa.screenBrightness * 100)) + "%");
        activity.getWindow().setAttributes(lpa);

    }

    /**
     * 设置seekbar进度
     *
     * @return
     */
    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;
//        $.id(R.id.app_video_currentTime).text(generateTime(position));
        currentTime.setText(generateTime(position));
//        $.id(R.id.app_video_endTime).text(generateTime(this.duration));
        endTime.setText(generateTime(this.duration));
        return position;
    }

//    public void hide(boolean force) {
//        if (force || isShowing) {
//            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
//            showBottomControl(false);
////            $.id(R.id.app_video_top_box).gone();
////            top_box.setVisibility(View.GONE);
////            $.id(R.id.app_video_fullscreen).invisible();
//            mVideoFullscreen.setVisibility(View.INVISIBLE);
//            isShowing = false;
////            onControlPanelVisibilityChangeListener.change(false);
//        }
//    }
}
