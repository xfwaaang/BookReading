package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.BookDetailActivity;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.BookInfo;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/1/21.
 * 发现页面ListView的适配器
 */

public class FindPagerListAdapter extends BaseAdapter {
    private List<BookBrief> mBookBriefList;
    private List<List<BookInfo>> mBookInfosList;
    private LayoutInflater mInflater;
    private Context mContext;

    public FindPagerListAdapter(Context context, List<BookBrief> bookBriefList, List<List<BookInfo>> bookInfosList) {
        mContext = context;
        mBookBriefList = bookBriefList;
        mBookInfosList = bookInfosList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mBookBriefList.size() > 0 && mBookInfosList.size() > 0){
            return mBookBriefList.size() + mBookInfosList.size() + 7;
        }else {
            return 0;
        }
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
    public int getItemViewType(int i) {
        if (i == 0 || i == 5 || i == 8 || i == 11 || i == 14 || i == 17 || i == 20){
            return 0;
        }else if (i == 7 || i == 10 || i == 13 || i == 16 || i == 19 || i == 22){
            return 2;
        }else {
            return 1;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == 0){
            return inflateTitleView(i);
        }else if (getItemViewType(i) == 2){
            return inflateBookInfoView(i);
        }else {
            return inflateBookBriefView(i);
        }
    }

    private View inflateBookInfoView(int i) {
        int pos = 0;
        if (i == 10){
            pos = 1;
        }else if (i == 13){
            pos = 2;
        }else if (i == 16){
            pos = 3;
        }else if (i == 19){
            pos = 4;
        }else if (i == 22){
            pos = 5;
        }

        View view = mInflater.inflate(R.layout.list_bookinfo_item_find_pager,null,false);

        //没有获取到数据，直接返回没有初始化的view
        if (mBookInfosList.size() == 0){
            return view;
        }

        final List<BookInfo> bookInfos = mBookInfosList.get(pos);

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        if (bookInfos != null){
            BookInfoGridAdapter adapter = new BookInfoGridAdapter(mContext,bookInfos);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BookDetailActivity.toActivity(mContext,bookInfos.get(i).getBookUrl());
                }
            });
        }

        return view;
    }

    private View inflateBookBriefView(int i) {
        int pos = 0;
        if (i >= 1 && i <= 4){
            pos = i - 1;
        }else if (i == 6){
            pos = 4;
        }else if (i == 9){
            pos = 5;
        }else if (i == 12){
            pos = 6;
        }else if (i == 15){
            pos = 7;
        }else if (i == 18){
            pos = 8;
        }else if (i == 21){
            pos = 9;
        }

        View view = mInflater.inflate(R.layout.list_bookbrief_item_find_pager,null,false);
        //没有获取到数据，直接返回没有初始化的view
        if (mBookBriefList.size() == 0){
            return view;
        }

        ViewHolder holder = new ViewHolder(view);

        final BookBrief bookBrief = mBookBriefList.get(pos);
        holder.tvBookName.setText(bookBrief.getBookName());
        if (bookBrief.getBookAuthor() != null){
            holder.tvBookAuthor.setText(bookBrief.getBookAuthor());
        }
        holder.tvBookBrief.setText(bookBrief.getBookBrief());
        if(!TextUtils.isEmpty(bookBrief.getIconUrl())){
            Picasso.with(mContext).load(bookBrief.getIconUrl()).into(holder.imageView);
        }else {
            Picasso.with(mContext).load("http://www.biquge.com.tw/modules/article/images/nocover.jpg").into(holder.imageView);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookDetailActivity.toActivity(mContext,bookBrief.getBookUrl());
            }
        });

        return view;
    }

    private String[] mTitles = {
      "首页推荐", "玄幻小说", "修真小说", "都市小说", "历史小说", "网游小说", "科幻小说"
    };
    private View inflateTitleView(int i) {
        //i == 0 && i == 5 && i == 8 && i == 11 && i == 14 && i == 17 && i == 20
        int pos = 0;
        if (i == 5){
            pos = 1;
        }else if (i == 8){
            pos = 2;
        }else if (i == 11){
            pos = 3;
        }else if (i == 14){
            pos = 4;
        }else if (i == 17){
            pos = 5;
        }else if (i == 20){
            pos = 6;
        }
        View view = mInflater.inflate(R.layout.list_text_item_find_pager,null,false);
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setText(mTitles[pos]);
        return view;
    }

    class ViewHolder{
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_book_author)
        TextView tvBookAuthor;
        @Bind(R.id.tv_book_brief)
        TextView tvBookBrief;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
