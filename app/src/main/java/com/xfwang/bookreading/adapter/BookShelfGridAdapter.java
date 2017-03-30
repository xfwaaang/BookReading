package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.TextReadingPagerActivity;
import com.xfwang.bookreading.activity.TextReadingScrollActivity;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookEntity;
import com.xfwang.bookreading.db.DBManager;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.SPUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaofeng on 2017/1/28.
 */

public class BookShelfGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BookEntity> mBookEntityList;

    private View mView;
    private String mChapterIndex;
    private List<BookEntity> mBookEntityOldList;


    public BookShelfGridAdapter(Context context, List<BookEntity> bookEntityList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBookEntityList = bookEntityList;
        mBookEntityOldList = DBManager.get(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = mInflater.inflate(R.layout.book_shelf_grid_item,null,false);
        ViewHolder holder = new ViewHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (position == 0 || position == 1 || position == 2){
            params.topMargin = 0;
        }else {
            params.topMargin = DensityUtils.dp2px(mContext,10);
        }

        params.leftMargin = DensityUtils.dp2px(mContext,5);
        params.rightMargin = DensityUtils.dp2px(mContext,5);

        mView.setLayoutParams(params);

        final BookEntity bookEntity = mBookEntityList.get(position);
        Picasso.with(mContext).load(bookEntity.getBookIconUrl()).into(((ViewHolder)holder).ivBookIcon);
        ((ViewHolder)holder).tvBookName.setText(bookEntity.getBookName());


        for (int i=0; i<mBookEntityOldList.size(); i++){
            BookEntity bookEntityOld = mBookEntityOldList.get(i);
            if (bookEntity.getBookId() == bookEntityOld.getBookId() && Integer.valueOf(bookEntity.getChapterNum()) > Integer.valueOf(bookEntityOld.getChapterNum())){
                ((ViewHolder)holder).tvUpdate.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder)holder).tvUpdate.setVisibility(View.GONE);
            }
        }


        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChapterIndex = (String) SPUtils.get(mContext, bookEntity.getBookId(), bookEntity.getFirstChapterIndex());
                String bookIds = ((String)SPUtils.get(mContext, BooksFragment.SP_BOOK_SUBSCRIBED_IDS,"")).replace(bookEntity.getBookId() + "#","");
                SPUtils.put(mContext,BooksFragment.SP_BOOK_SUBSCRIBED_IDS,bookEntity.getBookId() + "#" + bookIds);
                boolean isScrollReading = (boolean) SPUtils.get(mContext,TextReadingScrollActivity.SP_IS_SCROLL_READING,false);
                if (isScrollReading){
                    TextReadingScrollActivity.toActivity(mContext,ApiHelper.BIQUGE_URL + bookEntity.getBookId() + "/" + mChapterIndex + ".html",bookEntity.getBookId());
                }else {
                    TextReadingPagerActivity.toActivity(mContext,ApiHelper.BIQUGE_URL + bookEntity.getBookId() + "/" + mChapterIndex + ".html",bookEntity.getBookId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mBookEntityList == null){
            return 0;
        }
        return mBookEntityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.image_view)
        ImageView ivBookIcon;
        @Bind(R.id.tv_book_name)
        TextView tvBookName;
        @Bind(R.id.tv_update)
        TextView tvUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
