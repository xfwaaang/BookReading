package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by xiaofeng on 2017/2/2.
 */

public class DirectoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<BookBrief> mBookBriefList;
    private LayoutInflater mInflater;
    private View mView;
    private Handler mHandler;

    public DirectoryListAdapter(Context context, List<BookBrief> bookBriefList, Handler handler) {
        mContext = context;
        mBookBriefList = bookBriefList;
        mInflater = LayoutInflater.from(context);
        mHandler = handler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = mInflater.inflate(R.layout.book_detail_list_item_chapter,null,false);
        ViewHolder holder = new ViewHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final BookBrief bookBrief = mBookBriefList.get(position + 1);
        ((ViewHolder)holder).tvChapterName.setText(bookBrief.getChapterName());

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.shortToast(mContext,"" + position);
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
    }

    @Override
    public int getItemCount() {
        if (mBookBriefList == null){
            return 0;
        }
        return mBookBriefList.size() - 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_chapter_name)
        TextView tvChapterName;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
