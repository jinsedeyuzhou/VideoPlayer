package com.github.jinsedeyuzhou.ijkplayer.play;

/**
 * Created by Berkeley on 10/26/16.
 */
public class PlayStateParams {
    /**
     * fitParent:scale the video uniformly (maintain the video's aspect ratio) so that both dimensions (width and height) of the video will be equal to or **less** than the corresponding dimension of the view. like ImageView's `CENTER_INSIDE`.等比缩放,画面填满view。
     */
    public static final String SCALETYPE_FITPARENT = "fitParent";
    /**
     * fillParent:scale the video uniformly (maintain the video's aspect ratio) so that both dimensions (width and height) of the video will be equal to or **larger** than the corresponding dimension of the view .like ImageView's `CENTER_CROP`.等比缩放,直到画面宽高都等于或小于view的宽高。
     */
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    /**
     * wrapContent:center the video in the view,if the video is less than view perform no scaling,if video is larger than view then scale the video uniformly so that both dimensions (width and height) of the video will be equal to or **less** than the corresponding dimension of the view. 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中。
     */
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    /**
     * fitXY:scale in X and Y independently, so that video matches view exactly.不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY = "fitXY";
    /**
     * 16:9:scale x and y with aspect ratio 16:9 until both dimensions (width and height) of the video will be equal to or **less** than the corresponding dimension of the view.不剪裁,非等比例拉伸画面到16:9,并完全显示在View中。
     */
    public static final String SCALETYPE_16_9 = "16:9";
    /**
     * 4:3:scale x and y with aspect ratio 4:3 until both dimensions (width and height) of the video will be equal to or **less** than the corresponding dimension of the view.不剪裁,非等比例拉伸画面到4:3,并完全显示在View中。
     */
    public static final String SCALETYPE_4_3 = "4:3";


    /**
     * 播放出错
     */
    public static final int STATE_ERROR = -1;
    /**
     * 空闲
     */
    public static final int STATE_IDLE = 0;
    /**
     * 准备播放中
     */
    public static final int STATE_PREPARING = 1;
    /**
     * 准备完成
     */
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     */
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停
     */
    public static final int STATE_PAUSED = 4;
    /**
     * 完成
     */
    public static final int STATE_PLAYBACK_COMPLETED = 5;


    public static final int MESSAGE_SHOW_PROGRESS = 201;
    public static final int MESSAGE_FADE_OUT = 202;
    public static final int MESSAGE_SEEK_NEW_POSITION = 203;
    public static final int MESSAGE_HIDE_CENTER_BOX = 204;
    public static final int MESSAGE_RESTART_PLAY = 205;
    public static final int MESSAGE_HIDE_NETWORK = 206;
    public static final int MESSAGE_UPDATE_PAUSE_ONRESUME = 207;
    /**
     * =====================视频质量=======================================
     */
    // 默认显示/隐藏选择分辨率界面时间
    public static final int DEFAULT_QUALITY_TIME = 300;
    /**
     * 依次分别为：流畅、清晰、高清、超清和1080P
     */
    public static final int MEDIA_QUALITY_SMOOTH = 100;
    public static final int MEDIA_QUALITY_MEDIUM = 101;
    public static final int MEDIA_QUALITY_HIGH = 102;
    public static final int MEDIA_QUALITY_SUPER = 103;
    public static final int MEDIA_QUALITY_BD = 104;


    /**
     * ====================ijk状态码表 开始=================================
     */
    /*
     * Do not change these values without updating their counterparts in native
     */
    // 无效变量
    public static final int INVALID_VALUE = -1;
    public static final int MEDIA_INFO_UNKNOWN = 1;//未知信息
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;//播放下一条
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频开始整备中
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;//视频日志跟踪
    public static final int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲中
    public static final int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
    public static final int MEDIA_INFO_BUFFERING_BYTES_UPDATE = 503;//网速方面
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;//网络带宽，网速方面
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;//
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;//不可设置播放位置，直播方面
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;//
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;//不支持字幕
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;//字幕超时

    public static final int MEDIA_INFO_VIDEO_INTERRUPT = -10000;//数据连接中断
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频方向改变
    public static final int MEDIA_INFO_AUDIO_RENDERING_START = 10002;//音频开始整备中

    public static final int MEDIA_ERROR_UNKNOWN = 1;//未知错误
    public static final int MEDIA_ERROR_SERVER_DIED = 100;//服务挂掉
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
    public static final int MEDIA_ERROR_IO = -1004;//IO错误
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;//数据不支持
    public static final int MEDIA_ERROR_TIMED_OUT = -110;//数据超时
    /**====================ijk状态码表 结束=================================*/

    /**============================ 弹幕 ====================================*/
    /**
     * 视频编辑状态：正常未编辑状态、在播放时编辑、暂停时编辑
     */
    public static final int NORMAL_STATUS = 501;
    public static final int INTERRUPT_WHEN_PLAY = 502;
    public static final int INTERRUPT_WHEN_PAUSE = 503;


    // 弹幕格式：B站、A站和自定义
    public static final int DANMAKU_TAG_BILI = 701;
    public static final int DANMAKU_TAG_ACFUN = 702;
    public static final int DANMAKU_TAG_CUSTOM = 703;

}
