package com.xfwang.bookreading.bean;

/**
 * Created by xiaofeng on 2017/1/28.
 * 书架页面书籍实体类
 */

public class BookEntity {
    private String bookName;                //书名
    private String bookAuthor;              //作者
    private String latestChapterName;       //最新章节名称
    private String lastUpdateTime;          //最后更新时间
    private String currChapterPos;          //当前阅读到的章节位置
    private String chapterNum;              //章节总数
    private String firstChapterIndex;

    public String getFirstChapterIndex() {
        return firstChapterIndex;
    }

    public void setFirstChapterIndex(String firstChapterIndex) {
        this.firstChapterIndex = firstChapterIndex;
    }

    private String currChapterUrl;          //当前阅读到的章节链接

    private String bookId;                  //书籍的唯一标识id

    private String bookIconUrl;             //书籍icon链接

    private String chapter;

    private String chapterUrl;              //

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getBookIconUrl() {
        return bookIconUrl;
    }

    public void setBookIconUrl(String bookIconUrl) {
        this.bookIconUrl = bookIconUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCurrChapterUrl() {
        return currChapterUrl;
    }

    public void setCurrChapterUrl(String currChapterUrl) {
        this.currChapterUrl = currChapterUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getLatestChapterName() {
        return latestChapterName;
    }

    public void setLatestChapterName(String latestChapterName) {
        this.latestChapterName = latestChapterName;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCurrChapterPos() {
        return currChapterPos;
    }

    public void setCurrChapterPos(String currChapterPos) {
        this.currChapterPos = currChapterPos;
    }

    public String getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(String chapterNum) {
        this.chapterNum = chapterNum;
    }
}
