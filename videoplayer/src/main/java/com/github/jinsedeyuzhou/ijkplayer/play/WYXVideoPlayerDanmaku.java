package com.github.jinsedeyuzhou.ijkplayer.play;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.jinsedeyuzhou.ijkplayer.R;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.BiliDanmukuParser;
import com.github.jinsedeyuzhou.ijkplayer.danmaku.DanamakuAdapter;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by wyy on 2017/4/16.
 */

public class WYXVideoPlayerDanmaku extends WYXVideoPlayer {
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;//解析器对象
    private long mDanmakuStartSeekPosition = -1;
    private boolean mDanmaKuShow = true;


    public WYXVideoPlayerDanmaku(Context context) {
        super(context);
        initDanmaku();
    }


    public WYXVideoPlayerDanmaku(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDanmaku();
    }

    public WYXVideoPlayerDanmaku(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDanmaku();
    }


    @Override
    public int getLayoutId() {
        return R.layout.danmaku_player;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return super.onTouch(v, event);
    }

    private void initDanmaku() {
        mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);
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
                .setCacheStuffer(new SpannedCacheStuffer(), danamakuAdapter) // 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        if (mDanmakuView != null) {
            //todo 替换成你的数据流
            mParser = createParser(this.getResources().openRawResource(R.raw.comments));
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
                    if (mDanmakuView != null) {
                        mDanmakuView.start();
//                        if (getDanmakuStartSeekPosition() != -1) {
//                            resolveDanmakuSeek(DanmakuVideoPlayer.this, getDanmakuStartSeekPosition());
//                            setDanmakuStartSeekPosition(-1);
//                        }
//                        resolveDanmakuShow();
                    }
                }
            });
            mDanmakuView.enableDanmakuDrawingCache(true);
            startDanmakuView();
        }


    }

    /**
     * 播放弹幕
     */
    public void startDanmakuView() {
        if (mDanmakuView != null && !mDanmakuView.isPrepared()) {
            mDanmakuView.prepare(mParser, mDanmakuContext);
        }
    }

    /**
     * 释放弹幕控件
     */
    private void releaseDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
        }
    }

    /**
     * 创建解析器对象
     *
     * @param stream
     */
    public void setDanmakuCustomParser(InputStream stream) {
        if (mDanmakuView != null)
            mParser = createParser(stream);
    }

    /**
     * 弹幕的显示与关闭
     */
    private void resolveDanmakuShow() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mDanmaKuShow) {
                    if (!getDanmakuView().isShown())
                        getDanmakuView().show();
                } else {
                    if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }
                }
            }
        });
    }

    /**
     * 创建解析器对象，解析输入流
     *
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    /**
     * 发送一条只有内容的弹幕
     *
     * @param text
     * @param islive
     */
    public void addDanmaku(String text, boolean islive) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        // for(int i=0;i<100;i++){
        // }
        danmaku.text = text + System.nanoTime();
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);

    }

    /**
     * 图文混排
     *
     * @param drawable
     * @param islive
     */
    public void addDanmaKuShowTextAndImage(Drawable drawable, boolean islive) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }


    private void pauseDanmaku() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    private void resumeDanmaku() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    private void destoryDanmaku() {
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public BaseDanmakuParser getParser() {
        return mParser;
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


}
