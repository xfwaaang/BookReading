package com.xfwang.bookreading.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.BookDetailListAdapter;
import com.xfwang.bookreading.adapter.SpaceItemDecoration;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by xiaofeng on 2017/1/24.
 * 书籍详情页，包含目录
 */

public class BookDetailActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String BOOK_DETAIL_URL = "book_detail_url";        //书籍详情链接
    private String mBookDetailUrl;
    private List<BookBrief> mBookBriefList;
    private boolean isLogin;

    @Bind(R.id.swipe_refresh_book_detail)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    //顶部toolbar
    @Bind(R.id.tv_title)
    TextView tvBookName;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_add)
    ImageView ivAdd;

    //右下方直达底部（顶部）button
    @Bind(R.id.btn_down)
    FloatingActionButton mBtnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ButterKnife.bind(this);

        initData();
        initEvent();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            initAddOrDeleteIcon(isLogin);

            if (msg.what == 0){     //初次获取数据
                initView();
            }else if (msg.what == 1){   //刷新数据
                if (mBookBriefList != null){
                    initAdapter(mLayoutManager);
                    tvBookName.setText(mBookBriefList.get(0).getBookName());
                }else {
                    ToastUtils.shortToast(BookDetailActivity.this,"网络异常！ 请稍后再试...");
                }
            }

            mRefreshLayout.setRefreshing(false);
        }
    };

    private boolean isSubscribed;       //该书籍是否被订阅了
    private String mBookId;             //该书籍的唯一标识id
    private String mBookIds;            //已订阅书籍id集合

    //初始化 添加或删除书架按钮的icon
    private void initAddOrDeleteIcon(boolean isLogin) {
        if (mBookBriefList != null){
            mBookId = mBookDetailUrl.split("/")[3];
            if (isLogin){
                mBookIds = BmobUser.getCurrentUser(MyUser.class).getBookIds();
                if (mBookIds == null){
                    isSubscribed = false;
                }else {
                    isSubscribed = mBookIds.contains(mBookId) ? true : false;
                }
            }else {
                //id1#id2#id3...
                mBookIds = (String) SPUtils.get(BookDetailActivity.this, BooksFragment.SP_BOOK_SUBSCRIBED_IDS, "");
                isSubscribed = mBookIds.contains(mBookId) ? true : false;
            }

            if (isSubscribed){
                ivAdd.setImageResource(R.mipmap.delete);
            }else {
                ivAdd.setImageResource(R.mipmap.add);
            }
        }
    }

    private BookDetailListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private void initView() {
        mLayoutManager = new LinearLayoutManager(BookDetailActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.recycler_space)));
        initAdapter(mLayoutManager);

        if (mBookBriefList != null){
            tvBookName.setText(mBookBriefList.get(0).getBookName());
        }else {
            ToastUtils.shortToast(BookDetailActivity.this,"网络异常！ 请稍后再试...");
        }
    }

    private void initAdapter(RecyclerView.LayoutManager layoutManager){
        mAdapter = new BookDetailListAdapter(BookDetailActivity.this,mBookBriefList,layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mBookDetailUrl = getIntent().getStringExtra(BOOK_DETAIL_URL);
        isLogin = (boolean) SPUtils.get(this,LoginActivity.SP_IS_LOGIN,false);

        initRefreshLayout();

        updateData(0);
    }

    //更新数据
    //what=0：初次获取数据，=1：刷新数据
    private void updateData(final int what){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mBookBriefList = ApiHelper.getBookDetailData(mBookDetailUrl);
                if (!isFinishing()){
                    mHandler.sendEmptyMessage(what);
                }
            }
        };

        mThreadPool.execute(runnable);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mBtnDown.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    //数据加载显示成功前，默认显示progress
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setProgressViewOffset(true, 0, 100);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setRefreshing(true);
    }

    public static void toActivity(Context context, String url){
        Intent intent = new Intent(context,BookDetailActivity.class);
        intent.putExtra(BOOK_DETAIL_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private boolean isBottom = false;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:  //返回前一页
                finish();
                break;
            case R.id.btn_down:     //迅速滑到顶部或底部
                if (mBookBriefList != null){
                    if (isBottom){
                        mLayoutManager.scrollToPosition(0);
                        mBtnDown.setImageResource(R.mipmap.down);
                    }else {
                        mLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
                        mBtnDown.setImageResource(R.mipmap.up);
                    }
                    isBottom = !isBottom;
                }
                break;
            case R.id.iv_add:
                addOrDeleteBookShelf(isLogin);
                break;
        }
    }

    //将该书籍添加或删除书架
    private void addOrDeleteBookShelf(boolean isLogin) {
        if (mBookBriefList == null){
            return;
        }

        if (isLogin){
            if (isSubscribed){
                MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
                if (myUser != null){
                    MyUser newUser = new MyUser();
                    newUser.setBookIds(mBookIds.replace(mBookId + "#", ""));
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            initAddOrDeleteIcon(true);
                            if (e == null){
                                ToastUtils.shortToast(BookDetailActivity.this, "成功从书架移除");
                                SPUtils.remove(BookDetailActivity.this,mBookBriefList.get(0).getBookId());
                            }else {
                                ToastUtils.shortToast(BookDetailActivity.this, "移除失败");
                            }
                        }
                    });
                }
            }else {
                MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
                if (myUser != null){
                    MyUser newUser = new MyUser();
                    newUser.setBookIds(mBookId + "#" + mBookIds);
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            initAddOrDeleteIcon(true);
                            if (e == null){
                                ToastUtils.shortToast(BookDetailActivity.this, "成功添加到书架");
                            }else {
                                ToastUtils.shortToast(BookDetailActivity.this, "添加失败");
                            }
                        }
                    });
                }
            }
        }else {
            if (isSubscribed){  //已经订阅，则删除
                SPUtils.put(BookDetailActivity.this, BooksFragment.SP_BOOK_SUBSCRIBED_IDS, mBookIds.replace(mBookId + "#", ""));
                SPUtils.remove(BookDetailActivity.this,mBookBriefList.get(0).getBookId());
                ToastUtils.shortToast(BookDetailActivity.this, "成功从书架移除");
            }else {     //没有订阅，则订阅
                SPUtils.put(BookDetailActivity.this, BooksFragment.SP_BOOK_SUBSCRIBED_IDS,mBookId + "#" + mBookIds);
                ToastUtils.shortToast(BookDetailActivity.this, "成功添加到书架");
            }
        }

        //每次点击后，更新图标
        initAddOrDeleteIcon(isLogin);
    }

    @Override
    public void onRefresh() {
        updateData(1);
    }


}
