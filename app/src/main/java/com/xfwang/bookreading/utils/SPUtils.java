package com.xfwang.bookreading.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by XiaoFeng on 2016/8/13.
 * SharedPreference的封装工具类
 */

public class SPUtils {
    public static final String SHARED_PREF = "setting";

    /**
     * @param context
     * @param key
     * @param object
     * 保存对应类型的数据
     */
    public static void put(Context context,String key,Object object){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof Integer){
            editor.putInt(key, (Integer) object);
        }else if (object instanceof String){
            editor.putString(key, (String) object);
        }else if (object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        }else if (object instanceof Float){
            editor.putFloat(key, (Float) object);
        }else if (object instanceof Long){
            editor.putLong(key, (Long) object);
        }else {
            editor.putString(key,object.toString());
        }

        editor.apply();
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     * 取出对应类型的数据
     */
    public static Object get(Context context,String key,Object defaultValue){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        if (defaultValue instanceof String){
            return sp.getString(key, (String) defaultValue);
        }else if (defaultValue instanceof Integer){
            return sp.getInt(key, (Integer) defaultValue);
        }else if (defaultValue instanceof Float){
            return sp.getFloat(key, (Float) defaultValue);
        }else if (defaultValue instanceof Long){
            return sp.getLong(key, (Long) defaultValue);
        }else if (defaultValue instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defaultValue);
        }

        return null;
    }

    /**
     * @param context
     * @param key
     * 移除对应键值的值
     */
    public static void remove(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * @param context
     * 清除所有数据
     */
    public static void clear(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * @param context
     * @param key
     * @return
     * 查询对应键值的数据是否存在
     */
    public static boolean contain(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * @param context
     * @return
     * 取出所有键值数据
     */
    public static Map<String,?> getAll(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
