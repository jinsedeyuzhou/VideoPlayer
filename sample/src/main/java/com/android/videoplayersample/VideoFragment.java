package com.android.videoplayersample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.videoplayersample.adapter.VideoAdapter;
import com.android.videoplayersample.bean.LiveBean;
import com.android.videoplayersample.net.ApiServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berkeley on 10/31/16.
 */
public class VideoFragment extends Fragment {
    private List<LiveBean> list;
    private View view;
    private ListView mListView;
    private VideoAdapter videoAdapter;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            videoAdapter = new VideoAdapter(getActivity(), getContext(), list,view);
            mListView.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                //这里多有得罪啦，网上找的直播地址，如有不妥之处，可联系删除
                list = ApiServiceUtils.getLiveList();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);


        mListView = (ListView) view.findViewById(R.id.listview_video);

        return view;
    }

//    private void initView() {
//
//        mListView = (ListView) view.findViewById(R.id.listview_video);
//        videoAdapter = new VideoAdapter(getActivity(), getContext(), list);
//        mListView.setAdapter(videoAdapter);
//    }


}
