package com.jinqu.BaseBackEvent;

import android.util.Log;

import com.jinqu.Helper.BaseCallBack;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class TestCallBack<T> extends BaseCallBack<T>{

    @Override
    protected void OnRequestBefore(Request request) {
        Log.e("OnRequestBefore","xxx");
    }

    @Override
    protected void onFailure(Call call, IOException e) {
        Log.e("onFailure","xxx");
    }

    @Override
    protected void onSuccess(Call call, Response response, Object o) {
        Log.e("onSuccess",o.toString());
    }

    @Override
    protected void onResponse(Response response) {
        Log.e("onResponse","xxx");
    }

    @Override
    protected void onEror(Call call, int statusCode, Exception e) {
        Log.e("onEror","xxxyyy");
    }

    @Override
    protected void inProgress(int progress, long total, int id) {
        Log.e("inProgress","xxx");
    }
}
