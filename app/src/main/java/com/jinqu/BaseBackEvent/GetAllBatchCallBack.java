package com.jinqu.BaseBackEvent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.Api.ApiComData;
import com.jinqu.Helper.BaseCallBack;
import com.jinqu.Helper.CommonHelper;
import com.jinqu.kyojujor.myuhfapplication.MainActivity;
import com.jinqu.model.BatchModel;
import com.jinqu.model.EPCmodel;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class GetAllBatchCallBack<T> extends BaseCallBack<T> {

    public String ApiUrl;
    protected BatchSpinnerAdapter batchAdapter;
    protected MainActivity mainActivity;

    public GetAllBatchCallBack(Context con, MainActivity mainActivity)
    {
        ApiUrl = con.getString(R.string.url)+ ApiComData.GetAllBatchcontent+"?status=0";
        batchAdapter = new BatchSpinnerAdapter(con);
        this.mainActivity = mainActivity;
    }

    @Override
    protected void OnRequestBefore(Request request) {
    }

    @Override
    protected void onFailure(Call call, IOException e) {

//        CommonHelper.ToastByConnect(batchAdapter.mContext);
    }

    @Override
    protected void onSuccess(Call call, Response response, T o) {

        mainActivity.ClearEpcListView();
        if(o ==null)
        return;

        try
        {
              LinkedTreeMap content = (LinkedTreeMap<String,Object>)o;
              if(content.get("code").equals("200")) {
                  LinkedTreeMap dataT = (LinkedTreeMap) content.get("data");
                  ArrayList<LinkedTreeMap> data = (ArrayList<LinkedTreeMap>) dataT.get("data");
                  if(data!=null &&!data.isEmpty()&&data.size()>0)
                  {
                      for (LinkedTreeMap item : data) {
                          BatchModel bitem = new BatchModel();
                          bitem.setBatch(item.get("batch").toString());

                          int haslabelcount = CommonHelper.GsonDoubleToInt((Double) item.get("haslabelcount"));
                          bitem.setHasScanCount(haslabelcount);

                           int count = CommonHelper.GsonDoubleToInt((Double) item.get("count"));
                           bitem.setNeedcount(count);
                          bitem.setCompanyname(item.get("company_name").toString());

                          batchAdapter.spinbatchdata.add(bitem);
                      }

                      //组装spinner适配器
//                      mainActivity.getC_spi_batch().removeAllViewsInLayout();
                      mainActivity.getC_spi_batch().setAdapter(batchAdapter);
//                      batchAdapter.spinbatchdata = new ArrayList<BatchModel>();
//                      spin.setSelection(batchAdapter.getCount(),true);
                  }else
                  {
                      CommonHelper.ToastByConnect(batchAdapter.mContext);
                  }
              }
        }
        catch (Exception e)
        {
            Log.v("databatch",e.getMessage());
            return;
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

    class BatchSpinnerAdapter extends BaseAdapter
    {

        private ArrayList<BatchModel> spinbatchdata;
        private Context mContext;

        public BatchSpinnerAdapter(Context con)
        {
            spinbatchdata= new ArrayList<>();
            mContext = con;
        }

        @Override
        public int getCount() {
            return spinbatchdata.size();
        }

        @Override
        public BatchModel getItem(int position) {
            return spinbatchdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.batchspinner,parent,false);
            }

            TextView txt_batchitem_name = convertView.findViewById(R.id.batchitem_name);
            TextView txt_batchitem_count = convertView.findViewById(R.id.batchitem_count);
            TextView txt_batchitem_company = convertView.findViewById(R.id.batchitem_company);

            txt_batchitem_name.setText(spinbatchdata.get(position).getBatch());
            txt_batchitem_count.setText(spinbatchdata.get(position).getNeedcount()+"");
            txt_batchitem_company.setText(spinbatchdata.get(position).getCompanyname());
            return convertView;
        }


    }

}