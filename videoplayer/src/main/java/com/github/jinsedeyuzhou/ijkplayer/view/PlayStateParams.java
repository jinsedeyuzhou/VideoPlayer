package com.github.jinsedeyuzhou.ijkplayer.view;

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
    播放出错
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


    public static final int MESSAGE_SHOW_PROGRESS = 1;
    public static final int MESSAGE_FADE_OUT = 2;
    public  static final int MESSAGE_SEEK_NEW_POSITION = 3;
    public  static final int MESSAGE_HIDE_CENTER_BOX = 4;
    public  static final int MESSAGE_RESTART_PLAY = 5;


}
