package com.xfwang.bookreading.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xfwang.bookreading.MyApplication;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnFocusChangeListener {
    public static final String SP_IS_LOGIN = "SP_IS_LOGIN";

    @Bind(R.id.et_user_name) EditText etUserName;
    @Bind(R.id.et_password) EditText etPassword;
    @Bind(R.id.iv_clear) ImageView ivClear;

    @OnClick({R.id.iv_close,R.id.iv_clear,R.id.btn_login,R.id.btn_sign_up})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_clear:
                etUserName.setText("");
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_sign_up:
                signUp();
                break;
        }
    }

    private void login() {
        String userName = etUserName.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            ToastUtils.shortToast(this,"用户名和密码不能为空");
        }else {
            MyUser user = new MyUser();
            user.setUsername(userName);
            user.setPassword(pwd);

            user.login(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                        if (e == null){
                            ToastUtils.shortToast(LoginActivity.this,"登录成功，欢迎回来！");
                            SPUtils.put(LoginActivity.this,SP_IS_LOGIN,true);
                            LoginActivity.this.finish();
                        }else {
                            ToastUtils.shortToast(LoginActivity.this,"请检查用户名或密码是否有错误！！！");
                        }
                }
            });
        }
    }

    private void signUp() {
        String userName = etUserName.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            ToastUtils.shortToast(this,"用户名和密码不能为空");
        }else {
            MyUser user = new MyUser();
            user.setUsername(userName);
            user.setPassword(pwd);

            user.signUp(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                    if (e == null){
                        ToastUtils.shortToast(LoginActivity.this,"注册成功！请登录");
                        etUserName.setText("");
                        etPassword.setText("");
                    }else {
                        ToastUtils.shortToast(LoginActivity.this,"注册失败！该用户名可能已经被占用");
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initEvent();
    }

    private void initEvent() {
        etUserName.setOnFocusChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.et_user_name:
                if (b){
                    ivClear.setVisibility(View.VISIBLE);
                }else {
                    ivClear.setVisibility(View.GONE);
                }
                break;
        }
    }
}

