package com.github.jinsedeyuzhou.ijkplayer.play;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Berkeley on 10/26/16.
 */
public class Query {
    private  Activity activity;
    private  Context context;
    private View view;
    private View rootView;

    /**拓展播放器view的方法使用*/
    public Query(Context context, View view) {
        this.context = context;
        this.rootView = view;
    }
    public Query(Activity activity) {
        this.activity=activity;
        this.context=activity;
    }

    public Query id(int id) {
        if (null!=rootView)
        {
            view=rootView.findViewById(id);
        }
        else
        {
            view = activity.findViewById(id);
        }

        return this;
    }

    public Query image(int resId) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(resId);
        }
        return this;
    }

    public Query visible() {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public Query gone() {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public Query invisible() {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public Query clicked(View.OnClickListener handler) {
        if (view != null) {
            view.setOnClickListener(handler);
        }
        return this;
    }

    public Query text(CharSequence text) {
        if (view!=null && view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return this;
    }

    public Query visibility(int visible) {
        if (view != null) {
            view.setVisibility(visible);
        }
        return this;
    }

    private void size(boolean width, int n, boolean dip){

        if(view != null){

            ViewGroup.LayoutParams lp = view.getLayoutParams();


            if(n > 0 && dip){
                n = dip2pixel(activity, n);
            }

            if(width){
                lp.width = n;
            }else{
                lp.height = n;
            }

            view.setLayoutParams(lp);

        }

    }

    public void height(int height, boolean dip) {
        size(false,height,dip);
    }

    public int dip2pixel(Context context, float n){
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
        return value;
    }

    public float pixel2dip(Context context, float n){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = n / (metrics.densityDpi / 160f);
        return dp;

    }
}