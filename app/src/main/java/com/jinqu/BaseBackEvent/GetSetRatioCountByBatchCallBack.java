package com.jinqu.BaseBackEvent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.Api.ApiComData;
import com.jinqu.Helper.BaseCallBack;
import com.jinqu.Helper.CommonHelper;
import com.jinqu.kyojujor.myuhfapplication.MainActivity;
import com.jinqu.model.BatchModel;
import com.jinqu.model.RatioBatchModel;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class GetSetRatioCountByBatchCallBack<T> extends BaseCallBack<T> {
    public String ApiUrl;
    protected MainActivity mainActivity;
    protected RatioSpinnerAdapter ratioSpinnerAdapter;

    public GetSetRatioCountByBatchCallBack(MainActivity mainActivity) {
        ApiUrl = mainActivity.getApplicationContext().getString(R.string.url)
                + ApiComData.GetSetRatioByBatch;
        this.mainActivity = mainActivity;
        ratioSpinnerAdapter =new RatioSpinnerAdapter(mainActivity.getApplicationContext());
    }


    @Override
    protected void OnRequestBefore(Request request) {

    }

    @Override
    protected void onFailure(Call call, IOException e) {
        mainActivity.UItoastByNoConnect();
    }

    @Override
    protected void onSuccess(Call call, Response response, T o) {

        //清除spinner
        mainActivity.ClearRatioSpinner();
        mainActivity.setallProgress(0);

        if(o ==null)
            return;

        try
        {
            LinkedTreeMap content = (LinkedTreeMap<String,Object>)o;

            if(content.get("code").equals("200"))
            {


//                LinkedTreeMap dataT = (LinkedTreeMap) content.get("data");
                List<LinkedTreeMap> data = (ArrayList<LinkedTreeMap>)content.get("data");
                for (LinkedTreeMap item : data)
                {
                    RatioBatchModel model = new RatioBatchModel();
                    model.setBatch((String) item.get("batch"));
                    model.setName((String) item.get("name"));
                    model.setTotalcount(CommonHelper.GsonDoubleToInt((Double) item.get("totalcount")));
                    model.setHasscancount(CommonHelper.GsonDoubleToInt((Double) item.get("hasscancount")));
                    model.setOthercount(CommonHelper.GsonDoubleToInt((Double) item.get("othercount")));
                    model.setCate_id(CommonHelper.GsonDoubleToInt((Double) item.get("id")));
                    mainActivity.setRatioData(model);
                }
                ratioSpinnerAdapter.spinratiodata =mainActivity.getRatioData();
                //组装spinner适配器
//                      mainActivity.getC_spi_batch().removeAllViewsInLayout();
                mainActivity.getC_spi_cate().setAdapter(ratioSpinnerAdapter);
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
        mainActivity.UItoastByNoConnect();
    }

    @Override
    protected void inProgress(int progress, long total, int id) {

    }

    //内联类,
    class RatioSpinnerAdapter extends BaseAdapter
    {

        private List<RatioBatchModel> spinratiodata;
        private Context mContext;

        public RatioSpinnerAdapter(Context con)
        {
            spinratiodata= new ArrayList<>();
            mContext = con;
        }

        @Override
        public int getCount() {
            return spinratiodata.size();
        }

        @Override
        public RatioBatchModel getItem(int position) {
            return spinratiodata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ratiospinner,parent,false);
            }

            TextView txt_ratioitem_name = convertView.findViewById(R.id.ratio_name);
            TextView txt_ratioitem_count = convertView.findViewById(R.id.ratio_count);
            RatioBatchModel item = spinratiodata.get(position);
            txt_ratioitem_name.setText(item.getName());
            txt_ratioitem_count.setText(item.getOthercount()+"/"+item.getTotalcount());
            return convertView;
        }
    }
}
