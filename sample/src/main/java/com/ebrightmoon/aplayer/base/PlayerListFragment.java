package com.ebrightmoon.aplayer.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.videoplayersample.R;


public abstract class PlayerListFragment
        extends BaseFragment
{
    protected void initData(Bundle paramBundle) {}

    protected View initView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup)
    {
        return paramLayoutInflater.inflate(R.layout.fragment_container, paramViewGroup, false);
    }

    protected void processClick(View paramView) {}
}
