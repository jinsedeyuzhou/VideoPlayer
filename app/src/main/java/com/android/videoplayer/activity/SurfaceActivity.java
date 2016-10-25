package com.android.videoplayer.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.android.videoplayer.R;
import com.android.videoplayer.view.VPlayView;

/**
 * Created by Berkeley on 10/24/16.
 */
public class SurfaceActivity extends FragmentActivity {

    private VPlayView viewById;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        viewById = (VPlayView) findViewById(R.id.videoView);
    }
}
