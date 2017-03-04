package com.xfwang.bookreading.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xfwang.bookreading.R;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FullScreen);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this,HomeActivity.class));
                finish();
            }
        },1500);
    }
}
