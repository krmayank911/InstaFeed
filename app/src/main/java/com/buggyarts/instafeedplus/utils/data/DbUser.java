package com.buggyarts.instafeedplus.utils.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mayank on 2/16/18
 */

public class DbUser {

    private String json_string = "null";
    private Context context;
    private ArticleDbManager dbManager;

    public DbUser(Context context) {
        this.context = context;
    }

    public DbUser(Context context, String json_string) {
        this.json_string = json_string;
        this.context = context;
    }

    public String getJson_string() {
        return json_string;
    }

    public void setJson_string(String json_string) {
        this.json_string = json_string;
    }

    public void addArticleInDB() {
        dbManager = new ArticleDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataContract.ArticleEntry.ARTICLE_JSON, json_string);
        db.insert(DataContract.ArticleEntry.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> readArticlesFromDB() {
        ArrayList<String> articles = new ArrayList<>();
        dbManager = new ArticleDbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String[] columns = {DataContract.ArticleEntry.ARTICLE_JSON};
        Cursor cursor = db.query(DataContract.ArticleEntry.TABLE_NAME, columns, null, null, null, null, DataContract.ArticleEntry.ID);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String article = cursor.getString(cursor.getColumnIndex(DataContract.ArticleEntry.ARTICLE_JSON));
//                Log.d("readArticlesFromDB: ",article);
                articles.add(article);
            }
        }
        cursor.close();
        return articles;
    }

    public void deleteArticleFromDB(String articleJson) {
        dbManager = new ArticleDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        String[] argument = {articleJson};
        db.delete(DataContract.ArticleEntry.TABLE_NAME, DataContract.ArticleEntry.ARTICLE_JSON + "=?", argument);
        db.close();
    }

}
