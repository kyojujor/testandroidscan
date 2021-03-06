package com.jinqu.kyojujor.myuhfapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.Settings;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jinqu.BaseBackEvent.GetAllBatchCallBack;
import com.jinqu.BaseBackEvent.GetSetRatioCountByBatchCallBack;
import com.jinqu.BaseBackEvent.SaveEpcCallbBack;
import com.jinqu.Helper.CommonHelper;
import com.jinqu.Helper.OkHttpManager;
import com.jinqu.model.BatchModel;
import com.jinqu.model.EPCmodel;
import com.jinqu.model.RatioBatchModel;
import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.wang.avi.AVLoadingIndicatorView;
import com.xxx.kyojujor.myuhfapplication.R;
import com.jinqu.uhfreader.UhfReaderDevice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.media.AudioManager.STREAM_MUSIC;

public class MainActivity extends AppCompatActivity {

    private List<EPCmodel> mData = null;
    private List<RatioBatchModel> ratioData = null;
    private Context mContext;
    private LabelAdapter mAdapter = null;
    private boolean runflag = true;
    private boolean startflag = false;
    private static boolean isOpenLight = true;


    private Button c_btn_power;
    private Button c_btn_startScan;
    private Button c_btn_clear;
    private Button c_btn_save;
    private ListView c_epc_list;
    private Spinner c_spi_batch;
    private Spinner c_spi_cate;//保存类别
    private BatchModel beSelectedBatch;
    private RatioBatchModel beratioBatchModel;
    private AVLoadingIndicatorView c_avi;
    private NumberProgressBar c_npb;//进度条
    private TextView c_scancountview;//进度条具体数据

    private UhfReader reader; //超高频读写器
    private UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源
    private SoundPool soundPool;//todo 扫描到新标签则蜂鸣
    private int soundID;
    private String androidcode;//唯一标识码,每次请求都带


    //标志位,标志批次选择号是否被第一次选择
    private boolean oneBatchSelectFlag = false;

    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;

    public String GetBeSelectedBatchId() {
        return beSelectedBatch.getBatch();
    }

    public MainActivity() {
        mainActivity = this;
    }

    public Spinner getC_spi_batch() {
        return c_spi_batch;
    }

    public Spinner getC_spi_cate() {
        return c_spi_cate;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keepScreenLongLight(this);

        initView();
        initReader();

        GetAllBatchCallBack event = GetSpinevent();
        Map<String,String> map = new HashMap<>();
        map.put("rotcode",androidcode);
        OkHttpManager.getInstance().getRequest(event.ApiUrl,
                event, map);

    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        onclickClass cl = new onclickClass();
        SpinerItemSelected sid = new SpinerItemSelected();

        c_btn_clear = findViewById(R.id.btn_clear);
        c_btn_clear.setOnClickListener(cl);
        c_btn_power = findViewById(R.id.btn_power);
        c_btn_power.setOnClickListener(cl);
        c_btn_save = findViewById(R.id.btn_save);
        c_btn_save.setOnClickListener(cl);

        c_btn_startScan = findViewById(R.id.btn_startScan);
        c_btn_startScan.setOnClickListener(cl);

        c_spi_batch = findViewById(R.id.spin_batch);
        c_spi_batch.setOnItemSelectedListener(sid);
        //种类
        c_spi_cate = findViewById(R.id.spin_category);
        c_spi_cate.setOnItemSelectedListener(sid);


        c_epc_list = findViewById(R.id.epc_list);
        c_npb = findViewById(R.id.number_progress_bar);
        c_scancountview = findViewById(R.id.scancountview);

        mContext = MainActivity.this;
        mData = new ArrayList<>();
        ratioData = new ArrayList<>();

//        beSelectedBatch = new BatchModel();
        //加载声音
        soundPool = new SoundPool(5, STREAM_MUSIC, 0);
        soundID = soundPool.load(this, R.raw.msg, 1);

        androidcode = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
    }

