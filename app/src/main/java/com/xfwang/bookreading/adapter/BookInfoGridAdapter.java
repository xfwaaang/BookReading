package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.bean.BookInfo;

import java.util.List;

/**
 * Created by xiaofeng on 2017/1/23.
 */

public class BookInfoGridAdapter extends BaseAdapter {
    private List<BookInfo> mBookInfoList;
    private LayoutInflater mInflater;

    public BookInfoGridAdapter(Context context, List<BookInfo> bookInfoList) {
        mBookInfoList = bookInfoList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBookInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.grid_bookinfo_item_list,null,false);
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        BookInfo bookInfo = mBookInfoList.get(i);
        textView.setText(bookInfo.getBookName());
        return view;
    }
}
