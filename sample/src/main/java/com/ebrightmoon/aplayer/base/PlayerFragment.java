package com.ebrightmoon.aplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuxuan.common.base.BaseFragment;
import com.yuxuan.common.util.LogUtils;


public abstract class PlayerFragment
        extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "PlayerFragment";
    protected Context ct;
    protected View rootView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ct = getActivity();
    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        rootView = initView(inflater, container);
        return rootView;
    }
    protected abstract View initView(LayoutInflater inflater,
                                     ViewGroup container);

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void processClick(View v);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:

                break;

            default:
                break;
        }
        processClick(v);
    }

}
