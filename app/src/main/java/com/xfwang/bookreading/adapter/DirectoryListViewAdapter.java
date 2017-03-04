package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.ChapterText;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/3/4.
 */

public class DirectoryListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<BookBrief> mBookBriefList;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public DirectoryListViewAdapter(Context context, List<BookBrief> bookBriefList, Handler handler) {
        mContext = context;
        mBookBriefList = bookBriefList;
        mInflater = LayoutInflater.from(context);
        mHandler = handler;
    }

    @Override
    public int getCount() {
        if (mBookBriefList == null){
            return 0;
        }
        return mBookBriefList.size() - 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = mInflater.inflate(R.layout.book_detail_list_item_chapter,null,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        final BookBrief bookBrief = mBookBriefList.get(i + 1);
        holder.tvChapterName.setText(bookBrief.getChapterName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ChapterText chapterText = ApiHelper.getChapterText(bookBrief.getChapterUrl());
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = chapterText;
                        if (chapterText != null){
                            mHandler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });
        return view;
    }

    class ViewHolder{
        @Bind(R.id.tv_chapter_name)
        TextView tvChapterName;
        public ViewHolder(View itemView) {
            ButterKnife.bind(this,itemView);
        }
    }
}
