package com.xfwang.bookreading.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaofeng on 2017/2/3.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "bookShelf.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "book_entity";

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String spl = "create table " + TABLE_NAME +"(_id Integer primary key autoincrement," +
                "bookId text," +
                "bookName text," +
                "latestChapterName text," +
                "lastUpdateTime text," +
                "chapterNum text," +
                "bookIconUrl text," +
                "firstChapterIndex text)";
        sqLiteDatabase.execSQL(spl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
