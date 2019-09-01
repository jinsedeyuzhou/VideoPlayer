package com.github.jinsedeyuzhou.ijkplayer.adapter;


import com.github.jinsedeyuzhou.ijkplayer.play.EMPlayerStandard;

/**
 * Created by Rukey7 on 2016/10/29.
 */

public final class MediaQualityInfo {

    private int index;
    private String desc;
    private boolean isSelect;

    public MediaQualityInfo(@EMPlayerStandard.MediaQuality int index, String desc, boolean isSelect) {
        this.index = index;
        this.desc = desc;
        this.isSelect = isSelect;
    }

    public @EMPlayerStandard.MediaQuality
    int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "MediaQualityInfo{" +
                "index=" + index +
                ", desc='" + desc + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }
}
