package com.xxx.kyojujor.myuhfapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<LabelModel> mData = null;
    private Context mContext;
    private LabelAdapter mAdapter = null;
    private ListView list_epc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        list_epc = findViewById(R.id.epc_list);

        mData = new ArrayList<>() ;
        mData.add(new LabelModel("狗说", "你是狗么?"));
        mData.add(new LabelModel("狗说", "你是狗么?"));
        mData.add(new LabelModel("狗说", "你是狗么?"));
        mData.add(new LabelModel("xxx说", "你是狗么?"));
        mData.add(new LabelModel("狗说", "你是狗么?"));
//        mData.add(new Animal("牛说", "你是牛么?", R.mipmap.ic_icon_cow));
//        mData.add(new Animal("鸭说", "你是鸭么?", R.mipmap.ic_icon_duck));
//        mData.add(new Animal("鱼说", "你是鱼么?", R.mipmap.ic_icon_fish));
//        mData.add(new Animal("马说", "你是马么?", R.mipmap.ic_icon_horse));
        mAdapter = new LabelAdapter(mData, mContext);
        list_epc.setAdapter(mAdapter);
    }
}
