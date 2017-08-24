package com.ebrightmoon.aplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_surfaceview).setOnClickListener(this);
        findViewById(R.id.videoview).setOnClickListener(this);
        findViewById(R.id.fullscreen).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        int i = view.getId();
        if (i == R.id.btn_surfaceview) {
            intent = new Intent(getApplicationContext(), LiveActivity.class);
            startActivity(intent);
        } else if (i == R.id.videoview) {
            intent = new Intent(getApplicationContext(), VideoViewActivity.class);
            startActivity(intent);
        }else if (i==R.id.fullscreen)
        {
            intent = new Intent(getApplicationContext(), IjkFullscreenActivity.class);
            startActivity(intent);
        }


    }


}
