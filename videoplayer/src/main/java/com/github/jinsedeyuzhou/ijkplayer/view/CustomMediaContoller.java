package com.github.jinsedeyuzhou.ijkplayer.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.media.IMediaController;
import com.github.jinsedeyuzhou.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 *
 */
public class CustomMediaContoller implements IMediaController {

    private Context mContext;
    private View view;
    private View contollerbar;
    private IjkVideoView mVideoView;


//    private static final int SET_VIEW_HIDE = 1;
//    private static final int TIME_OUT = 5000;
//    private static final int MESSAGE_SHOW_PROGRESS = 2;
//    private static final int PAUSE_IMAGE_HIDE = 3;
//    private static final int MESSAGE_SEEK_NEW_POSITION = 4;
//    private static final int MESSAGE_HIDE_CONTOLL = 5;

    //是否展示
    private boolean isShow;
    //是否拖动
    private boolean isDragging;
    //是否显示控制bar
    private boolean isShowContoller;

    private boolean isSound;
    private final AudioManager audioManager;

    //初始化view
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView allTime;
    private TextView time;
    private ImageView full;
    private ImageView sound;
    private ImageView play;
    private ImageView pauseImage;
    private LinearLayout brightness_layout;
    private VSeekBar brightness_seek;
    private LinearLayout sound_layout;
    private VSeekBar sound_seek;
    private RelativeLayout show;
    private TextView seekTxt;
    private Bitmap bitmap;
    private GestureDetector detector;
    private final View toolbar;
    private LinearLayout top_box;



    private int volume = -1;
    private float brightness = -1;
    private long newPosition = 1;
    private int mMaxVolume;
    private long duration;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PlayStateParams.SET_VIEW_HIDE:
                    isShow = false;
                    contollerbar.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PlayStateParams.PAUSE_IMAGE_HIDE:
                    pauseImage.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_HIDE_CONTOLL:
                    seekTxt.setVisibility(View.GONE);
                    brightness_layout.setVisibility(View.GONE);
                    sound_layout.setVisibility(View.GONE);
                    break;
            }
        }
    };


    public CustomMediaContoller(Context context, View view) {

        this.view = view;
        contollerbar = view.findViewById(R.id.media_contoller);
        this.mVideoView = (IjkVideoView) view.findViewById(R.id.main_video);
        toolbar = view.findViewById(R.id.player_toolbar);
        toolbar.setVisibility(View.GONE);
        contollerbar.setVisibility(View.GONE);
        isShow = false;
        isDragging = false;
        isShow = false;
        isDragging = false;
        isShowContoller = true;
        this.mContext = context;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initView();
        initAction();

    }

    private void initView() {

        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        seekBar = (SeekBar) contollerbar.findViewById(R.id.seekbar);
        allTime = (TextView) contollerbar.findViewById(R.id.all_time);
        time = (TextView) contollerbar.findViewById(R.id.time);
        full = (ImageView) contollerbar.findViewById(R.id.full);
        sound = (ImageView) contollerbar.findViewById(R.id.sound);
        play = (ImageView) contollerbar.findViewById(R.id.player_btn);
        pauseImage = (ImageView) view.findViewById(R.id.pause_image);

        brightness_layout = (LinearLayout) view.findViewById(R.id.brightness_layout);
        brightness_seek = (VSeekBar) view.findViewById(R.id.brightness_seek);
        sound_layout = (LinearLayout) view.findViewById(R.id.sound_layout);
        sound_seek = (VSeekBar) view.findViewById(R.id.sound_seek);
        show = (RelativeLayout) view.findViewById(R.id.show);
        seekTxt = (TextView) view.findViewById(R.id.seekTxt);


        top_box = (LinearLayout) toolbar.findViewById(R.id.app_video_top_box);
    }

    private void initAction() {

        sound_seek.setEnabled(true);
        brightness_seek.setEnabled(true);
        isSound = true;
        detector = new GestureDetector(mContext, new PlayGestureListener());
        mMaxVolume = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                time.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                mVideoView.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isDragging = false;
                handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detector.onTouchEvent(event))
                    return true;

                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });

        contollerbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("custommedia", "event");

                Rect seekRect = new Rect();
                seekBar.getHitRect(seekRect);


                if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {

                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return seekBar.onTouchEvent(me);

                }
                return false;
            }
        });

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.e("setOnInfoListener", what + "");
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        if (progressBar.getVisibility() == View.GONE)
                            progressBar.setVisibility(View.VISIBLE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                        statusChange(STATUS_PLAYING);
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                isSound = !isSound;
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    pause();
                } else {
                    reStart();
                }
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("full", "full");
                if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

        });

    }


    public void start() {
        pauseImage.setVisibility(View.GONE);
        contollerbar.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        play.setImageResource(R.mipmap.video_stop_btn);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void pause() {
        play.setImageResource(R.mipmap.video_play_btn);
        mVideoView.pause();
        bitmap = mVideoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            pauseImage.setVisibility(View.VISIBLE);
        }
    }

    public void reStart() {
        play.setImageResource(R.mipmap.video_stop_btn);
        mVideoView.start();
        if (bitmap != null) {
            handler.sendEmptyMessageDelayed(PlayStateParams.PAUSE_IMAGE_HIDE, 100);
            bitmap.recycle();
            bitmap = null;
//                        pauseImage.setVisibility(View.GONE);
        }
    }

    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        contollerbar.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
    }

    public int getScreenOrientation(Activity activity) {
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


    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public void setVisiable() {
        show();
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime((long) (duration * seekBar.getProgress() * 1.0f / 100));
        time.setText(string);
        return position;
    }


    private VedioIsPause vedioIsPause;

    public interface VedioIsPause {
        void pause(boolean pause);
    }

    public void setPauseImageHide() {
        pauseImage.setVisibility(View.GONE);
    }

    public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean seek;

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            //横屏下拦截事件
            if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return true;
            }else {
                return super.onDown(e);
            }
        }

