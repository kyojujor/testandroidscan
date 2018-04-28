package com.jinqu.BaseBackEvent;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.Api.ApiComData;
import com.jinqu.Helper.BaseCallBack;
import com.jinqu.kyojujor.myuhfapplication.MainActivity;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class GetLabelTitleByBatchCallBack<T> extends BaseCallBack<T> {
    public String ApiUrl;
    protected MainActivity mainActivity;

    public GetLabelTitleByBatchCallBack(MainActivity mainActivity) {
        ApiUrl = mainActivity.getApplicationContext().getString(R.string.url)
                + ApiComData.GetLabelByBatch + "?batch=" + mainActivity.GetBeSelectedBatchId();
        this.mainActivity = mainActivity;
    }


    @Override
    protected void OnRequestBefore(Request request) {

    }

    @Override
    protected void onFailure(Call call, IOException e) {

    }

    @Override
    protected void onSuccess(Call call, Response response, T o) {
        if(o ==null)
            return;

        try
        {
            LinkedTreeMap content = (LinkedTreeMap<String,Object>)o;

            if(content.get("code").equals("200"))
            {
                LinkedTreeMap dataT = (LinkedTreeMap) content.get("data");
                List<LinkedTreeMap> data = (ArrayList<LinkedTreeMap>) dataT.get("data");
//                Map a = new HashMap<String,String>();
//                a.put();
            }
        }
        catch (Exception e)
        {
            Log.d("getlabeltitlebybatchonsuccess",e.getMessage());
        }
    }

    @Override
    protected void onResponse(Response response) {

    }

    @Override
    protected void onEror(Call call, int statusCode, Exception e) {

    }

    @Override
    protected void inProgress(int progress, long total, int id) {

    }
}
