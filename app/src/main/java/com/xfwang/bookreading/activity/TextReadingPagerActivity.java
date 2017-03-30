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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.adapter.DirectoryListAdapter;
import com.xfwang.bookreading.adapter.DirectoryListViewAdapter;
import com.xfwang.bookreading.adapter.SpaceItemDecoration;
import com.xfwang.bookreading.adapter.VPTextAdapter;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.ChapterText;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.utils.DensityUtils;
import com.xfwang.bookreading.utils.LogUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ScreenUtils;
import com.xfwang.bookreading.utils.ToastUtils;
import com.xfwang.bookreading.widget.DepthPageTransformer;
import com.xfwang.bookreading.widget.MyViewPager;
import com.xfwang.bookreading.widget.ViewPagerScroller;
import com.xfwang.bookreading.widget.ZoomOutPageTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

import static com.xfwang.bookreading.activity.TextReadingScrollActivity.SP_IS_SCROLL_READING;
import static com.xfwang.bookreading.utils.SPUtils.get;

/**
 * Created by xiaofeng on 2017/1/26.
 * 正文阅读界面   翻页阅读
 */

public class TextReadingPagerActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String CHAPTER_TEXT_URL = "CHAPTER_TEXT_URL";      //章节正文链接
    public static final String SP_TEXT_SIZE = "SP_TEXT_SIZE";
    public static final String SP_IS_NIGHT_MODE = "SP_IS_NIGHT_MODE";
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

    @Bind(R.id.iv_back)
    ImageView ivBack;           //toolbar返回按钮
    @Bind(R.id.tv_title)
    TextView tvTitle;           //toolbar标题，章节名称
    @Bind(R.id.iv_more)
    ImageView ivMore;

    @Bind(R.id.tv_to_last)
    TextView tvToLast;
    @Bind(R.id.tv_to_next)
    TextView tvToNext;
    @Bind(R.id.iv_to_small)
    ImageView ivToSmall;
    @Bind(R.id.iv_to_big)
    ImageView ivToBig;
    @Bind(R.id.iv_night_mode)
    ImageView ivNightMode;
    @Bind(R.id.iv_convert)
    ImageView ivConvert;
    @Bind(R.id.iv_mu_lu)
    ImageView ivMuLu;
    @Bind(R.id.iv_setting)
    ImageView ivSetting;

    @Bind(R.id.view_pager)
    MyViewPager mViewPager;

    @Bind(R.id.tv_chapter_name)
    TextView tvChapterName;
    @Bind(R.id.tv_pager_index)
    TextView tvPagerIndex;
    @Bind(R.id.tv_time)
    TextView tvTime;

    @Bind(R.id.tool_bar_text_reading)
    RelativeLayout mToolBar;
    @Bind(R.id.bottom_window)
    LinearLayout mBottomWindow;

    private boolean isNightMode = false;
    private float mTextSize = 18;           //默认字体大小

    private static String mBookIds;         //订阅书籍id集合

    private static String mBookId;          //当前书籍id

    private BatteryReceiver mBatteryReceiver;
    private TimeReceiver mTimeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_reading_pager);
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

        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryReceiver = new BatteryReceiver();
        registerReceiver(mBatteryReceiver,batteryFilter);

        IntentFilter timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        mTimeReceiver = new TimeReceiver();
        registerReceiver(mTimeReceiver,timeFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    private SwipeRefreshLayout mRefreshLayout;
    private ListView lvDirectory;
    private View mDirectorView;

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
        mRefreshLayout = (SwipeRefreshLayout) mDirectorView.findViewById(R.id.refresh_layout_directory);
        lvDirectory = (ListView) mDirectorView.findViewById(R.id.lv_directory);

        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        //下拉刷新目录列表
        updateDirectory(ApiHelper.BIQUGE_URL + mBookId);
    }

    /*
    * 初始化目录适配器
    * */
    private void initDirectoryAdapter() {
        lvDirectory.setAdapter(new DirectoryListViewAdapter(this,mBookBriefList,mHandler));
    }

    private void initData(String chapterTextUrl) {
        mTextSize = (float) get(this,SP_TEXT_SIZE,18F);
        isNightMode = (boolean) get(this,SP_IS_NIGHT_MODE,false);

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
                }

                if (!isFinishing()){
                    mHandler.sendEmptyMessage(0);
                }
            }
        };

        mThreadPool.execute(runnable);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        ivMore.setOnClickListener(this);

        tvToLast.setOnClickListener(this);
        tvToNext.setOnClickListener(this);
        ivToBig.setOnClickListener(this);
        ivToSmall.setOnClickListener(this);
        ivMuLu.setOnClickListener(this);
        ivNightMode.setOnClickListener(this);
        ivConvert.setOnClickListener(this);
        ivSetting.setOnClickListener(this);

        mViewPager.setMyOnTouchListener(new MyViewPager.OnMyTouchListener() {
            @Override
            public void onTouchLeft() {
                if(mToolBar.getVisibility() != View.GONE){
                    mToolBar.setVisibility(View.GONE);
                }
                if(mBottomWindow.getVisibility() != View.GONE){
                    mBottomWindow.setVisibility(View.GONE);
                    return;
                }

                if (mViewPager.getCurrentItem() == 0){
//                    toLastChapter();
                    toLastChapterLastPager();
                }

            }

            @Override
            public void onTouchCenter() {
                if (mToolBar.getVisibility() == View.GONE){
                    mToolBar.setVisibility(View.VISIBLE);
                }else {
                    mToolBar.setVisibility(View.GONE);
                }
                if (mBottomWindow.getVisibility() == View.GONE){
                    mBottomWindow.setVisibility(View.VISIBLE);
                }else {
                    mBottomWindow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTouchRight() {
                if (mToolBar.getVisibility() != View.GONE) {
                    mToolBar.setVisibility(View.GONE);
                }
                if (mBottomWindow.getVisibility() != View.GONE) {
                    mBottomWindow.setVisibility(View.GONE);
                    return;
                }

                if (mViewPager.getCurrentItem() == mPagerCount - 1)
                    toNextChapter();
                }
        });
    }

    //切换到上一章最后一页
    private void toLastChapterLastPager() {
        isLastPager = true;
        toLastChapter();
        isLastPager = false;
    }

    private void toLastChapter(){
        if (mChapterText == null)   return;
        updateData(mChapterText.getLastChapterUrl());
    }

    private void toNextChapter(){
        if (mChapterText == null)   return;

        if (!mChapterText.getNextChapterUrl().contains(".html")){
            ToastUtils.shortToast(TextReadingPagerActivity.this,"暂无下一章！");
            return;
        }

        //触摸right，加载下一章
        updateData(mChapterText.getNextChapterUrl());
    }

    private void initView(){
        //初始化夜间模式
        if (isNightMode){
            mViewPager.setBackgroundResource(R.color.night_color);
            mDirectorView.setBackgroundResource(R.color.night_color);
            ivNightMode.setImageResource(R.mipmap.taiyang);
        }else {
            mViewPager.setBackgroundResource(R.color.text_bg_def);
            mDirectorView.setBackgroundResource(R.color.text_bg_def);
            ivNightMode.setImageResource(R.mipmap.yueliang);
        }

        if (mChapterText == null){
            ToastUtils.shortToast(TextReadingPagerActivity.this,"网络异常！ 请稍后再试...");
        }else {
            tvTitle.setText(mChapterText.getBookName());

            tvChapterName.setText(mChapterText.getChapterName());

            initViewPager();

        }

    }

    private int mPagerCount;

    /**
     * 根据屏幕大小对文本进行分页
     * @return
     */
    private String[] getTextArray(){
        float density = getResources().getDisplayMetrics().density;
        float textSize = (float) SPUtils.get(this,SP_TEXT_SIZE,18F);
        String text = mChapterText.getChapterText();
        //屏幕每一行可以显示的字符数
        int perRowCharCount = (int) ((ScreenUtils.getScreenWidth(this) - 24*density) / (textSize*density));
        //每页屏幕可以显示文本的行数
        int perPagerRowCount = (int) ((ScreenUtils.getScreenHeight(this) - 60*density) / (textSize*density + getResources().getDimension(R.dimen.text_view_line_space))) - 1;

        char[] replaceCharArray = new char[perRowCharCount];
        for (int i = 0; i < replaceCharArray.length; i++) {
            replaceCharArray[i] = '#';
        }

        String replaceStr = new String(replaceCharArray);

        String replaceText = text.replace("\n",replaceStr);
        int perPagerCharCount = perRowCharCount * perPagerRowCount;

        int pagerCount = replaceText.length() / perPagerCharCount + 1;
        mPagerCount = pagerCount;

        String[] textArray = new String[pagerCount];

        for (int i = 0; i < textArray.length; i++) {
            if (replaceText.length() < perPagerCharCount*i + perPagerCharCount){
                textArray[i] = replaceText.substring(perPagerCharCount * i, replaceText.length()).replace(replaceStr,"\n").replace("#","");
            }else {
                textArray[i] = replaceText.substring(perPagerCharCount * i, perPagerCharCount * i + perPagerCharCount).replace(replaceStr,"\n").replace("#","");
            }
        }

        return textArray;
    }

    private VPTextAdapter mVPTextAdapter;
    private boolean isLastPager = false;
    private void initViewPager() {
        String[] textArray = getTextArray();
        mTextSize = (float) get(this,SP_TEXT_SIZE,18F);

        mVPTextAdapter = new VPTextAdapter(TextReadingPagerActivity.this,textArray,mTextSize);
        mViewPager.setAdapter(mVPTextAdapter);

        if (isLastPager){
            mViewPager.setCurrentItem(mPagerCount - 1);
            tvPagerIndex.setText(mPagerCount + "/" + mPagerCount);
        }else {
            tvPagerIndex.setText(1 + "/" + mPagerCount);
        }

        mViewPager.setPageTransformer(true,new DepthPageTransformer());
//        mViewPager.setPageTransformer(true,new ZoomOutPageTransformer());

//        ViewPagerScroller scroller = new ViewPagerScroller(this);
//        scroller.setScrollDuration(500);
//        scroller.initViewPagerScroll(mViewPager);//这个是设置切换过渡时间为2秒

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPagerIndex.setText(position + 1 + "/" + mPagerCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * @param context
     * @param url               章节正文链接
     */
    public static void toActivity(Context context, String url, String bookId){
        isLogin = (boolean) get(context,LoginActivity.SP_IS_LOGIN,false);
        if (isLogin){
            mBookIds = BmobUser.getCurrentUser(MyUser.class).getBookIds().replace("null","");
        }{
            mBookIds = (String) get(context,BooksFragment.SP_BOOK_SUBSCRIBED_IDS,"");
        }
        mBookId = bookId;

        Intent intent = new Intent(context,TextReadingPagerActivity.class);
        intent.putExtra(CHAPTER_TEXT_URL,url);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

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
        unregisterReceiver(mBatteryReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_more:   //弹出窗体
                // TODO: 2017/3/4  show more window
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_to_last:
                toLastChapter();
                break;
            case R.id.tv_to_next:
                toNextChapter();
                break;
            case R.id.iv_to_big:    //加大字体
                mTextSize += 1;
                SPUtils.put(TextReadingPagerActivity.this,SP_TEXT_SIZE,mTextSize);
                ToastUtils.shortToast(this,"读到下一章字体才会变化哦！");
                break;
            case R.id.iv_to_small:     //减小字体
                mTextSize -= 1;
                SPUtils.put(TextReadingPagerActivity.this,SP_TEXT_SIZE,mTextSize);
                ToastUtils.shortToast(this,"读到下一章字体才会变化哦！");
                break;
            case R.id.iv_mu_lu:     //显示目录窗体
                if (mToolBar.getVisibility() != View.GONE){
                    mToolBar.setVisibility(View.GONE);
                }
                if (mBottomWindow.getVisibility() != View.GONE){
                    mBottomWindow.setVisibility(View.GONE);
                }
                directoryWindow.showAtLocation(mToolBar,Gravity.LEFT,0,0);
                break;
            case R.id.iv_night_mode:    //更新夜间模式
                if (isNightMode){
                    mDirectorView.setBackgroundResource(R.color.text_bg_def);
                    mViewPager.setBackgroundResource(R.color.text_bg_def);
                    ivNightMode.setImageResource(R.mipmap.yueliang);
                }else {
                    mDirectorView.setBackgroundResource(R.color.night_color);
                    mViewPager.setBackgroundResource(R.color.night_color);
                    ivNightMode.setImageResource(R.mipmap.taiyang);
                }
                isNightMode = !isNightMode;
                SPUtils.put(TextReadingPagerActivity.this,SP_IS_NIGHT_MODE,isNightMode);
                break;
            case R.id.iv_convert:   //翻页阅读
                if (mChapterText != null) {
                    SPUtils.put(this,SP_IS_SCROLL_READING,true);
                    TextReadingScrollActivity.toActivity(TextReadingPagerActivity.this, mChapterText.getChapterUrl(), mChapterText.getBookId());
                    finish();
                }
                break;
            case R.id.iv_setting:
                // TODO: 2017/3/3  show setting popWindow
                break;
        }
    }

    @Bind(R.id.tv_battery)
    TextView tvBattery;
    class BatteryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_BATTERY_CHANGED){
                int current = intent.getExtras().getInt("level");
                int total = intent.getExtras().getInt("scale");
                tvBattery.setText(current*100 / total + "");
            }
        }
    }

    class TimeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_TIME_TICK){
                initTimeText();
            }
        }
    }

    private void initTimeText() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        tvTime.setText(format.format(new Date(System.currentTimeMillis())));
    }

}
