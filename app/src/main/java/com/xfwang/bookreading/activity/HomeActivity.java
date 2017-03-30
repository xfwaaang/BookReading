package com.xfwang.bookreading.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.fragment.HomeTab;
import com.xfwang.bookreading.service.BookUpdateService;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.widget.MyFragmentTabHost;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;

/**
 * Created by xiaofeng on 2017/1/20.
 */

public class HomeActivity extends BaseActivity implements TabHost.OnTabChangeListener, View.OnClickListener {
    @Bind(R.id.tool_bar_home) RelativeLayout toolBarHome;       //顶部toolbar
    @Bind(R.id.tv_title) TextView tvTitle;                      //toolbar，title，TextView
    @Bind(R.id.iv_search) ImageView ivSearch;                   //toolbar，search，ImageView
    @Bind(R.id.iv_more) ImageView ivMore;                       //toolbar，more，ImageView，点击弹出窗体

    @Bind(R.id.tab_host)
    MyFragmentTabHost mTabHost;         //底部导航栏

    private String[] mTitles;           //导航栏及toolbar的title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBookShelfPopWindow();
    }

    private FragmentManager mFragmentManager;
    private void initView() {
        mTitles = getResources().getStringArray(R.array.TopTitles);
        mFragmentManager = getSupportFragmentManager();
        mTabHost.setup(this,mFragmentManager,R.id.home_content);
        mTabHost.getTabWidget().setShowDividers(TabWidget.SHOW_DIVIDER_NONE);       //去除分割线
        //初始化底部导航栏的标签
        initTabs();
        mTabHost.setCurrentTab(0);      //默认显示第0页，书架
    }

    private void initEvent() {
        mTabHost.setOnTabChangedListener(this);
        ivMore.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
    }

    //mTabHost -> HomeTab(4) -> TabSpec , Class
    private void initTabs() {
        HomeTab[] tabs = HomeTab.values();
        for (int i = 0; i < tabs.length; i++) {
            HomeTab tab = tabs[i];
            TabHost.TabSpec spec = mTabHost.newTabSpec(mTitles[i]);
            View indicator = View.inflate(this,R.layout.layout_home_tab_indicator,null);
            TextView tabTitle = (TextView) indicator.findViewById(R.id.tab_title);
            ImageView tabIcon = (ImageView) indicator.findViewById(R.id.tab_icon);
            tabTitle.setText(getString(tab.getTitleRes()));
            tabIcon.setImageResource(tab.getIconRes());

            spec.setIndicator(indicator);

            mTabHost.addTab(spec,tab.getCls(),null);
        }

    }

    @Override
    public void onTabChanged(String s) {
        tvTitle.setText(mTabHost.getCurrentTabTag());
        if (mTabHost.getCurrentTab() == 0){
            ivMore.setImageResource(R.mipmap.more);
        }else if (mTabHost.getCurrentTab() == 3){
            ivMore.setImageResource(R.mipmap.setting);
        }else {
            ivMore.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_more:
                if (mTabHost.getCurrentTab() == 0){
                    //显示书架页面弹出窗
                    showBookShelfPopWindow();
                }
                break;
            case R.id.iv_search:
                //进入搜索界面
                startActivity(new Intent(this,SearchActivity.class));
                break;
            case R.id.tv_list_or_grid:
                //切换订阅书籍显示模式list or grid
                shiftGridOrList();
                break;
            case R.id.tv_sync_book_shelf:
                //同步书架
                syncBookShelf();
                break;
        }
    }

    private void syncBookShelf() {
        BooksFragment fragment = (BooksFragment) mFragmentManager.findFragmentByTag(mTabHost.getCurrentTabTag());

        fragment.updateDate();
        mBookShelfPopWindow.dismiss();
    }

    private boolean isBooksShowList = true;
    private void shiftGridOrList() {
        BooksFragment fragment = (BooksFragment) mFragmentManager.findFragmentByTag(mTabHost.getCurrentTabTag());

        if (isBooksShowList){
            SPUtils.put(this,BooksFragment.SP_BOOKS_IS_LIST,false);
            tvGridOrList.setText("列表展示");
            fragment.shiftGrid();
        }else {
            SPUtils.put(this,BooksFragment.SP_BOOKS_IS_LIST,true);
            tvGridOrList.setText("网格展示");
            fragment.shiftList();
        }
        isBooksShowList = !isBooksShowList;

    }

    private TextView tvGridOrList;  //书架页面弹出窗的网格列表切换TextView
    private TextView tvSyncBokShelf;
    private PopupWindow mBookShelfPopWindow;
    private void initBookShelfPopWindow() {
        mBookShelfPopWindow = new PopupWindow(DensityUtils.dp2px(this,120),DensityUtils.dp2px(this,200));
        mBookShelfPopWindow.setFocusable(true);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_window_book_shelf,null,false);
        mBookShelfPopWindow.setContentView(view);
        //点击外面消失
        mBookShelfPopWindow.setOutsideTouchable(true);
        mBookShelfPopWindow.setBackgroundDrawable(new BitmapDrawable());

        tvGridOrList = (TextView) view.findViewById(R.id.tv_list_or_grid);
        tvSyncBokShelf = (TextView) view.findViewById(R.id.tv_sync_book_shelf);

        tvGridOrList.setOnClickListener(this);
        tvSyncBokShelf.setOnClickListener(this);

        initGridOrListText();
    }

    private void showBookShelfPopWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mBookShelfPopWindow.showAsDropDown(toolBarHome,-10,10,Gravity.RIGHT);
        }else {
            mBookShelfPopWindow.showAtLocation(toolBarHome,Gravity.RIGHT|Gravity.TOP,10,DensityUtils.dp2px(this,70));
        }
    }

    private void initGridOrListText() {
        isBooksShowList = (boolean) SPUtils.get(this, BooksFragment.SP_BOOKS_IS_LIST,true);
        if (isBooksShowList){
            tvGridOrList.setText("网格展示");
        }else {
            tvGridOrList.setText("列表展示");
        }
    }
}
