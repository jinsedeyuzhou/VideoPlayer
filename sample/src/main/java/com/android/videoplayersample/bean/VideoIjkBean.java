package com.android.videoplayersample.bean;

import java.io.Serializable;

/**
 * Created by Berkeley on 10/26/16.
 */


public class VideoIjkBean implements Serializable {
    /**
     * id
     */
    private int id;

    /**
     * 视频地址
     */
    private String streamUrl;

    /**
     * 屏幕缩放类型
     */
    private String scaleType;

    /**
     * 是否仅仅为全屏
     */
    private boolean fullScreenOnly;
    /**
     * 默认重复请求时间
     */
    private long defaultRetryTime = 5 * 1000;
    /**
     * titie
     */
    private String title;
    private boolean showNavIcon = true;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    public boolean isFullScreenOnly() {
        return fullScreenOnly;
    }

    public void setFullScreenOnly(boolean fullScreenOnly) {
        this.fullScreenOnly = fullScreenOnly;
    }

    public long getDefaultRetryTime() {
        return defaultRetryTime;
    }

    public void setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowNavIcon() {
        return showNavIcon;
    }

    public void setShowNavIcon(boolean showNavIcon) {
        this.showNavIcon = showNavIcon;
    }





}

