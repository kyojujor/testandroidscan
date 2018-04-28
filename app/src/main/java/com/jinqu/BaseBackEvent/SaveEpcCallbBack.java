package com.jinqu.BaseBackEvent;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.Api.ApiComData;
import com.jinqu.Helper.BaseCallBack;
import com.jinqu.Helper.CommonHelper;
import com.jinqu.Helper.OkHttpManager;
import com.jinqu.kyojujor.myuhfapplication.MainActivity;
import com.jinqu.model.EPCmodel;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class SaveEpcCallbBack<T> extends BaseCallBack<T> {

    public String ApiUrl;
    public Context context;
    public List<EPCmodel> mData;
    public ListView lve;
    public MainActivity mainActivity;

    public SaveEpcCallbBack(Context con, List<EPCmodel> mData, ListView lve,MainActivity mainActivity)
    {
        context = con;
        ApiUrl = con.getString(R.string.url)+ ApiComData.InsertEpcByBatch;
        this.mData = mData;
        this.lve = lve;
        this.mainActivity = mainActivity;
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
        String ret = (String) content.get("code");
        if(ret.equals("200"))
        {
            CommonHelper.ToastCommon("EPC数据保存成功",context);
//            CommonHelper.ClearListView(lve,mData);
//            new CommonHelper().ClearProcess(0);
            //回调请求批次号,更新数据
            GetAllBatchCallBack event = mainActivity.GetSpinevent();
            OkHttpManager.getInstance().getRequest(event.ApiUrl,
                    event);
        }
        if(ret.equals("500"))
        {
            String msg = (String) content.get("msg");
            CommonHelper.ToastCommon("保存失败"+msg,context);
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
