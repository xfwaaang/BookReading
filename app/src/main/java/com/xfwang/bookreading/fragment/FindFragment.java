package com.xfwang.bookreading.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.FindPagerListAdapter;
import com.xfwang.bookreading.api.GetFindDataThread;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.BookInfo;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by xiaofeng on 2017/1/20.
 * 首页发现标签页
 */

public class FindFragment extends BaseFragment {
    @Bind(R.id.list_view_find)
    ListView mListView;
    @Bind(R.id.swipe_refresh_find)
    SwipeRefreshLayout mRefreshLayout;

    //initData方法执行完，数据加载完毕后，发送消息执行initView方法
    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                if (isAdded()){
                    initView();
                }
            }else if (msg.what == 1){
                if (mBookBriefList.size() > 0 && mBookInfosList.size() > 0){
                    if (isAdded()){
                        mListAdapter.notifyDataSetChanged();
                    }
                }else {
                    ToastUtils.shortToast(getActivity(),"请求超时，请稍后再试...");
                }

                mRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_find,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshLayout();
    }

    //存储数据的变量
    private List<BookBrief> mBookBriefList = new ArrayList<>();
    private List<List<BookInfo>> mBookInfosList = new ArrayList<>();

    @Override
    protected void initData() {
        super.initData();
        //开启线程获取数据
        new Thread(new GetFindDataThread(mBookBriefList,mBookInfosList,mHandler,0)).start();
    }

    @Override
    protected void initView() {
        super.initView();


        initEvent();

        //初始化mListView
        initBookList();
    }

    //数据加载显示成功前，默认显示progress
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setProgressViewOffset(true, 0, 100);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setRefreshing(true);
    }

    private FindPagerListAdapter mListAdapter;
    private void initBookList() {
        mRefreshLayout.setRefreshing(false);    //数据显示完毕，隐藏掉progress

        //创建mListAdapter
        mListAdapter = new FindPagerListAdapter(getActivity(),mBookBriefList,mBookInfosList);
        mListView.setAdapter(mListAdapter);

        if (mBookBriefList.size() <= 0 || mBookInfosList.size() <= 0){
            ToastUtils.shortToast(getActivity(),"请求超时，请稍后再试...");
        }
    }

    @Override
    protected void initEvent() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        new Thread(new GetFindDataThread(mBookBriefList,mBookInfosList,mHandler,1)).start();
    }


}