    private void loadSound() {
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;

        soundPool.play(soundID, volumnRatio, volumnRatio, 1, 0, 1);
    }

    public GetAllBatchCallBack<?> GetSpinevent() {
        return new GetAllBatchCallBack<>(getApplicationContext(), mainActivity);
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
//        SharedPreferences shared = getSharedPreferences("power", 0);
//        int value = shared.getInt("value", 26);
//        Log.d("", "value" + value);
        reader.setOutputPower(13);

        Log.d("debugOnCreate", "all init");
        Thread thread = new InventoryThread();
        thread.start();
    }

    public void setRatioData(RatioBatchModel ratioDataItem) {
        if (ratioData != null)
            this.ratioData.add(ratioDataItem);
    }

    public List<RatioBatchModel> getRatioData() {
        return this.ratioData;
    }

    public String getAndroidcode() {
        return androidcode;
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
                        epcList = reader.inventoryRealTime(); //实时盘存
                        if (epcList != null && !epcList.isEmpty()) {
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
                    if (epc_code.equals(epcitem.getEpc())) {
                        epcitem.setCount(epcitem.getCount() + 1);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {

                    //声音播放
                    loadSound();

                    //list中不存在此epc
                    mData.add(new EPCmodel(epc_code, 1));
                    setallProgress(mData.size());
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
//                    GetLabelTitleByBatchCallBack<LinkedTreeMap> event = new GetLabelTitleByBatchCallBack(getMainActivity());
//                    OkHttpManager.getInstance().getRequest(event.ApiUrl,
//                            event);
//                    GetSetRatioCountByBatchCallBack<LinkedTreeMap> event = new GetSetRatioCountByBatchCallBack(getMainActivity());
//                    OkHttpManager.getInstance().getRequest(event.ApiUrl,
//                            event);
                    CommonHelper.ToastCommon("打开电源", getApplicationContext());
                    break;
                case R.id.btn_startScan:
                    startflag = !startflag;
                    String tostr = "";
                    FileWriter localFileWriterOn = null;
                    try {
                        localFileWriterOn = new FileWriter(new File(
                                "/proc/gpiocontrol/set_uhf"));
                        if (startflag) {
                            localFileWriterOn.write("1");
                            c_btn_startScan.setText("关闭扫描");
                            tostr = "扫描端口打开";
                            CommonHelper.setButtonClickable(c_btn_save, false);
                        } else {
                            localFileWriterOn.write("0");
                            c_btn_startScan.setText("开始扫描");
                            tostr = "扫描端口关闭";
                            CommonHelper.setButtonClickable(c_btn_save, true);
                        }
                        localFileWriterOn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CommonHelper.ToastCommon(tostr, getApplicationContext());
                    break;
                case R.id.btn_save:
                    if (mData != null && mData.size() > 0) {
                        SaveEpcDialog();
                    } else {
                        CommonHelper.ToastCommon("标签列表中无数据,无法保存", getApplicationContext());
                    }
                    break;
                case R.id.btn_clear:
                    ClearEpcListView();
                    break;

            }
        }
    }


    //批次下拉选择选项后的触发事件
    class SpinerItemSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.spin_batch:
                    beSelectedBatch = (BatchModel) parent.getAdapter().getItem(position);
                    //清除所有进度数据以及列表
                    ClearRatioSpinner();

                    GetSetRatioCountByBatchCallBack<LinkedTreeMap> event = new GetSetRatioCountByBatchCallBack<>(getMainActivity());
                    Map<String, String> map = new HashMap<>();
                    map.put("batch", beSelectedBatch.getBatch());
                    map.put("rotcode",androidcode);
                    OkHttpManager.getInstance().getRequest(event.ApiUrl,
                            event, map);
                    CommonHelper.ToastCommon("已选择批次号" + beSelectedBatch.getBatch(), getApplication());
                    break;
                case R.id.spin_category:
                    beratioBatchModel = (RatioBatchModel) parent.getAdapter().getItem(position);
                    CommonHelper.ToastCommon("已选择 " + beratioBatchModel.getName(), getApplication());
                    break;
            }
            setallProgress(0);
            ClearEpcListView();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private void SaveEpcDialog() {
        String[] items = new String[]{"保存EPC", "取消"};

        if (beSelectedBatch == null) {
            CommonHelper.ToastCommon("请选择批次号", getApplicationContext());
            return;
        }

        if (beratioBatchModel == null) {
            CommonHelper.ToastCommon("请选择保存类别", getApplicationContext());
            return;
        }

        int finalCount = 0;//最后还需扫描数量
        finalCount = beratioBatchModel.getOthercount() - mData.size();

        if (finalCount < 0) {
            CommonHelper.ToastCommon("扫描数量超过剩余数量,请清空列表重新扫描", getApplicationContext());
            return;
        }

        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器


        String title = MessageFormat.format("本次扫描数:{0},还需扫描数:{1},保存类别:{2} 是否保存?",
                mData.size(), finalCount, beratioBatchModel.getName()
        );
        builder.setTitle(title); //设置标题
        //builder.setMessage("是否确认退出?"); //设置内容
        //builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                tv_text.setText(items[which]);
                if (which == 0)//确定保存
                {
                    SaveEpcCallbBack<LinkedTreeMap> event = new SaveEpcCallbBack<>(getApplicationContext(), mData, c_epc_list, getMainActivity());

                    Map<String, String> postlist = new HashMap<>();

                    //把EPC的键值拼接成一个list
                    ArrayList<String> tempepcnumberList = new ArrayList<>();
//                    mData = new ArrayList<>();
//                    mData.add(new EPCmodel("xxxxx1", 23));
//                    mData.add(new EPCmodel("xxxxx2", 23));

                    if (mData != null && mData.size() > 0) {
                        for (EPCmodel epcitem : mData) {
                            tempepcnumberList.add(epcitem.getEpc());
                        }

                        String epcstr = new Gson().toJson(tempepcnumberList);
                        Log.d("epcstr", epcstr);

                        postlist.put("EPC", epcstr);
                        postlist.put("batch", beSelectedBatch.getBatch());
                        postlist.put("operation_id", CommonHelper.labelOp.INCB.value() + "");
                        postlist.put("cate", beratioBatchModel.getCate_id() + "");
                        postlist.put("rotcode",androidcode);

                        OkHttpManager.getInstance().postRequest(event.ApiUrl,
                                event, postlist);
                    }
                }
            }
        });
