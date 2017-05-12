package com.android.videoplayersample.net;

import com.alibaba.fastjson.JSON;
import com.android.videoplayersample.bean.LiveBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 */
public class ApiServiceUtils {

    private static final String TAG = "ApiServiceUtils";

    public static List<LiveBean> getLiveList() {
        List<LiveBean> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.55.238.158/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService mApiServicePresenter = retrofit.create(ApiService.class);
        Call<String> call = mApiServicePresenter.lives("3000000000030010&cv=IK3.4.00_Android&cc=TG36008&ua=XiaomiMI5&uid=228032761&sid=202lSEzzUzSfALa0MctmHfzme7pKq5p7bn2Oi1Z6dfOKQQT1cNk&devi=861322035697784&imsi=460027380351530&imei=861322035697784&icc=898600e31614f1753512&conn=WIFI&vv=1.0.3-201610121413.android&aid=52f25a4c60eda7f1&osversion=android_23&proto=4&smid=Dun%2FEZ%2FI5fUAYraBm2dTqK37varDJXR92GZgiy%2F%2B4fqF5eq%2FlxOMe%2BWJ1pNVqu0mKhskb%2FmOCNvCfL%2BbLUB%2B3jrQ&mtid=254c6736f3bfe2333172a577198a8ab5&mtxid=d0ee073ec278&multiaddr=1&s_sg=140860d87c2c3d6c185c0f0f3f8f0512&s_sc=100&s_st=1477621348");

        Response<String> response = null;
        try {
            response = call.execute();


            String body = response.body();
            JSONObject js = new JSONObject(body);
            if (body != null) {
                List<LiveBean> temp = JSON.parseArray(js.getJSONArray("lives").toString(), LiveBean.class);
                list.addAll(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
