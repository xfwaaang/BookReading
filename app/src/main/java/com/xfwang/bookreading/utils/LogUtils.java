package com.xfwang.bookreading.utils;

import android.content.Context;
import android.util.Log;

import static android.R.attr.tag;

/**
 * Created by XiaoFeng on 2016/8/13.
 * 打印log的工具类
 */

public class LogUtils {
    public static final String TAG = "TAG";
    /*
    * 下面是默认TAG的log
    * */
    public static void i(String msg){
        Log.i(TAG, msg);
    }
    public static void d(String msg){
        Log.d(TAG, msg);
    }

    public static void e(String msg){
        Log.e(TAG, msg);
    }
    public static void v(String msg){
        Log.v(TAG, msg);
    }

    /*
    * 下面是以当前类名为TAG的log
    * */
    public static void i(Context context , String msg){
        String tag = context.getClass().getSimpleName();
        Log.i(tag,msg);
    }

    public static void d(Context context,String msg){
        String tag = context.getClass().getSimpleName();
        Log.d(tag,msg);
    }

    public static void e(Context context,String msg){
        String tag = context.getClass().getSimpleName();
        Log.e(tag,msg);
    }

    public static void v(Context context,String msg){
        String tag = context.getClass().getSimpleName();
        Log.v(tag,msg);
    }

    /*
    * 下面是自定义TAG的log
    * */
    public static void i(String tag,String msg){
        Log.i(tag,msg);
    }

    public static void d(String tag,String msg){
        Log.d(tag,msg);
    }

    public static void e(String tag,String msg){
        Log.e(tag,msg);
    }

    public static void v(String tag,String msg){
        Log.v(tag,msg);
    }
}
