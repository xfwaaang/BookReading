package com.xfwang.bookreading.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xfwang.bookreading.utils.LogUtils;
import com.xfwang.bookreading.utils.ScreenUtils;

import static android.R.attr.x;

/**
 * Created by xiaofeng on 2017/2/2.
 * 可以设置点击 left，center，right事件的TextView
 */

public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
        init(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private int mScreenWidth;
    private void init(Context context) {
        mScreenWidth = ScreenUtils.getScreenWidth(context);
    }

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
                    }else if (startX > mScreenWidth*3/8 && startX < mScreenWidth*5/8 ){
                        if (mListener != null){
                            mListener.onTouchCenter();
                        }
                    }else {
                        if (mListener != null){
                            mListener.onTouchRight();
                        }
                    }
                }
                break;
        }
        return true;
    }
}
