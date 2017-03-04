package com.xfwang.bookreading.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by XiaoFeng on 2016/8/13.
 * toast封装工具类
 */

public class ToastUtils {

    /**
     * @param context
     * @param msg
     * 短时间显示toast
     */
    public static void shortToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * @param context
     * @param msg
     * 长时间显示toast
     */
    public static void longToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    /**
     * @param context
     * @param msg
     * @param duration
     * 自定义时间显示toast
     */
    public static void toast(Context context,String msg,int duration){
        Toast.makeText(context,msg,duration).show();
    }
}
