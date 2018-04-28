package com.jinqu.kyojujor.myuhfapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinqu.model.EPCmodel;
import com.xxx.kyojujor.myuhfapplication.R;

import java.util.List;

public class LabelAdapter extends BaseAdapter {

    private List<EPCmodel> mData;
    private Context mContext;

    public LabelAdapter(List<EPCmodel> mData, Context mContext)
    {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public EPCmodel getItem(int position) {

        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Viewholder viewholder = null;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            viewholder = new Viewholder();
            viewholder.item_count = (TextView) convertView.findViewById(R.id.count);
            viewholder.item_epcname = (TextView) convertView.findViewById(R.id.epcname);
            convertView.setTag(viewholder);
        }else {
            viewholder = (Viewholder) convertView.getTag();
        }

//        TextView txt_epcname = (TextView) convertView.findViewById(R.id.epcname);
//        TextView txt_count = (TextView) convertView.findViewById(R.id.count);
//
//        txt_epcname.setText(mData.get(position).getEpc());
//        txt_count.setText(mData.get(position).getCount()+"");
        EPCmodel model =getItem(position);
        viewholder.item_epcname.setText(model.getEpc());
        viewholder.item_count.setText(model.getCount()+"");

        return convertView;
    }



    static  class Viewholder
    {
        TextView item_epcname;
        TextView item_count;
    }
}
