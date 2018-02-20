package com.buggyarts.instafeedplus.utils.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mayank on 2/16/18
 */

public class ArticleDbManager extends SQLiteOpenHelper {

    private static String DB_NAME = "article.db";
    private static int DB_VERSION = 1;

    private String QUERY = "CREATE TABLE " + DataContract.ArticleEntry.TABLE_NAME + " ( " +
            DataContract.ArticleEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            DataContract.ArticleEntry.ARTICLE_JSON + " TEXT NOT NULL " + " )";

    public ArticleDbManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
