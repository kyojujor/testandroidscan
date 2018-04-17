package com.xxx.kyojujor.myuhfapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.xxx.uhfreader.UhfReaderDevice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<LabelModel> mData = null;
    private Context mContext;
    private LabelAdapter mAdapter = null;
    private ListView list_epc;
    private boolean flag = true;

    private UhfReader reader; //超高频读写器
    private UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        list_epc = findViewById(R.id.epc_list);

        mData = new ArrayList<>();
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

        SharedPreferences sharedPortPath = getSharedPreferences("portPath", 0);
        String serialPortPath = sharedPortPath.getString("portPath", "/dev/ttyS2");
        UhfReader.setPortPath(serialPortPath);
        reader = UhfReader.getInstance();

        TextView text = findViewById(R.id.textView);
        if (reader != null)
            text.setText(reader.toString());
        else
            text.setText("33333333");

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textdevice = findViewById(R.id.textdevice);
                int i = 0;
                try{
                    readerDevice = UhfReaderDevice.getInstance();
                }catch (Exception e)
                {
//                    TextView textdevice = findViewById(R.id.textdevice);
//                    textdevice.setText(e.toString());
                }
//                while (flag) {
//                    textdevice.setText("chushi"+i);
//                    List<byte[]> ret = reader.inventoryRealTime();
//                    if (ret != null && !ret.isEmpty()) {
//                        textdevice.setText(ret.toString());
//                        flag = false;
//                    } else {
//                        i++;
//                        textdevice.setText("mao_dou_mei_you"+i);
//                    }
//                    try {
//                        Thread.currentThread().sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

//        //获取读写器设备示例，若返回null，则设备电源打开失败
//        try{
//            readerDevice = UhfReaderDevice.getInstance();
//
//        }catch (Exception e)
//        {
//            text.setText(e.toString());
//        }

//        if (readerDevice != null)
//            text.setText(reader.toString());
//        else
//            text.setText("444444444");
    }
}
