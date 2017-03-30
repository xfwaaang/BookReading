package com.xfwang.bookreading.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.LoginActivity;
import com.xfwang.bookreading.adapter.BookShelfGridAdapter;
import com.xfwang.bookreading.adapter.BookShelfListAdapter;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookEntity;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.db.DBManager;
import com.xfwang.bookreading.utils.LogUtils;
import com.xfwang.bookreading.utils.NetworkUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;

/**
 * Created by xiaofeng on 2017/1/20.
 * 订阅书籍列表页（书架）
 */

public class BooksFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String SP_BOOK_SUBSCRIBED_IDS = "BOOK_SUBSCRIBED_IDS";
    public static final String SP_BOOKS_IS_LIST = "books_is_list";

    @Bind(R.id.rv_books)
    RecyclerView mRvBooks;
    @Bind(R.id.refresh_layout_book_shelf)
    SwipeRefreshLayout mRefreshLayout;

    private boolean mIsList;    //订阅书籍是否是列表显示
    private boolean isLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_books,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateDataFromLocal();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBookEntityList != null) {
            DBManager.clear(getActivity());
            DBManager.put(getActivity(), mBookEntityList);
        }

        getActivity().unregisterReceiver(mBookUpdateReceiver);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mRefreshLayout.setOnRefreshListener(this);
    }

    private String mBookIds;
    private String[] mBookDetailUrlArray;
    private List<BookEntity> mBookEntityList;

    @Override
    protected void initData() {
        super.initData();
        initRefreshLayout();

//        updateDataFromLocal();

//        updateDate();

        mBookUpdateReceiver = new BookUpdateReceiver();
        IntentFilter bookUpdateFilter = new IntentFilter(ACTION_BOOK_UPDATE);
        getActivity().registerReceiver(mBookUpdateReceiver,bookUpdateFilter);

    }

    /*
    * 获取本地书架数据
    * */
    private void updateDataFromLocal() {
        mBookEntityList = DBManager.get(getActivity());

        initView();
    }

    public void updateDate(){
        isLogin = (boolean) SPUtils.get(getActivity(), LoginActivity.SP_IS_LOGIN,false);

        boolean isHave = checkBookShelf(isLogin);
        if (isHave){
            mApiHelper.updateBookShelfData(mBookDetailUrlArray, new ApiHelper.CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    mBookEntityList = (List<BookEntity>) obj;
                    if (isAdded()){
                        initView();
                    }

                    DBManager.clear(getActivity());
                    DBManager.put(getActivity(),mBookEntityList);
                }

                @Override
                public void onFailure() {
                    mRefreshLayout.setRefreshing(false);
                    ToastUtils.shortToast(getActivity(),"请求超时，请稍后再试...");
                }
            });
        }else {
            ToastUtils.shortToast(getActivity(),"书架还没有书，赶快去添加吧");
            mRefreshLayout.setRefreshing(false);
            mBookEntityList = null;
            initView();
        }
    }

    public boolean checkBookShelf(boolean isLogin){
        if (isLogin){
            String ids = BmobUser.getCurrentUser(MyUser.class).getBookIds();
            mBookIds = ids == null ? "" : ids.replace("null","");
        }else {
            mBookIds = (String) SPUtils.get(getActivity(),SP_BOOK_SUBSCRIBED_IDS,"");
        }

        if (mBookIds != null && mBookIds.contains("#")){
            String[] bookIdArray = mBookIds.split("#");
            mBookDetailUrlArray = new String[bookIdArray.length];
            for (int i = 0; i < mBookDetailUrlArray.length; i++) {
                mBookDetailUrlArray[i] = ApiHelper.BIQUGE_URL + bookIdArray[i];
            }
            return true;
        }

        return false;
    }

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void initView() {
        super.initView();

        mRefreshLayout.setRefreshing(false);

        //初始化书籍显示模式
        mIsList = (boolean) SPUtils.get(getActivity(),SP_BOOKS_IS_LIST,true);

        if (mIsList){
            shiftList();
        }else {
            shiftGrid();
        }

    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setProgressViewOffset(true, 0, 100);
        mRefreshLayout.setDistanceToTriggerSync(300);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setRefreshing(true);
    }

    public void shiftGrid() {
        mLayoutManager = new GridLayoutManager(getActivity(),3);
        mRvBooks.setLayoutManager(mLayoutManager);
        mAdapter = new BookShelfGridAdapter(getActivity(),mBookEntityList);
        mRvBooks.setAdapter(mAdapter);
    }

    public void shiftList() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvBooks.setLayoutManager(mLayoutManager);
        mAdapter = new BookShelfListAdapter(getActivity(),mBookEntityList);
        mRvBooks.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        updateDate();
    }

    public static final String ACTION_BOOK_UPDATE = "ACTION_BOOK_UPDATE";
    private BookUpdateReceiver mBookUpdateReceiver;
    public class BookUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_BOOK_UPDATE) {
                updateDate();
                LogUtils.i(ACTION_BOOK_UPDATE);
            }
        }
    }

}
