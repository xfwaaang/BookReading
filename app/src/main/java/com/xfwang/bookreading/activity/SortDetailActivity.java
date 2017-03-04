package com.xfwang.bookreading.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.SortDetailListAdapter;
import com.xfwang.bookreading.adapter.SpaceItemDecoration;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.SortItem;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaofeng on 2017/2/4.
 */

public class SortDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String SORT_URL = "SORT_URL";
    private static final String SORT_TITLE = "SORT_TITLE";

    private String mUrl;
    private String mTitle;
    private List<SortItem> mSortItemList;

    @OnClick(R.id.iv_back)
    public void back(View view){
        finish();
    }

    @Bind(R.id.tv_title) TextView tvTitle;
    @Bind(R.id.iv_more) ImageView ivMore;

    @Bind(R.id.rv_sort_list)
    RecyclerView rvSortList;
    @Bind(R.id.refresh_layout_sort_detail)
    SwipeRefreshLayout mRefreshLayout;

    private SortDetailListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_detail);

        ButterKnife.bind(this);

        initData();
        initEvent();
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        mUrl = getIntent().getStringExtra(SORT_URL);
        mTitle = getIntent().getStringExtra(SORT_TITLE);
        tvTitle.setText(mTitle);
        rvSortList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.recycler_space)));
        initRefreshLayout();

        updateData();
    }

    //数据加载显示成功前，默认显示progress
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setProgressViewOffset(true, 0, 100);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setRefreshing(true);
    }

    private void updateData() {
        mApiHelper.updateSortData(mUrl, new ApiHelper.CallBack() {
            @Override
            public void onSuccess(Object obj) {
                mSortItemList = (List<SortItem>) obj;
                if (!isFinishing()){
                    initView();
                }
            }

            @Override
            public void onFailure() {
                ToastUtils.shortToast(SortDetailActivity.this,"请求超时，请稍后再试...");
                mRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void initView() {
        mRefreshLayout.setRefreshing(false);
        mAdapter = new SortDetailListAdapter(this,mSortItemList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSortList.setLayoutManager(layoutManager);
        rvSortList.setAdapter(mAdapter);
    }

    public static void toActivity(Context context, String url, String title){
        Intent intent = new Intent(context,SortDetailActivity.class);
        intent.putExtra(SORT_URL,url);
        intent.putExtra(SORT_TITLE,title);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        updateData();
    }
}
