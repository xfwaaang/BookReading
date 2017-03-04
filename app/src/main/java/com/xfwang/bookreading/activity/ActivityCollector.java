package com.xfwang.bookreading.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofeng on 2017/1/20.
 * activity的集合管理类
 */

public class ActivityCollector {
    public static List<Activity> atyList = new ArrayList<>();

    public static void addActivity(Activity aty){
        atyList.add(aty);
    }

    public static void removeActivity(Activity aty){
        atyList.remove(aty);
    }

    public static void finishAll(){
        for (Activity activity : atyList) {
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
