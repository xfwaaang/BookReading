package com.xfwang.bookreading.fragment;

import com.xfwang.bookreading.R;

/**
 * Created by xiaofeng on 2017/1/29.
 */

public enum HomeTab {
    BOOKSHELF(0, R.string.books, R.drawable.selector_book_shelf_bg,BooksFragment.class),
    FIND(1, R.string.find, R.drawable.selector_find_bg, FindFragment.class),
    SORT(2, R.string.sort, R.drawable.selector_sort_bg, SortOtherFragment.class),
    ME(3, R.string.me, R.drawable.selector_me_bg, MeFragment.class)
    ;

    private int id;
    private int titleRes;
    private int iconRes;
    private Class<?> cls;

    HomeTab(int id, int titleRes, int iconRes, Class<?> cls) {
        this.id = id;
        this.titleRes = titleRes;
        this.iconRes = iconRes;
        this.cls = cls;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }
}
