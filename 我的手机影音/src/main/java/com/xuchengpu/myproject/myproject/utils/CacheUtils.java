package com.xuchengpu.myproject.myproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 许成谱 on 2017/1/13.
 */

public class CacheUtils {
    public static void setPlayMode(Context context,String key,int value) {
        SharedPreferences sp=context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    public static  int  getPlayMode(Context context,String key){
        SharedPreferences sp=context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return  sp.getInt(key,-1);
    }
}
