package com.xfwang.bookreading.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xfwang.bookreading.api.ApiHelper;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/1/20.
 * 所有activity的基类，方便管理
 */

public class BaseActivity extends AppCompatActivity {
    protected ExecutorService mThreadPool = Executors.newCachedThreadPool();
    protected ApiHelper mApiHelper = ApiHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