//        /**
//         * 单击
//         *
//         * @param e
//         * @return
//         */
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//          show();
//            return true;
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = e1.getX() - e2.getX();
            float y = e1.getY() - e2.getY();
            if (firstTouch) {
                seek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = e1.getX() < view.getMeasuredWidth() * 0.5;
                firstTouch = false;
            }

            if (seek) {
                onProgressSlide(-x / view.getWidth(),e1.getX()/view.getWidth());
            } else {
                float percent = y / view.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);


        }


    }
    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            contollerbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
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
        handler.sendEmptyMessageDelayed(PlayStateParams.SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        if (!isShowContoller)
            return;
        isShow = true;
        progressBar.setVisibility(View.GONE);
        contollerbar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        show(PlayStateParams.TIME_OUT);
    }

    @Override
    public void showOnce(View view) {

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
        handler.removeMessages(PlayStateParams.MESSAGE_HIDE_CONTOLL);
        handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_HIDE_CONTOLL, 500);

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
//        hide();

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        if (sound_layout.getVisibility()==View.GONE)
            sound_layout.setVisibility(View.VISIBLE);
        // 变更进度条
        int i = (int) (index * 1.0f / mMaxVolume * 100);
        if (i == 0) {
            sound.setImageResource(R.mipmap.sound_mult_icon);
        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
        }
        sound_seek.setProgress(i);
    }

    /**
     * 滑动跳转
     *
     * @param percent 移动比例
     * @param downPer 按下比例
     */
    private void onProgressSlide(float percent,float downPer) {
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
        Log.e("showdelta", ((downPer +percent)*100) + "");
        if (showDelta != 0) {
            if (seekTxt.getVisibility()==View.GONE)
                seekTxt.setVisibility(View.VISIBLE);
            String current=generateTime(newPosition);
            seekTxt.setText(current+"/"+allTime.getText());
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        if (brightness_layout.getVisibility()==View.GONE)
            brightness_layout.setVisibility(View.VISIBLE);

        brightness_seek.setProgress((int) (lpa.screenBrightness * 100));
        ((Activity) mContext).getWindow().setAttributes(lpa);

    }
}
