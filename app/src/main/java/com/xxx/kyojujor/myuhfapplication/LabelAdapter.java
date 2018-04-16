package com.xxx.kyojujor.myuhfapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LabelAdapter extends BaseAdapter {

    private List<LabelModel> mData;
    private Context mContext;

    public LabelAdapter(List<LabelModel> mData, Context mContext)
    {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        }

        TextView txt_epcname = (TextView) convertView.findViewById(R.id.epcname);
        TextView txt_count = (TextView) convertView.findViewById(R.id.count);

        txt_epcname.setText(mData.get(position).getEpcName());
        txt_count.setText(mData.get(position).getCount());
        return convertView;
    }
}
