package com.buggyarts.instafeedplus.utils;

import android.os.Environment;

import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mayank on 1/6/18
 */

public class Constants {

    public static final String SCIENCE = "science";
    public static final String TECHNOLOGY = "technology";
    public static final String SPORTS = "sports";
    public static final String BUSINESS = "business";
    public static final String GENERAL = "general";
    public static final String ENTERTAINMENT = "entertainment";
    public static final String HEALTH = "health";

    public static final String NEWS_API_KEY = "23bd594449b9494b8b383976f5f0b17f";
    public static final String YOUTUBE_API_KEY = "AIzaSyDjcbKSAOvPZkR96NgJgwcuu66-VxqgzXc";
    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final String PRIVACY_POLICY_URL = "https://krmayank911.github.io/buggyartsWeb/";
    public static final String TOP_HEADLINES = "top-headlines?";
    public static final String EVERYTHING = "everything?";
    public static final String SOURCE = "sources=";
    public static final String ALLSOURCES = "sources?";

    public static final String[] CATEGORIES = {BUSINESS, ENTERTAINMENT, GENERAL, HEALTH, SCIENCE, SPORTS, TECHNOLOGY};
    public static ArrayList<Source> SOURCES;

    public static final String CONSUMER_KEY = "e1jmTivMZwMJ1J2vcOWdNTapC";
    public static final String CONSUMER_SECRET = "aEkAYtUmeBYSeTQvDZcaeLOdzf4xeVMaS4b81bNddjcWjofzoY";
    public static final String TWITTER_AUTH_TOKEN = "950291305065725952-6F5OWvcNep0PfHZz2uock0mzjf94YLS";
    public static final String TWITTER_AUTH_SECRET = "Uslv9ZvXrB0pfFng1tFvsGSk7IYFCMMv2BBVSz7I36avi";

    public static ArrayList<String> CATEG_S;

    public static final String[] languages_options_abr = {"ar", "de", "en", "es", "fr", "he", "it",
            "nl", "no", "pt", "ru", "se", "ud", "zh"};
    public static final String[] languages = {"Select Language", "Arabic", "German", "English", "French", "Hebrew", "Italian",
            "Dutch", "Norwegian", "Portuguese", "Russian", "Northern Sami", "Urdu", "Chinese"};

    public static final String[] country_options_abr = { "ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de",
            "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no",
            "nz", "ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us", "ve", "za"};

    public static final String[] countries = {"Select Country", "United Arab Emirates", "Argentina", "Austria", "Australia", "Belgium", "Bulgaria",
            "Brazil", "Canada", "Switzerland", "China", "Colombia", "Cuba", "Czech Republic", "Germany",
            "Egypt", "France", "United Kingdom", "Greece", "Hong Kong", "Hungary", "Indonesia", "Ireland", "Israel", "India", "Italy",
            "Japan", "Republic Of Korea", "Lithuania", "Latvia", "Morocco", "Mexico", "Malaysia", "Nigeria", "Netherlands", "Norway",
            "New Zealand", "Philippines", "Poland", "Portugal", "Romania", "Serbia", "Russian Federation", "Saudi Arabia",
            "Sweden", "Singapore", "Slovenia", "Slovakia", "Thailand", "Turkey", "Taiwan", "Ukraine", "United States", "Venezuela", "South Africa"};

    public static final String[] countriesInEurope = {"at","be", "bg","cz","de","eg", "fr", "gb",
            "gr","ie", "il","it","nl", "no","pl", "pt", "ro","se","si", "sk","ua"};

    public static final String[] countriesINAsia = {"in","ae","jp","kr","cn","hk","id","my","ru","sa","sg","th"};

    public static File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
            "capturedScreenShot.jpg");

    public static String TRENDING_TITLE = null;
    public static String TRENDING_SUB_TITLE = null;

    public static String RELATIONSHIP_TITLE = null;
    public static String RELATIONSHIP_SUB_TITLE = null;

    public static String ADMOB_APP_ID_DUMMY = "ca-app-pub-3940256099942544~3347511713";
    public static String ADMOB_INTERSTITIAL_AD_ID_DUMMY = "ca-app-pub-3940256099942544/1033173712";

    public static String ADMOB_APP_ID = "ca-app-pub-3247474976237255~1815393488";
    public static String ADMOB_INTERSTITIAL_AD_ID = "ca-app-pub-3247474976237255/6296140492";

    public static int INTERSTITIAL_AD_GAP = 1;

    public static InterstitialAd INTERSTITIAL_AD;

    public static final int ITEM_TYPE_STORY_LIST = 5;
    public static final int ITEM_TYPE_ADVERTISEMENT = 8;
    public static final int ITEM_TYPE_VIDEO = 9;

    public static final int ITEM_TYPE_CATEGORY_LIST = 56;
    public static final int ITEM_TYPE_NEWS_FEED_LIST = 57;

    public static final int ITEM_TYPE_CATEGORY = 86;
    public static final int ITEM_TYPE_NEWS_FEED = 87;

    public static final int FEED_TYPE_CAT = 100;
    public static final int FEED_TYPE_LOADER = 109;
    public static final int FEED_TYPE_NA_MEDIUM = 101;
    public static final int FEED_TYPE_NA_SHORT = 102;
    public static final int FEED_TYPE_PAGER = 103;

    public static final int FEED_TYPE_PAGE_HEADER = 104;
    public static final int FEED_TYPE_HEADER = 105;
    public static final int FEED_TYPE_MORE = 106;

    public static final String[] IF_TOPICS = {"IF_WORLD","IF_INDIA","IF_UK","IF_US","IF_ASIA","IF_EUROPE"};
}
