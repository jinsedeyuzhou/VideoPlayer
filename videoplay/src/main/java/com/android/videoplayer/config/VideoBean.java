package com.android.videoplayer.config;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Berkeley on 10/26/16.
 */


public class VideoBean implements Parcelable {
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





    private VideoBean(Parcel in) {
        id=in.readInt();
        scaleType = in.readString();
        fullScreenOnly = in.readByte() != 0;
        defaultRetryTime = in.readLong();
        title = in.readString();
        showNavIcon = in.readByte() != 0;
        streamUrl=in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(scaleType);
        dest.writeByte((byte) (fullScreenOnly ? 1 : 0));
        dest.writeLong(defaultRetryTime);
        dest.writeString(title);
        dest.writeString(streamUrl);
        dest.writeByte((byte) (showNavIcon ? 1 : 0));
    }

    public static final Parcelable.Creator<VideoBean> CREATOR = new Parcelable.Creator<VideoBean>() {
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };
}

