package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.BookDetailActivity;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.utils.DensityUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/2/1.
 */

public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BookBrief> mBookBriefList;
    private View mView;

    public SearchListAdapter(Context context, List<BookBrief> bookBriefList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBookBriefList = bookBriefList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = mInflater.inflate(R.layout.search_list_item,null,false);
        Holder holder = new Holder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final BookBrief bookBrief = mBookBriefList.get(position);
        Picasso.with(mContext).load(bookBrief.getIconUrl()).into(((Holder)holder).ivIcon);
        ((Holder)holder).tvBookName.setText(bookBrief.getBookName());
        ((Holder)holder).tvBookAuthor.setText(bookBrief.getBookAuthor());
        ((Holder)holder).tvBookType.setText(bookBrief.getBookType());
        ((Holder)holder).tvUpdateTime.setText(bookBrief.getLastTime());
        ((Holder)holder).tvUpdateState.setText(bookBrief.getUpdateState());
        ((Holder)holder).tvBookBrief.setText(bookBrief.getBookBrief());

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookDetailActivity.toActivity(mContext,bookBrief.getBookUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mBookBriefList == null){
            return 0;
        }
        return mBookBriefList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        @Bind(R.id.image_view) ImageView ivIcon;
        @Bind(R.id.tv_book_name) TextView tvBookName;
        @Bind(R.id.tv_book_author) TextView tvBookAuthor;
        @Bind(R.id.tv_book_type) TextView tvBookType;
        @Bind(R.id.tv_update_time) TextView tvUpdateTime;
        @Bind(R.id.tv_update_state) TextView tvUpdateState;
        @Bind(R.id.tv_book_brief) TextView tvBookBrief;
        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
