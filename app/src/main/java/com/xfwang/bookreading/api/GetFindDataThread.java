package com.xfwang.bookreading.api;

import android.content.Context;
import android.os.Handler;

import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.BookInfo;

import java.util.List;

/**
 * Created by xiaofeng on 2017/1/21.
 * 获取发现页面数据的线程
 */

public class GetFindDataThread implements Runnable {
    private List<BookBrief> mBookBriefList;
    private List<List<BookInfo>> mBookInfosList;
    private Handler mHandler;
    private int what;

    public GetFindDataThread(List<BookBrief> bookBriefList, List<List<BookInfo>> bookInfosList, Handler handler, int what) {
        mBookBriefList = bookBriefList;
        mBookInfosList = bookInfosList;
        mHandler = handler;
        this.what = what;

        //每次获取数据前，清空之前存储的数据
        mBookBriefList.clear();
        mBookInfosList.clear();
    }

    @Override
    public void run() {
        //获取数据
        ApiHelper.getFindPagerData(mBookBriefList,mBookInfosList);
        //数据获取完毕，发送消息，更新view
        if (mHandler != null){
            mHandler.sendEmptyMessage(what);
        }
    }
}
