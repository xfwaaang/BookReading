package com.xfwang.bookreading.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.DirectoryListAdapter;
import com.xfwang.bookreading.adapter.DirectoryListViewAdapter;
import com.xfwang.bookreading.adapter.SpaceItemDecoration;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.ChapterText;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ScreenUtils;
import com.xfwang.bookreading.utils.ToastUtils;
import com.xfwang.bookreading.widget.MyTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by xiaofeng on 2017/1/26.
 * 正文阅读界面   滑动阅读
 */

public class TextReadingScrollActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String CHAPTER_TEXT_URL = "CHAPTER_TEXT_URL";      //章节正文链接
    private static final String SP_TEXT_SIZE = "SP_TEXT_SIZE";
    private static final String SP_IS_NIGHT_MODE = "SP_IS_NIGHT_MODE";
    public static final String SP_IS_SCROLL_READING = "SP_IS_SCROLL_READING";
    private static final String KEY_CHAPTER_TEXT_URL = "key_chapter_text_url";
    private static boolean isLogin;

    private String mChapterTextUrl;     //
    private ChapterText mChapterText;       //章节正文实体类

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                initView();
            }else if (msg.what == 1){   //初始化目录适配器
                initDirectoryAdapter();
                mRefreshLayout.setRefreshing(false);
            }else if (msg.what == 2){   //点击目录
                mChapterText = (ChapterText) msg.obj;
                directoryWindow.dismiss();
                initView();
            }
        }
    };

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tv_time)
    TextView tvTime;

//    @Bind(R.id.tv_chapter_name)
//    TextView tvChapterName;

    @Bind(R.id.tv_content)
    MyTextView tvContent;
    @Bind(R.id.text_view_bg)
    NestedScrollView mScrollView;

    private boolean isNightMode = false;
    private float mTextSize = 18;           //默认字体大小

    private static String mBookIds;         //订阅书籍id集合

    private static String mBookId;          //当前书籍id
    private TimeReceiver mTimeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_reading_scroll);
        ButterKnife.bind(this);

        if (savedInstanceState == null){
            mChapterTextUrl = getIntent().getStringExtra(CHAPTER_TEXT_URL);
        }else {
            String url = (String) savedInstanceState.get(KEY_CHAPTER_TEXT_URL);
            mChapterTextUrl = url == null ? getIntent().getStringExtra(CHAPTER_TEXT_URL) : url;
        }

        initData(mChapterTextUrl);
        initEvent();

        initTimeText();

        IntentFilter timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        mTimeReceiver = new TimeReceiver();
        registerReceiver(mTimeReceiver,timeFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTextReadingPopWindow();     //初始化
        initDirectory();    //初始化目录窗体View
        updateDirectory(ApiHelper.BIQUGE_URL + mBookId);        //获取目录数据
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CHAPTER_TEXT_URL,mChapterText.getChapterUrl());
    }

    private List<BookBrief> mBookBriefList;
    /*
    * 更新目录数据
    * */
    private void updateDirectory(final String url) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mBookBriefList = ApiHelper.getBookDetailData(url);
                mHandler.sendEmptyMessage(1);
            }
        };

        mThreadPool.execute(runnable);
    }

    private PopupWindow directoryWindow;
//    private ImageView ivSelect;
//    private FloatingActionButton btnDown;
//    private EditText etSelect;
//    private RecyclerView rvDirectory;
    private SwipeRefreshLayout mRefreshLayout;
    private ListView lvDirectory;
    private View mDirectorView;

    private boolean isBottom = false;
    /*
    * 初始化目录窗体View
    * */
    private void initDirectory() {
        directoryWindow = new PopupWindow(ScreenUtils.getScreenWidth(this)*4/5,ScreenUtils.getScreenHeight(this));
        directoryWindow.setFocusable(true);
        mDirectorView = LayoutInflater.from(this).inflate(R.layout.pop_window_directory,null,false);
        directoryWindow.setContentView(mDirectorView);
        //点击外面消失
        directoryWindow.setOutsideTouchable(true);
        directoryWindow.setBackgroundDrawable(new BitmapDrawable());
//        ivSelect = (ImageView) mDirectorView.findViewById(R.id.iv_select);
//        btnDown = (FloatingActionButton) mDirectorView.findViewById(R.id.btn_down);
//        rvDirectory = (RecyclerView) mDirectorView.findViewById(R.id.rv_directory);
//        etSelect = (EditText) mDirectorView.findViewById(R.id.et_select);
        mRefreshLayout = (SwipeRefreshLayout) mDirectorView.findViewById(R.id.refresh_layout_directory);
        lvDirectory = (ListView) mDirectorView.findViewById(R.id.lv_directory);

//        rvDirectory.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.recycler_space)));
//        btnDown.setOnClickListener(this);
//        ivSelect.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        //下拉刷新目录列表
        updateDirectory(ApiHelper.BIQUGE_URL + mBookId);
    }

