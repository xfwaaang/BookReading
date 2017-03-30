package com.xfwang.bookreading.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xfwang.bookreading.utils.ScreenUtils;

/**
 * Created by xiaofeng on 2017/1/20.
 * //可以禁止左右滑动
 */

public class MyViewPager extends ViewPager  {
    private boolean noScroll = false;

    public MyViewPager(Context context) {
        super(context);
        init(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private int mScreenWidth;
    private void init(Context context) {
        mScreenWidth = ScreenUtils.getScreenWidth(context);
    }

//    /**
//     * @param noScroll
//     * 设置禁止左右滑动
//     */
//    public void setNoScroll(boolean noScroll){
//        this.noScroll = noScroll;
//    }




    public interface OnMyTouchListener{
        void onTouchLeft();
        void onTouchCenter();
        void onTouchRight();
    }

    private OnMyTouchListener mListener;
    public void setMyOnTouchListener(OnMyTouchListener listener){
        mListener = listener;
    }

    private int startX;
    private int startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int dX = (int) event.getRawX() - startX;
                int dy = (int) event.getRawY() - startY;
                if (dX == 0 && dy == 0){
                    if (startX > 0 && startX <= mScreenWidth*3/8){
                        if (mListener != null){
                            mListener.onTouchLeft();
                        }

                        setCurrentItem(getCurrentItem() - 1);
                    }else if (startX > mScreenWidth*3/8 && startX < mScreenWidth*5/8 ){
                        if (mListener != null){
                            mListener.onTouchCenter();
                        }
                    }else {
                        if (mListener != null){
                            mListener.onTouchRight();
                        }

                        setCurrentItem(getCurrentItem() + 1);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (noScroll){
//            return false;
//        }else {
//            return super.onTouchEvent(ev);
//        }
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (noScroll){
//            return false;
//        }else {
//            return super.onInterceptTouchEvent(ev);
//        }
//    }
}
