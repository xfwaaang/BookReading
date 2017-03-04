package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.TextReadingPagerActivity;
import com.xfwang.bookreading.activity.TextReadingScrollActivity;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/1/25.
 */

public class BookDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BookBrief> mBookBriefList;
    private RecyclerView.LayoutManager mLayoutManager;

    public BookDetailListAdapter(Context context, List<BookBrief> bookBriefList, RecyclerView.LayoutManager layoutManager) {
        mBookBriefList = bookBriefList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLayoutManager = layoutManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(mInflater.inflate(R.layout.book_detail_list_item_header,null,false));
            return headerViewHolder;
        }else {
            ChapterViewHolder holder = new ChapterViewHolder(mInflater.inflate(R.layout.book_detail_list_item_chapter,null,false));
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mBookBriefList == null){
            return;
        }

        final BookBrief bookBrief = mBookBriefList.get(position);
        if (position == 0){
            ((HeaderViewHolder)holder).tvBookName.setText(bookBrief.getBookName());
            ((HeaderViewHolder)holder).tvBookAuthor.setText(bookBrief.getBookAuthor());
            ((HeaderViewHolder)holder).tvBookBrief.setText(bookBrief.getBookBrief());
            ((HeaderViewHolder)holder).tvLastChapter.setText(bookBrief.getLastChapter());
            ((HeaderViewHolder)holder).tvLastTime.setText(bookBrief.getLastTime());
            ((HeaderViewHolder)holder).tvChapterNum.setText("共" + (mBookBriefList.size() - 1) + "章");
            Picasso.with(mContext).load(bookBrief.getIconUrl()).into(((HeaderViewHolder)holder).ivBookIcon);

            ((HeaderViewHolder)holder).tvLastChapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    TextReadingPagerActivity.toActivity(mContext,bookBrief.getLastChapterUrl(),bookBrief.getBookId());
//                    TextReadingScrollActivity.toActivity(mContext,bookBrief.getLastChapterUrl(),bookBrief.getBookId());
                    boolean isScrollReading = (boolean) SPUtils.get(mContext,TextReadingScrollActivity.SP_IS_SCROLL_READING,false);
                    if (isScrollReading){
                        TextReadingScrollActivity.toActivity(mContext,bookBrief.getLastChapterUrl(),bookBrief.getBookId());
                    }else {
                        TextReadingPagerActivity.toActivity(mContext,bookBrief.getLastChapterUrl(),bookBrief.getBookId());
                    }
                }
            });

            ((HeaderViewHolder)holder).ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //显示章节选择弹窗
                    showSelectWindow((HeaderViewHolder)holder);
                }
            });

        }else {
            ((ChapterViewHolder)holder).textView.setText(bookBrief.getChapterName());
            ((ChapterViewHolder)holder).textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    TextReadingPagerActivity.toActivity(mContext,bookBrief.getChapterUrl(),bookBrief.getBookId());
//                    TextReadingScrollActivity.toActivity(mContext,bookBrief.getChapterUrl(),bookBrief.getBookId());
                    boolean isScrollReading = (boolean) SPUtils.get(mContext,TextReadingScrollActivity.SP_IS_SCROLL_READING,false);
                    if (isScrollReading){
                        TextReadingScrollActivity.toActivity(mContext,bookBrief.getChapterUrl(),bookBrief.getBookId());
                    }else {
                        TextReadingPagerActivity.toActivity(mContext,bookBrief.getChapterUrl(),bookBrief.getBookId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mBookBriefList == null){
            return 0;
        }
        return mBookBriefList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    //显示章节选择弹窗
    private void showSelectWindow(HeaderViewHolder holder) {
        final PopupWindow popupWindow = new PopupWindow(DensityUtils.dp2px(mContext,200),DensityUtils.dp2px(mContext,100));
        View view = mInflater.inflate(R.layout.layout_pop_window_select,null,false);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(holder.ivSelect,Gravity.CENTER,0,0);

        final EditText editText = (EditText) view.findViewById(R.id.et_select);
        Button button = (Button) view.findViewById(R.id.btn_yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = editText.getText().toString().trim();
                if (result != null && !TextUtils.isEmpty(result)){
                    int number = Integer.valueOf(result);
                    if (number > mBookBriefList.size() - 1){
                        ToastUtils.shortToast(mContext,"还没写呢！！！");
                    }else if (number < 0){
                        ToastUtils.shortToast(mContext,"没有这一章！！！");
                    }else {
                        mLayoutManager.scrollToPosition(number);
                        popupWindow.dismiss();
                        popupWindow.setFocusable(false);
                    }
                }
            }
        });
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_book_author)
        TextView tvBookAuthor;
        @Bind(R.id.tv_book_brief)
        TextView tvBookBrief;
        @Bind(R.id.tv_last_chapter)
        TextView tvLastChapter;
        @Bind(R.id.tv_last_time)
        TextView tvLastTime;
        @Bind(R.id.tv_chapter_num)
        TextView tvChapterNum;
        @Bind(R.id.iv_select)
        ImageView ivSelect;
        @Bind(R.id.image_view)
        ImageView ivBookIcon;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_chapter_name)
        TextView textView;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
