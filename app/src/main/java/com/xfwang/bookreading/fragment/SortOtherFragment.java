package com.xfwang.bookreading.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.SortDetailActivity;
import com.xfwang.bookreading.api.ApiHelper;

import butterknife.OnClick;

/**
 * Created by xiaofeng on 2017/3/1.
 */

public class SortOtherFragment extends BaseFragment {
    private String[] mTitles;

    @Override
    protected void initData() {
        super.initData();
        mTitles = getResources().getStringArray(R.array.SortTitles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_tab_sort_other,null,false);
    }

    @OnClick({R.id.tv_xuanhuan,R.id.tv_xiuzhen,R.id.tv_dushi,R.id.tv_lishi,R.id.tv_wangyou,R.id.tv_kehuan,R.id.tv_kongbu,R.id.tv_quanben,R.id.tv_paihangbang})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_xuanhuan:
                SortDetailActivity.toActivity(getActivity(), ApiHelper.BIQUGE_URL + ApiHelper.XUANHUAN,mTitles[0]);
                break;
            case R.id.tv_xiuzhen:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.XIUZHEN, mTitles[1]);
                break;
            case R.id.tv_dushi:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.DUSHI, mTitles[2]);
                break;
            case R.id.tv_lishi:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.LISHI, mTitles[3]);
                break;
            case R.id.tv_wangyou:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.WANGYOU, mTitles[4]);
                break;
            case R.id.tv_kehuan:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.KEHUAN, mTitles[5]);
                break;
            case R.id.tv_kongbu:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.KONGBU, mTitles[6]);
                break;
            case R.id.tv_quanben:
                SortDetailActivity.toActivity(getActivity(),ApiHelper.BIQUGE_URL + ApiHelper.QUANBEN, mTitles[7]);
                break;
            case R.id.tv_paihangbang:
                break;
        }
    }
}
