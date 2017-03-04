package com.xfwang.bookreading.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.SearchListAdapter;
import com.xfwang.bookreading.adapter.SpaceItemDecoration;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.utils.KeyBoardUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaofeng on 2017/1/31.
 */

public class SearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnKeyListener {
    @Bind(R.id.et_search) EditText etSearch;
    @Bind(R.id.refresh_layout_search) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.rv_search_list) RecyclerView rvSearchList;
    @Bind(R.id.tv_pager_index) TextView tvPagerIndex;

    @OnClick(R.id.iv_back)
    public void back(View view){
        finish();
    }

    @OnClick(R.id.iv_search)
    public void search(View view){
        initRefreshLayout();
        mKey = etSearch.getText().toString().replace("\n","").trim();
        mPagerIndex = 0;
        refreshData();
    }

    //上一页
    @OnClick(R.id.btn_last)
    public void last(View view){
        if (mPagerIndex == 0){
            return;
        }
        mPagerIndex -= 1;
        refreshData();
    }

    //下一页
    @OnClick(R.id.btn_next)
    public void next(View view){
        mPagerIndex += 1;
        refreshData();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initView();
        }
    };

    private void initView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearchList.setLayoutManager(layoutManager);
        mAdapter = new SearchListAdapter(this,mBookBriefList);
        rvSearchList.setAdapter(mAdapter);
        tvPagerIndex.setText("" + (mPagerIndex + 1));
        if (mBookBriefList == null){
            ToastUtils.shortToast(this,"网络不给力");
        }


        mRefreshLayout.setRefreshing(false);
        KeyBoardUtils.closeKeybord(etSearch,this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initData();
        initEvent();
    }

    private List<BookBrief> mBookBriefList;
    private SearchListAdapter mAdapter;
    private String mKey;
    private int mPagerIndex = 0;

    private void initData() {
        rvSearchList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.recycler_space)));
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        etSearch.setOnKeyListener(this);
    }

    private void updateData(final String key, final int index, final String mode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBookBriefList = ApiHelper.getSearchData(key,index,mode);
                if (!isFinishing()){
                    mHandler.sendEmptyMessage(index);
                }
            }
        }).start();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setProgressViewOffset(true, 0, 100);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    private void refreshData(){
        if (TextUtils.isEmpty(mKey)){
            etSearch.startAnimation(AnimationUtils.loadAnimation(this,R.anim.et_shake_anim));
            mRefreshLayout.setRefreshing(false);
        }else {
            updateData(mKey,mPagerIndex,ApiHelper.SEARCH_MODE_DEF);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            initRefreshLayout();
            mKey = etSearch.getText().toString().trim();
            etSearch.setText(mKey.replace("\n",""));
            mPagerIndex = 0;
            refreshData();
        }
        return false;
    }
}