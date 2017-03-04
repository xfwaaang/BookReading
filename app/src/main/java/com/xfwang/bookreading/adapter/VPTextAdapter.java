package com.xfwang.bookreading.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xfwang.bookreading.R;
import com.xfwang.bookreading.activity.TextReadingPagerActivity;
import com.xfwang.bookreading.bean.ChapterText;
import com.xfwang.bookreading.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofeng on 2017/2/27.
 */

public class VPTextAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private List<View> mViewList;

    private Context mContext;

    public VPTextAdapter(Context context, String[] textList,float textSize) {
        mInflater = LayoutInflater.from(context);
        mContext = context;

        initView(textList,textSize);
    }

    private void initView(String[] textList, float textSize) {
        if (textList == null)   return;

        mViewList = new ArrayList<>();
        for (int i = 0; i < textList.length; i++) {
            View view = mInflater.inflate(R.layout.vp_text_reading_layout,null,false);
            TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvContent.setText(textList[i]);
            tvContent.setTextSize(textSize);

            mViewList.add(view);
        }

    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
