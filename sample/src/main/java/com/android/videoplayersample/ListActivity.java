package com.android.videoplayersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

/**
 * Created by Berkeley on 10/31/16.
 */
public class ListActivity extends FragmentActivity{
    private final static String TAG="home";

    private FrameLayout flContent;
    private VideoFragment videoFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listvideo);
        if (savedInstanceState==null) {
            videoFragment = new VideoFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_content, videoFragment, TAG);
            ft.commit();
        }


    }

    @Override
    protected void onResume() {

        Fragment fragment=getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment==null)
        {
            fragment.onResume();
        }
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment=getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment!=null)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
