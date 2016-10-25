package com.android.videoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.videoplayer.R;

/**
 * Created by Berkeley on 10/25/16.
 */
public class VPlayView extends View {
    private String TAG = "VPlayView";
    private RelativeLayout view;
    private Context mContext;

    public VPlayView(Context context) {
        super(context);
        initializeViews(context);
    }


    public VPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public VPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    private void initializeViews(Context context) {
        mContext=context;
        view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.giraffe_player, null);

    }
}
