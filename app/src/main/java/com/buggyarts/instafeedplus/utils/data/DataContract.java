package com.buggyarts.instafeedplus.utils.data;

import android.provider.BaseColumns;


/**
 * Created by mayank on 2/16/18
 */

public class DataContract {

    public static class ArticleEntry implements BaseColumns {

        public static String TABLE_NAME = "BOOKMARKED";
        public static String ID = "_id";
        public static String ARTICLE_JSON = "article_json";

    }
}