//        builder.create();
        builder.create().show();
    }

    //清除列表里面所有的item
    public void ClearEpcListView() {
        CommonHelper.ClearListView(c_epc_list, mData);
        mData.removeAll(mData);
        setallProgress(0);
//        }
    }

    //设置进度条
    public void setallProgress(int number) {
        c_npb.setProgress(number);
        if (beratioBatchModel != null) {
            c_npb.setMax(beratioBatchModel.getOthercount());
            c_scancountview.setText(number + "/" + beratioBatchModel.getOthercount());
        } else {
            c_scancountview.setText(number + "/" + 0);
        }
    }

    /**
     * 是否使屏幕常亮
     *
     * @param activity
     */
    public static void keepScreenLongLight(Activity activity) {
        Window window = activity.getWindow();
        if (isOpenLight) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    public void ClearRatioSpinner() {
        if (ratioData != null) {
            ratioData.removeAll(ratioData);
        }
        if (beratioBatchModel != null) {
            beratioBatchModel = null;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                c_spi_cate.setAdapter(null);
            }
        });
    }

    public void UItoastByNoConnect()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonHelper.ToastCommon("访问网络失败,请检查网络设置",getApplicationContext());
            }
        });
    }

    public void UItoast(final String str)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonHelper.ToastCommon(str,getApplicationContext());
            }
        });
    }


}
