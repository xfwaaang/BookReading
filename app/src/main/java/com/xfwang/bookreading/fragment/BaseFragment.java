package com.xfwang.bookreading.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xfwang.bookreading.api.ApiHelper;

import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/1/20.
 * 四个标签页fragment的基类
 * 在ViewPager中只加载当前显示页面的数据，不预先缓存下一页
 */

public class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        init();
        initData();
        initEvent();
    }

    protected ApiHelper mApiHelper;

    private void init() {
        mApiHelper = ApiHelper.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
//        initData();
//        initEvent();
    }

    protected void initEvent() {

    }

    protected void initData() {

    }

    protected void initView() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //重写此方法实现ViewPager只有加载并显示此页时才加载数据
    //ViewPager切换页面时会调用FragmentPagerAdapter的setUserVisibleHint方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
//            initData();
        }
    }

}
