package com.xfwang.bookreading;

import android.app.Application;
import android.content.Intent;

import com.xfwang.bookreading.service.BookUpdateService;

import cn.bmob.v3.Bmob;

/**
 * Created by xiaofeng on 2017/1/21.
 */

public class MyApplication extends Application {
    private Intent mBookUpdateServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"754c3d6c301e2a6a5321100b3ab82aa2");

        mBookUpdateServiceIntent = new Intent(getApplicationContext(), BookUpdateService.class);
        startService(mBookUpdateServiceIntent);
    }

}
