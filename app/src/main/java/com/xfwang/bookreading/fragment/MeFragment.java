package com.xfwang.bookreading.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xfwang.bookreading.MyApplication;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.LoginActivity;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.SPUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by xiaofeng on 2017/1/20.
 */

public class MeFragment extends BaseFragment {
    private boolean isLogin = false;

    @Bind(R.id.tv_user_name)
    TextView tvUserName;

    @OnClick({R.id.rl_user_view,R.id.tv_logout,R.id.tv_user_brief})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_user_view:
                isLogin = (boolean) SPUtils.get(getActivity(),LoginActivity.SP_IS_LOGIN,false);
                if (!isLogin){
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_logout:
                isLogin = (boolean) SPUtils.get(getActivity(),LoginActivity.SP_IS_LOGIN,false);
                if (isLogin){
                    showLogoutWindow();
                }
                break;
            case R.id.tv_user_brief:

                break;
        }

    }

    private void showLogoutWindow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.logout_dialog,null,false);

        builder.setView(view);

        //须在setView方法之后执行以下两个方法，才可以显示自定义内容
        final AlertDialog dialog = builder.create();
        dialog.show();

        //是
        view.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUser.logOut();
                tvUserName.setText("请登录");
                SPUtils.put(getActivity(),LoginActivity.SP_IS_LOGIN,false);
                dialog.dismiss();
            }
        });
        //否
        view.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        //须在show方法之后使用才有效
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DensityUtils.dp2px(getActivity(),300);
        params.height = DensityUtils.dp2px(getActivity(),200);
        dialog.getWindow().setAttributes(params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_me,null,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        isLogin = (boolean) SPUtils.get(getActivity(),LoginActivity.SP_IS_LOGIN,false);

        if (isLogin){
            tvUserName.setText(MyUser.getCurrentUser().getUsername());
        }
    }

}
