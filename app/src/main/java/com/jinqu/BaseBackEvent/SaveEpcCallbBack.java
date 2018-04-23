package com.jinqu.BaseBackEvent;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.Api.ApiComData;
import com.jinqu.Helper.BaseCallBack;
import com.jinqu.Helper.CommonHelper;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class SaveEpcCallbBack<T> extends BaseCallBack<T> {

    public String ApiUrl;
    public Context context;

    public SaveEpcCallbBack(Context con)
    {
        context = con;
        ApiUrl = con.getString(R.string.url)+ ApiComData.InsertEpcByBatch;
    }

    @Override
    protected void OnRequestBefore(Request request) {

    }

    @Override
    protected void onFailure(Call call, IOException e) {
        CommonHelper.ToastByConnect(context);
    }

    @Override
    protected void onSuccess(Call call, Response response, T t) {

//        Toast toast = Toast.makeText(context, "power open", Toast.LENGTH_SHORT);
//        toast.show();
//        return;

        LinkedTreeMap content = (LinkedTreeMap<String,Object>)t;
        double ret = (double)content.get("code");
        if(ret==Double.parseDouble("200"))
        {
            CommonHelper.ToastCommon("EPC数据保存成功",context);

        }
        if(ret==Double.parseDouble("500"))
        {

        }
    }

    @Override
    protected void onResponse(Response response) {

    }

    @Override
    protected void onEror(Call call, int statusCode, Exception e) {
        CommonHelper.ToastByConnect(context);
    }

    @Override
    protected void inProgress(int progress, long total, int id) {

    }
}
