package com.xfwang.bookreading.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xfwang.bookreading.bean.BookEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofeng on 2017/2/3.
 */

public class DBManager {
    private static DBHelper mDBHelper;

    public static DBHelper getInstance(Context context){
        if (mDBHelper == null){
            synchronized (DBHelper.class){
                if (mDBHelper == null){
                    mDBHelper = new DBHelper(context);
                }
            }
        }
        return mDBHelper;
    }

    /**
     * 清空表
     * @param context
     */
    public static void clear(Context context){
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME,null,null);
        db.close();
    }

    /**
     * 保存bookEntityList到表中
     * @param context
     * @param bookEntityList
     */
    public static void put(Context context,List<BookEntity> bookEntityList){
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        for (int i = 0; i < bookEntityList.size(); i++) {
            BookEntity bookEntity = bookEntityList.get(i);
            ContentValues values = new ContentValues();
            values.put("_id",i);
            values.put("bookId",bookEntity.getBookId());
            values.put("bookName",bookEntity.getBookName());
            values.put("latestChapterName",bookEntity.getLatestChapterName());
            values.put("lastUpdateTime",bookEntity.getLastUpdateTime());
            values.put("chapterNum",bookEntity.getChapterNum());
            values.put("bookIconUrl",bookEntity.getBookIconUrl());
            values.put("firstChapterIndex",bookEntity.getFirstChapterIndex());
            db.insert(DBHelper.TABLE_NAME,null,values);
        }
        db.close();
    }

    /**
     * 从表中获取bookEntityList
     * @param context
     * @return
     */
    public static List<BookEntity> get(Context context){
        List<BookEntity> bookEntityList = new ArrayList<>();
        SQLiteDatabase db = getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME,null);
        while (cursor.moveToNext()){
            BookEntity bookEntity = new BookEntity();
            bookEntity.setBookId(cursor.getString(cursor.getColumnIndex("bookId")));
            bookEntity.setBookName(cursor.getString(cursor.getColumnIndex("bookName")));
            bookEntity.setLatestChapterName(cursor.getString(cursor.getColumnIndex("latestChapterName")));
            bookEntity.setLastUpdateTime(cursor.getString(cursor.getColumnIndex("lastUpdateTime")));
            bookEntity.setChapterNum(cursor.getString(cursor.getColumnIndex("chapterNum")));
            bookEntity.setBookIconUrl(cursor.getString(cursor.getColumnIndex("bookIconUrl")));
            bookEntity.setFirstChapterIndex(cursor.getString(cursor.getColumnIndex("firstChapterIndex")));
            bookEntityList.add(bookEntity);
        }
        db.close();
        return bookEntityList;
    }
}
