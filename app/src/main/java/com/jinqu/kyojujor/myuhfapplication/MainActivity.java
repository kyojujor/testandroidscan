package com.jinqu.kyojujor.myuhfapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jinqu.Helper.CommonHelper;
import com.jinqu.model.EPCmodel;
import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.xxx.kyojujor.myuhfapplication.R;
import com.jinqu.uhfreader.UhfReaderDevice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<EPCmodel> mData = null;
    private Context mContext;
    private LabelAdapter mAdapter = null;
    private boolean runflag = true;
    private boolean startflag = false;


    private Button c_btn_power;
    private Button c_btn_startScan;
    private Button c_btn_clear;
    private Button c_btn_save;
    private ListView c_epc_list;
    private Spinner c_spi_batch;

    private UhfReader reader; //超高频读写器
    private UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initReader();

        Log.d("debugOnCreate", "all init");
        Thread thread = new InventoryThread();
        thread.start();

//        for (int i = 0; i < 60; i++) {
//            if (i % 2 == 0)
//                AddtoEpclist("abcd" + i);
//            else
//                AddtoEpclist("zxc");
//        }

    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        onclickClass cl = new onclickClass();

        c_btn_clear = findViewById(R.id.btn_clear);
        c_btn_power = findViewById(R.id.btn_power);
        c_btn_power.setOnClickListener(cl);
        c_btn_save = findViewById(R.id.btn_save);
        c_btn_startScan = findViewById(R.id.btn_startScan);
        c_btn_startScan.setOnClickListener(cl);

        c_spi_batch = findViewById(R.id.spin_batch);
        c_epc_list = findViewById(R.id.epc_list);

        mContext = MainActivity.this;
        mData = new ArrayList<>();
    }

    //初始化读写器及读写器电源
    private void initReader() {
        SharedPreferences sharedPortPath = getSharedPreferences("portPath", 0);
        String serialPortPath = sharedPortPath.getString("portPath", "/dev/ttyS2");
        UhfReader.setPortPath(serialPortPath);
        reader = UhfReader.getInstance();
        //获取读写器设备示例，若返回null，则设备电源打开失败
        readerDevice = UhfReaderDevice.getInstance();

        if (reader == null || readerDevice == null) {
            Log.e("READER", "读写器及电源至少一个无法打开");
            CommonHelper.setButtonClickable(c_btn_startScan, false);
            CommonHelper.setButtonClickable(c_btn_save, false);
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //获取用户设置功率,并设置
        SharedPreferences shared = getSharedPreferences("power", 0);
        int value = shared.getInt("value", 26);
        Log.d("", "value" + value);
        reader.setOutputPower(value);
    }


    /**
     * 盘存线程
     */
    class InventoryThread extends Thread {

        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();

            while (runflag)
                if (startflag) {
                    {
                        Log.v("xxx", "aaabvbb");
                        epcList = reader.inventoryRealTime(); //实时盘存
                        if (epcList != null && !epcList.isEmpty()) {
                            Log.v("zzz", "epclist.size=" + epcList.size());
                            for (byte[] epc : epcList) {
                                if (epc != null) {
                                    String epcStr = Tools.Bytes2HexString(epc, epc.length);
                                    AddtoEpclist(epcStr);
                                }
                            }
                        }
                        epcList = null;
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
        }

    }

    @Override
    protected void onDestroy() {
//        if (screenReceiver != null) {
//            unregisterReceiver(screenReceiver);
//        }
        runflag = false;
        if (reader != null) {
            reader.close();
        }
        if (readerDevice != null) {
            readerDevice.powerOff();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    ///添加数据至EPCLIST
    public void AddtoEpclist(final String epc_code) {
        if (epc_code.isEmpty())
            return;

        if (mData == null)
            mData = new ArrayList<>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                for (EPCmodel epcitem : mData) {
                    //之前存在epc
                    if ( epc_code.equals(epcitem.getEpc())) {
                        epcitem.setCount(epcitem.getCount() + 1);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    //list中不存在此epc
                    mData.add(new EPCmodel(epc_code, 1));
                }
                mAdapter = new LabelAdapter(mData, mContext);
                c_epc_list.setAdapter(mAdapter);
            }
        });


    }

    class onclickClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_power:
                    runflag = true;
                    Toast toast = Toast.makeText(getApplicationContext(), "power open", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case R.id.btn_startScan:
                    startflag = !startflag;
                    String tostr = "";
                    FileWriter localFileWriterOn = null;
                    try {
                        localFileWriterOn = new FileWriter(new File(
                                "/proc/gpiocontrol/set_uhf"));
                        if(startflag)
                        {
                            localFileWriterOn.write("1");
                            c_btn_startScan.setText("关闭扫描");
                            tostr = "scan open";
                        }else
                        {
                            localFileWriterOn.write("0");
                            c_btn_startScan.setText("开始扫描");
                            tostr = "scan close";

                        }
                        localFileWriterOn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast t2 = Toast.makeText(getApplicationContext(), tostr, Toast.LENGTH_SHORT);
                    t2.show();
                    break;
            }
        }
    }


}
