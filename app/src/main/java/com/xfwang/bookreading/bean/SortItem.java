package com.xfwang.bookreading.bean;

/**
 * Created by xiaofeng on 2017/2/4.
 */

public class SortItem {
    private int viewType;
    private String bookName;
    private String bookUrl;
    private String bookIconUrl;
    private String bookAuthor;
    private String bookInfo;

    private String chapterName;
    private String chapterUrl;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getBookIconUrl() {
        return bookIconUrl;
    }

    public void setBookIconUrl(String bookIconUrl) {
        this.bookIconUrl = bookIconUrl;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(String bookInfo) {
        this.bookInfo = bookInfo;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }
}
