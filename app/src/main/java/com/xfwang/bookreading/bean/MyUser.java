package com.xfwang.bookreading.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by xiaofeng on 2017/2/6.
 */

public class MyUser extends BmobUser {
    private String nick;
    private String bookIds;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getBookIds() {
        return bookIds;
    }

    public void setBookIds(String bookIds) {
        this.bookIds = bookIds;
    }
}
