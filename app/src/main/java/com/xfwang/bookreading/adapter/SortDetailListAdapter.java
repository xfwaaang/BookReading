package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.BookDetailActivity;
import com.xfwang.bookreading.activity.TextReadingScrollActivity;
import com.xfwang.bookreading.bean.SortItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/2/4.
 */

public class SortDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SortItem> mSortItemList;

    private View mView1;
    private View mView3;

    public SortDetailListAdapter(Context context, List<SortItem> sortItemList) {
        mContext = context;
        mSortItemList = sortItemList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            return new Holder0(mInflater.inflate(R.layout.list_text_item_find_pager,null,false));
        }else if (viewType == 1){
            mView1 = mInflater.inflate(R.layout.list_bookbrief_item_find_pager,null,false);
            return new Holder1(mView1);
        }else if (viewType == 2){
            return new Holder2(mInflater.inflate(R.layout.sort_detail_list_item_2,null,false));
        }else if (viewType == 3){
            mView3 = mInflater.inflate(R.layout.sort_detail_list_item_3,null,false);
            return new Holder3(mView3);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        holder.setIsRecyclable(false);
        SortItem sortItem = mSortItemList.get(position);
        final String bookUrl = sortItem.getBookUrl();
        final String chpaterUrl = sortItem.getChapterUrl();

        if (sortItem.getViewType() == 0){
            ((Holder0)holder).tvTitle.setText(sortItem.getBookName());
        }else if (getItemViewType(position) == 1){
            ((Holder1)holder).tvBookName.setText(sortItem.getBookName());
            ((Holder1)holder).tvBookAuthor.setText(sortItem.getBookAuthor());
            ((Holder1)holder).tvBookBrief.setText(sortItem.getBookInfo());
            if (TextUtils.isEmpty(sortItem.getBookIconUrl())){
                Picasso.with(mContext).load("http://www.biquge.com.tw/modules/article/images/nocover.jpg").into(((Holder1)holder).ivIcon);
            }else {
                Picasso.with(mContext).load(sortItem.getBookIconUrl()).into(((Holder1)holder).ivIcon);
            }

            mView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookDetailActivity.toActivity(mContext,bookUrl);
                }
            });
        }else if (sortItem.getViewType() == 2){
            ((Holder2)holder).tvBookName.setText(sortItem.getBookName());
            ((Holder2)holder).tvBookAuthor.setText(sortItem.getBookAuthor());
            ((Holder2)holder).tvChapter.setText(sortItem.getChapterName());

            ((Holder2)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookDetailActivity.toActivity(mContext,bookUrl);
                }
            });

            ((Holder2)holder).tvBookName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookDetailActivity.toActivity(mContext,bookUrl);
                }
            });

            ((Holder2)holder).tvChapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //http://www.biquge.com.tw/0_703/
//                    TextReadingPagerActivity.toActivity(mContext,chpaterUrl,bookUrl.split("/")[3]);
                    TextReadingScrollActivity.toActivity(mContext,chpaterUrl,bookUrl.split("/")[3]);
                }
            });
        }else if (sortItem.getViewType() == 3){
            holder.setIsRecyclable(false);
            ((Holder3)holder).tvBookName.setText(sortItem.getBookName());
            ((Holder3)holder).tvBookAuthor.setText(sortItem.getBookAuthor());

            mView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookDetailActivity.toActivity(mContext,bookUrl);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSortItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mSortItemList.get(position).getViewType();
    }

    class Holder0 extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_title)
        TextView tvTitle;

        public Holder0(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class Holder1 extends RecyclerView.ViewHolder{
        @Bind(R.id.image_view)
        ImageView ivIcon;
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_book_author)
        TextView tvBookAuthor;
        @Bind(R.id.tv_book_brief)
        TextView tvBookBrief;

        public Holder1(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class Holder2 extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_book_author)
        TextView tvBookAuthor;
        @Bind(R.id.tv_chapter)
        TextView tvChapter;

        public Holder2(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class Holder3 extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_book_author)
        TextView tvBookAuthor;

        public Holder3(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
