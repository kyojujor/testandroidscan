package com.jinqu.Helper;

import android.graphics.Color;
import android.widget.Button;

public class CommonHelper {
    //设置按钮是否可用
    public static void setButtonClickable(Button button, boolean flag){
        button.setClickable(flag);
        if(flag){
            button.setTextColor(Color.BLACK);
        }else{
            button.setTextColor(Color.GRAY);
        }
    }
}
