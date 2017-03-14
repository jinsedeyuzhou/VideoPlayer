package com.github.jinsedeyuzhou.ijkplayer.danmaku;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

/**
 * Created by Berkeley on 3/8/17.
 * 绘制背景(自定义弹幕样式)
 */

public class BackgroundCacheStuffer extends SpannedCacheStuffer {
    // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
    final Paint paint = new Paint();

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        danmaku.padding = 10;  // 在背景绘制模式下增加padding
        super.measure(danmaku, paint, fromWorkerThread);
    }

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        paint.setColor(0x8125309b);
        canvas.drawRect(left + 2, top + 2, left + danmaku.paintWidth - 2, top + danmaku.paintHeight - 2, paint);
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
        // 禁用描边绘制
    }
}
