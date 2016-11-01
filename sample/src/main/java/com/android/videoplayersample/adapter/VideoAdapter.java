package com.android.videoplayersample.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.videoplayersample.R;
import com.android.videoplayersample.bean.LiveBean;
import com.github.jinsedeyuzhou.ijkplayer.media.VPlayPlayer;

import java.util.List;

/**
 * Created by Berkeley on 10/31/16.
 */
public class VideoAdapter  extends BaseAdapter {
    private Context context;
    private Activity activity;
    private List<LiveBean> videoBeanList;
    private VPlayPlayer player;
    private View rootView;

    public VideoAdapter(Activity activity, Context ctx, List<LiveBean> videoBeanList,View rootView) {
        this.activity = activity;
        context = ctx;
        this.videoBeanList = videoBeanList;
        this.rootView=rootView;

    }

    @Override
    public int getCount() {
        return videoBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        System.out.println("LiveBean:"+videoBeanList.get(i).getName());
        view = LayoutInflater.from(context).inflate(R.layout.item_video, null);
        player = new VPlayPlayer(activity,view);

//        if (view == null) {
//            viewHolder = new ViewHolder();
//            view = LayoutInflater.from(context).inflate(R.layout.item_video, null);
//            player = new VPlayPlayer(activity,view);
//
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }
        player.setTitle(videoBeanList.get(i).getName());
        player.setUrl(videoBeanList.get(i).getStream_addr());
        player.setShowNavIcon(false);


        return view;
    }


    final static class ViewHolder {


    }
}
