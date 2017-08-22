package com.ebrightmoon.aplayer.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebrightmoon.aplayer.R;
import com.yuxuan.common.base.BaseFragment;


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