//    private DirectoryListAdapter mDirectoryListAdapter;
//    private LinearLayoutManager mLayoutManager;
    /*
    * 初始化目录适配器
    * */
    private void initDirectoryAdapter() {
//        mLayoutManager = new LinearLayoutManager(this);
//        rvDirectory.setLayoutManager(mLayoutManager);
//        mDirectoryListAdapter = new DirectoryListAdapter(this,mBookBriefList,mHandler);
//        rvDirectory.setAdapter(mDirectoryListAdapter);
        lvDirectory.setAdapter(new DirectoryListViewAdapter(this,mBookBriefList,mHandler));
    }

    private void initData(String chapterTextUrl) {
//        mChapterTextUrl = getIntent().getStringExtra(CHAPTER_TEXT_URL);
        mTextSize = (float) SPUtils.get(this,SP_TEXT_SIZE,18F);
        isNightMode = (boolean) SPUtils.get(this,SP_IS_NIGHT_MODE,false);

        updateData(chapterTextUrl);
    }

    /*
    * 获取章节正文数据
    * */
    public void updateData(final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ChapterText chapterText = ApiHelper.getChapterText(url);
                if (chapterText != null){
                    mChapterText = chapterText;
                    if (!isFinishing()){
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }
        };

        mThreadPool.execute(runnable);
    }

    private void initEvent() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolBar.inflateMenu(R.menu.menu_text_reading);
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showTextReadingPopWindow();
                return false;
            }
        });

        tvContent.setMyOnTouchListener(new MyTextView.OnMyTouchListener() {
            @Override
            public void onTouchLeft() {
                if (mChapterText == null)   return;
                //触摸left，加载上一章
                updateData(mChapterText.getLastChapterUrl());
            }

            @Override
            public void onTouchCenter() {
                showTextReadingPopWindow();
            }

            @Override
            public void onTouchRight() {
                if (mChapterText == null)   return;
                if (!mChapterText.getNextChapterUrl().contains(".html")){
                    ToastUtils.shortToast(TextReadingScrollActivity.this,"暂无下一章！");
                    return;
                }

                //触摸right，加载下一章
                updateData(mChapterText.getNextChapterUrl());
            }
        });

    }

    private void initView(){
        //初始化夜间模式
        if (isNightMode){
            mScrollView.setBackgroundResource(R.color.night_color);
            mDirectorView.setBackgroundResource(R.color.night_color);
//            tvChapterName.setBackgroundResource(R.color.night_color);
        }else {
            mScrollView.setBackgroundResource(R.color.text_bg_def);
            mDirectorView.setBackgroundResource(R.color.text_bg_def);
//            tvChapterName.setBackgroundResource(R.color.text_bg_def);
        }

        if (mChapterText == null){
            ToastUtils.shortToast(TextReadingScrollActivity.this,"网络异常！ 请稍后再试...");
        }else {
            mToolBar.setTitle(mChapterText.getBookName());
            String chapterName = mChapterText.getChapterName() + " (" + mChapterText.getChapterText().replace(" ","").replace("\n","").length() + ")";
//            tvChapterName.setText(chapterName);
//            initTimeText();
            tvContent.setText(chapterName + "\n\n" + mChapterText.getChapterText());
            tvContent.setTextSize(mTextSize);
            mScrollView.scrollTo(0,0);
        }

    }

    /**
     * @param context
     * @param url               章节正文链接
     */
    public static void toActivity(Context context, String url, String bookId){
        isLogin = (boolean) SPUtils.get(context,LoginActivity.SP_IS_LOGIN,false);
        if (isLogin){
            mBookIds = BmobUser.getCurrentUser(MyUser.class).getBookIds().replace("null","");
        }{
            mBookIds = (String) SPUtils.get(context,BooksFragment.SP_BOOK_SUBSCRIBED_IDS,"");
        }
        mBookId = bookId;

        Intent intent = new Intent(context,TextReadingScrollActivity.class);
        intent.putExtra(CHAPTER_TEXT_URL,url);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        if (mUtilWindow != null && mUtilWindow.isShowing()){
            mUtilWindow.dismiss();
        }

        if (directoryWindow != null && directoryWindow.isShowing()){
            directoryWindow.dismiss();
        }

        //退出时，更新当前阅读到的章节序号
        if (mChapterText != null){
            if(mBookIds != null && mBookIds.contains(mChapterText.getBookId())){
                SPUtils.put(this, mChapterText.getBookId(), mChapterText.getChapterIndex());
            }
        }

        //更新正文字体大小
        SPUtils.put(this,SP_TEXT_SIZE,mTextSize);

        unregisterReceiver(mTimeReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_to_big:    //加大字体
                mTextSize += 1;
                tvContent.setTextSize(mTextSize);
                break;
            case R.id.iv_to_small:     //减小字体
                mTextSize -= 1;
                tvContent.setTextSize(mTextSize);
                break;
            case R.id.iv_mu_lu:     //显示目录窗体
                mUtilWindow.dismiss();
                directoryWindow.showAtLocation(mToolBar,Gravity.START,0,0);
                break;
            case R.id.iv_night_mode:    //更新夜间模式
                if (isNightMode){
                    mDirectorView.setBackgroundResource(R.color.text_bg_def);
                    mScrollView.setBackgroundResource(R.color.text_bg_def);
                    ivNightMode.setImageResource(R.mipmap.yueliang);
                }else {
                    mDirectorView.setBackgroundResource(R.color.night_color);
                    mScrollView.setBackgroundResource(R.color.night_color);
                    ivNightMode.setImageResource(R.mipmap.taiyang);
                }
                isNightMode = !isNightMode;
                SPUtils.put(TextReadingScrollActivity.this,SP_IS_NIGHT_MODE,isNightMode);
                break;
            case R.id.iv_convert:   //翻页阅读
                if (mChapterText != null) {
                    mUtilWindow.dismiss();
                    SPUtils.put(this,SP_IS_SCROLL_READING,false);
                    TextReadingPagerActivity.toActivity(TextReadingScrollActivity.this, mChapterText.getChapterUrl(), mChapterText.getBookId());
                    finish();
                }
        }
    }

    private ImageView ivToBig;
    private ImageView ivToSmall;
    private ImageView ivMuLu;
    private ImageView ivNightMode;
    private ImageView ivConvert;
    private PopupWindow mUtilWindow;
    /*
    * 初始化toolbar的util窗口
    * */
    private void initTextReadingPopWindow() {
        mUtilWindow = new PopupWindow(DensityUtils.dp2px(this,120),DensityUtils.dp2px(this,240));
        mUtilWindow.setFocusable(true);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_window_text_reading,null,false);
        mUtilWindow.setContentView(view);

        ivToBig = (ImageView) view.findViewById(R.id.iv_to_big);
        ivToSmall = (ImageView) view.findViewById(R.id.iv_to_small);
        ivMuLu = (ImageView) view.findViewById(R.id.iv_mu_lu);
        ivNightMode = (ImageView) view.findViewById(R.id.iv_night_mode);
        ivConvert = (ImageView) view.findViewById(R.id.iv_convert);

        isNightMode = (boolean) SPUtils.get(TextReadingScrollActivity.this,SP_IS_NIGHT_MODE,false);
        if (isNightMode){
            ivNightMode.setImageResource(R.mipmap.taiyang);
        }else {
            ivNightMode.setImageResource(R.mipmap.yueliang);
        }

        ivToBig.setOnClickListener(this);
        ivToSmall.setOnClickListener(this);
        ivMuLu.setOnClickListener(this);
        ivNightMode.setOnClickListener(this);
        ivConvert.setOnClickListener(this);
    }

    private void showTextReadingPopWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mUtilWindow.showAsDropDown(mToolBar,-10,10, Gravity.END);
        }else {
            mUtilWindow.showAtLocation(mToolBar,Gravity.END|Gravity.TOP,10,DensityUtils.dp2px(this,70));
        }
    }

    class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_TIME_TICK){
                initTimeText();
            }
        }
    }

    private void initTimeText() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        tvTime.setText(format.format(new Date()));
    }

}
