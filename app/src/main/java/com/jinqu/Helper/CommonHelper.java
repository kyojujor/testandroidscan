package com.jinqu.Helper;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.xxx.kyojujor.myuhfapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;


public class CommonHelper {



    //设置按钮是否可用
    public static void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    //仅仅为了gson转int问题而写
    public static int GsonDoubleToInt(double d) {
        String s1 = String.valueOf(d);
        String s2 = s1.substring(0, s1.indexOf("."));
        return Integer.parseInt(s2);
    }

    public enum labelOp {
        SEND(1),//配送
        INCB(2),//入库
        WASH(3);//洗涤

        private int value;

        private labelOp(int value){
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    public static void ToastCommon(String text, Context con)
    {
        Toast toast = Toast.makeText(con, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void ToastByConnect(Context con)
    {
        ToastCommon("访问网络失败,请检查网络设置",con);
    }

    public static void ClearListView(ListView lve, List<?> data)
    {
        data.removeAll(data);
        lve.setAdapter(null);
    }

    //清除进度有关
    //otherCount 剩余未扫的数量
    public void ClearProcess(int otherCount)
    {
//        npb.setProgress(0);
//        npbtext.setText("0/"+otherCount);
    }
}
