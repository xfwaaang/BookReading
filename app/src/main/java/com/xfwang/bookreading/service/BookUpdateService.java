package com.xfwang.bookreading.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.LoginActivity;
import com.xfwang.bookreading.adapter.BookShelfListAdapter;
import com.xfwang.bookreading.api.ApiHelper;
import com.xfwang.bookreading.bean.BookEntity;
import com.xfwang.bookreading.bean.MyUser;
import com.xfwang.bookreading.db.DBManager;
import com.xfwang.bookreading.fragment.BooksFragment;
import com.xfwang.bookreading.utils.LogUtils;
import com.xfwang.bookreading.utils.SPUtils;
import com.xfwang.bookreading.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobUser;

import static com.xfwang.bookreading.fragment.BooksFragment.SP_BOOK_SUBSCRIBED_IDS;

/**
 * Created by xiaofeng on 2017/3/5.
 */

public class BookUpdateService extends Service {
    private ApiHelper mApiHelper;
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();


    private List<BookEntity> mBookEntityList;
    private List<BookEntity> mBookEntityOldList;
    private List<BookEntity> mBookEntityUpdateList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mApiHelper = ApiHelper.getInstance();
        checkBookUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                checkBookUpdate();
            }else if (msg.what == 1){
                if (mBookEntityList != null)
                    compareNewAndOld();
            }
        }
    };

    private void compareNewAndOld() {
        ToastUtils.shortToast(getApplicationContext(),"compare");

        for (int i=0; i<mBookEntityList.size(); i++){
            BookEntity bookEntity = mBookEntityList.get(i);
            for (int j=0; j<mBookEntityOldList.size(); j++){
                BookEntity bookEntityOld = mBookEntityOldList.get(j);
                if (bookEntity.getBookId() == bookEntityOld.getBookId() && Integer.valueOf(bookEntity.getChapterNum()) > Integer.valueOf(bookEntityOld.getChapterNum())){
                    //该书籍有更新
                    mBookEntityUpdateList.add(bookEntity);
                }
            }
        }

        //notify book update
        notifyBookUpdate();
    }

    private void checkBookUpdate() {
        boolean isLogin = (boolean) SPUtils.get(getApplicationContext(),LoginActivity.SP_IS_LOGIN,false);
        boolean isHave = checkBookShelf(isLogin);
        mBookEntityOldList = DBManager.get(getApplicationContext());
        mBookEntityUpdateList = new ArrayList<>();

        if (isHave){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mBookEntityList = mApiHelper.getBookEntityList(mBookDetailUrlArray);
                    mHandler.sendEmptyMessage(1);
                }
            };

            mThreadPool.execute(runnable);
        }

        //十分钟刷新一次
        mHandler.sendEmptyMessageDelayed(0,1000*60*10);

        ToastUtils.shortToast(getApplicationContext(),"check update");
    }

    private void notifyBookUpdate() {
        if (mBookEntityUpdateList.size() == 0) return;

        sendBroadcast(new Intent(BooksFragment.ACTION_BOOK_UPDATE));

        int num = mBookEntityUpdateList.size();
        String firstBookName = mBookEntityUpdateList.get(0).getBookName();
        String latestChapterName = mBookEntityUpdateList.get(0).getLatestChapterName();
        String title = num == 1 ? firstBookName + "更新了！" : firstBookName + "等" + num + "本书更新了！";

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(latestChapterName)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis());

        manager.notify(0,builder.build());

        ToastUtils.shortToast(getApplicationContext(),"notify:" + num);
    }

    private String mBookIds;
    private String[] mBookDetailUrlArray;
    public boolean checkBookShelf(boolean isLogin){
        if (isLogin){
            String ids = BmobUser.getCurrentUser(MyUser.class).getBookIds();
            mBookIds = ids == null ? "" : ids.replace("null","");
        }else {
            mBookIds = (String) SPUtils.get(getApplicationContext(),SP_BOOK_SUBSCRIBED_IDS,"");
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
