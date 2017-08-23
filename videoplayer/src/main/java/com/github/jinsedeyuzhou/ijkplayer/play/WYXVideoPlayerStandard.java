package com.github.jinsedeyuzhou.ijkplayer.play;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.adapter.AdapterMediaQuality;
import com.github.jinsedeyuzhou.ijkplayer.adapter.MediaQualityInfo;
import com.github.jinsedeyuzhou.ijkplayer.media.IjkVideoView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.DEFAULT_QUALITY_TIME;
import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.MEDIA_QUALITY_BD;
import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.MEDIA_QUALITY_HIGH;
import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.MEDIA_QUALITY_MEDIUM;
import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.MEDIA_QUALITY_SMOOTH;
import static com.github.jinsedeyuzhou.ijkplayer.play.PlayStateParams.MEDIA_QUALITY_SUPER;

/**
 * Created by Berkeley on 8/23/17.
 */

public class WYXVideoPlayerStandard extends WYXVideoPlayer {
    private LinearLayout mContainerTools;
    private SeekBar mLandSeekBar;
    private TextView mSeparator;
    private View mPlaceHolder;
    private TextView mVideoLists;

    public WYXVideoPlayerStandard(Context context) {
        super(context);
    }


    public WYXVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        mLandSeekBar = (SeekBar) findViewById(R.id.app_land_seekBar);
        mSeparator = (TextView) findViewById(R.id.tv_separator);
        mPlaceHolder = findViewById(R.id.place_holder);
        mVideoLists = (TextView) findViewById(R.id.app_video_list);
        initMediaQuality();
        initMediaLists();
    }


    //==============================加载不同视频源==============================//


    // 保存Video Url
    private SparseArray<String> mVideoSource = new SparseArray<>();
    // 描述信息
    private String[] mMediaQualityDesc;
    // 分辨率选择布局
    private View mFlMediaQuality;
    // 清晰度
    private TextView mTvMediaQuality;
    // 分辨率选择列表
    private ListView mLvMediaQuality;
    // 分辨率选择列表适配器
    private AdapterMediaQuality mQualityAdapter;
    // 列表数据
    private List<MediaQualityInfo> mQualityData;
    // 是否显示分辨率选择列表
    private boolean mIsShowQuality = false;
    // 当前选中的分辨率
    private
    @MediaQuality
    int mCurSelectQuality = MEDIA_QUALITY_SMOOTH;

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    @IntDef({MEDIA_QUALITY_SMOOTH, MEDIA_QUALITY_MEDIUM, MEDIA_QUALITY_HIGH, MEDIA_QUALITY_SUPER, MEDIA_QUALITY_BD})
    public @interface MediaQuality {
    }

    private void initMediaQuality() {
        mMediaQualityDesc = getResources().getStringArray(R.array.media_quality);
        mFlMediaQuality = findViewById(R.id.fl_media_quality);
        mTvMediaQuality = (TextView) findViewById(R.id.app_video_quality);
        mTvMediaQuality.setOnClickListener(this);
        mLvMediaQuality = (ListView) findViewById(R.id.lv_media_quality);
        mQualityAdapter = new AdapterMediaQuality(mContext);
        mLvMediaQuality.setAdapter(mQualityAdapter);
        mLvMediaQuality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurSelectQuality != mQualityAdapter.getItem(position).getIndex()) {
                    setMediaQuality(mQualityAdapter.getItem(position).getIndex());
                    start();
                }
                toggleMediaQuality();
            }
        });

    }

    /**
     * 选择视频源
     *
     * @param quality 分辨率
     *                {@link #,#MEDIA_QUALITY_MEDIUM,#MEDIA_QUALITY_HIGH,#MEDIA_QUALITY_SUPER,#MEDIA_QUALITY_BD}
     * @return
     */
    public void setMediaQuality(@MediaQuality int quality) {
        if (mCurSelectQuality == quality || mVideoSource.get(quality) == null) {
            return;
        }
        mQualityAdapter.setMediaQuality(quality);
        mTvMediaQuality.setText(mMediaQualityDesc[quality]);
        mCurSelectQuality = quality;
        if (mVideoView.isPlaying()) {
            currentPosition = mVideoView.getCurrentPosition();
            mVideoView.release(false);
        }
        mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        setVideoPath(mVideoSource.get(quality));
    }

    /**
     * 设置视频源
     *
     * @param mediaSmooth 流畅
     * @param mediaMedium 清晰
     * @param mediaHigh   高清
     * @param mediaSuper  超清
     * @param mediaBd     1080P
     */
    public void setVideoSource(String mediaSmooth, String mediaMedium, String mediaHigh, String mediaSuper, String mediaBd) {
        boolean isSelect = true;
        mQualityData = new ArrayList<>();
        if (mediaSmooth != null) {
            mVideoSource.put(MEDIA_QUALITY_SMOOTH, mediaSmooth);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_SMOOTH, mMediaQualityDesc[MEDIA_QUALITY_SMOOTH], isSelect));
            mCurSelectQuality = MEDIA_QUALITY_SMOOTH;
            isSelect = false;
        }
        if (mediaMedium != null) {
            mVideoSource.put(MEDIA_QUALITY_MEDIUM, mediaMedium);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_MEDIUM, mMediaQualityDesc[MEDIA_QUALITY_MEDIUM], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_MEDIUM;
            }
            isSelect = false;
        }
        if (mediaHigh != null) {
            mVideoSource.put(MEDIA_QUALITY_HIGH, mediaHigh);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_HIGH, mMediaQualityDesc[MEDIA_QUALITY_HIGH], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_HIGH;
            }
            isSelect = false;
        }
        if (mediaSuper != null) {
            mVideoSource.put(MEDIA_QUALITY_SUPER, mediaSuper);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_SUPER, mMediaQualityDesc[MEDIA_QUALITY_SUPER], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_SUPER;
            }
            isSelect = false;
        }
        if (mediaBd != null) {
            mVideoSource.put(MEDIA_QUALITY_BD, mediaBd);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_BD, mMediaQualityDesc[MEDIA_QUALITY_BD], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_BD;
            }
        }
        mQualityAdapter.updateItems(mQualityData);
        mTvMediaQuality.setText(mMediaQualityDesc[mCurSelectQuality]);
        setVideoPath(mVideoSource.get(mCurSelectQuality));
    }


    public void toggleMediaQuality() {
        if (mFlMediaQuality.getVisibility() == GONE) {
            mFlMediaQuality.setVisibility(VISIBLE);
        }
        if (mIsShowQuality) {
            ViewCompat.animate(mFlMediaQuality).translationX(mFlMediaQuality.getWidth()).setDuration(DEFAULT_QUALITY_TIME);
            mIsShowQuality = false;
        } else {
            ViewCompat.animate(mFlMediaQuality).translationX(0).setDuration(DEFAULT_QUALITY_TIME);
            mIsShowQuality = true;
        }
    }
    //======================================================================//


    //=============================加载视频列表===============================//

    private void initMediaLists() {

    }

    //======================================================================//

    public void toggleQualityView(boolean isShow) {
        if (isShow) {
            mLandSeekBar.setVisibility(View.VISIBLE);
            mSeparator.setVisibility(View.VISIBLE);
            mVideoLists.setVisibility(View.VISIBLE);
            mTvMediaQuality.setVisibility(View.VISIBLE);
            mPlaceHolder.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.GONE);
        } else {
            mLandSeekBar.setVisibility(View.GONE);
            mSeparator.setVisibility(View.GONE);
            mVideoLists.setVisibility(View.GONE);
            mTvMediaQuality.setVisibility(View.GONE);
            mPlaceHolder.setVisibility(View.GONE);
            seekBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void doOnConfigurationChanged(boolean portrait) {
        super.doOnConfigurationChanged(portrait);
        toggleQualityView(!portrait);
    }

    @Override
    public boolean hideFrameLayout() {
        if (mIsShowQuality) {
            toggleMediaQuality();
            return true;
        }
        return false;
    }

    public int getLayoutId() {
        return R.layout.wxy_player_standard;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.app_video_quality) {
            if (!mIsShowQuality) {
                toggleMediaQuality();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }
}
