package com.buggyarts.instafeedplus;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.buggyarts.instafeedplus.Models.AppVersion;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.IFNotificationObject;
import com.buggyarts.instafeedplus.activity.ArticleListActivity;
import com.buggyarts.instafeedplus.activity.SearchNewsActivity;
import com.buggyarts.instafeedplus.activity.VideoPayerActivity;
import com.buggyarts.instafeedplus.adapters.MainPagerAdapter;
import com.buggyarts.instafeedplus.customViews.CustomDialog;
import com.buggyarts.instafeedplus.customViews.SimpleToolBar;
import com.buggyarts.instafeedplus.fragments.Bookmarks;
import com.buggyarts.instafeedplus.fragments.CategoriesFragment;
import com.buggyarts.instafeedplus.fragments.FargHomeFeed;
import com.buggyarts.instafeedplus.fragments.StoriesFragment;
import com.buggyarts.instafeedplus.fragments.TopFeeds;
import com.buggyarts.instafeedplus.fragments.TrendingFeeds;
import com.buggyarts.instafeedplus.rest.ApiClient;
import com.buggyarts.instafeedplus.rest.ApiInterface;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Constants;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buggyarts.instafeedplus.utils.Constants.ADMOB_APP_ID_DUMMY;
import static com.buggyarts.instafeedplus.utils.Constants.ADMOB_INTERSTITIAL_AD_ID_DUMMY;
import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;
import static com.buggyarts.instafeedplus.utils.Constants.INTERSTITIAL_AD;
import static com.buggyarts.instafeedplus.utils.Constants.PRIVACY_POLICY_URL;

public class HomeTabActivity extends AppCompatActivity implements SimpleToolBar.TopBarCallback, OnCompleteListener<InstanceIdResult>, CustomDialog.Callback {

    TopFeeds fragmentTopFeeds;
    CategoriesFragment fragmentCategories;
    TrendingFeeds fragmentTrendingFeeds;
    StoriesFragment fragmentStories;
    FargHomeFeed fragHomeFeed;
    Bookmarks bookmarks;

    ViewPager viewPager;
    MainPagerAdapter adapter;
    FragmentManager fragmentManager;
    SimpleToolBar toolBar;
    CustomDialog customDialog;

    AHBottomNavigation bottomNavigationView;

    private static int PREFERENCE_REQUEST = 12;
    private static int AD_REQUEST = 31;
    private int FLAG_RATE = 22;
    private int FLAG_UPDATE = 23;
    boolean onBoarding = false;
    String SELECTED_COUNTRY;
    String previouslySelectedCountry = "";
    ArrayList<String> subTopics = new ArrayList<>();

    String TAG = HomeTabActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab2);
        Fabric.with(this, new Crashlytics());

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        checkUserPreferences();

        setupToolbar();
        setupCustomDialog();

        MobileAds.initialize(this,ADMOB_APP_ID_DUMMY);

        INTERSTITIAL_AD = new InterstitialAd(this);
        INTERSTITIAL_AD.setAdUnitId(ADMOB_INTERSTITIAL_AD_ID_DUMMY);
        INTERSTITIAL_AD.loadAd(new AdRequest.Builder().build());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(this);

        fragmentManager = getSupportFragmentManager();
        if (fragHomeFeed == null) {
            fragHomeFeed = new FargHomeFeed();
        }
        loadFragment(fragHomeFeed);
        setupBottomNav();

        Intent intent = getIntent();
        if(intent != null){
            resolveIntents(intent);
        }

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        if (preferences.contains("asked_before")) {
            SELECTED_COUNTRY = preferences.getString("country", "in");
            previouslySelectedCountry = SELECTED_COUNTRY;
            addSubTopics();
            for(String topic : subTopics){
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(getResources().getString(R.string.msg_subscribed))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });

        INTERSTITIAL_AD.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                INTERSTITIAL_AD.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        if (preferences.contains("asked_before")) {
            SELECTED_COUNTRY = preferences.getString("country", "in");

            if(!previouslySelectedCountry.equals(SELECTED_COUNTRY)) {

                for(String topic: subTopics){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                }

                addSubTopics();

                for (String topic : subTopics) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if(intent != null){
//            resolveIntents(intent);
//        }
    }

    public void resolveIntents(Intent intent){

        if(intent.hasExtra(getResources().getString(R.string.is_notification))){

            if(intent.hasExtra(getResources().getString(R.string.notification_type_browser))){
                String dataUrl = intent.getStringExtra(getResources().getString(R.string.notification_type_browser));

                if(dataUrl != null) {
                    if (dataUrl.length() > 0) {

                        Intent browserIntent = new Intent(HomeTabActivity.this, BrowserActivity.class);
                        intent.putExtra("visit", dataUrl);
                        intent.putExtra(getResources().getString(R.string.show_ad_on_back),true);
                        startActivityForResult(browserIntent,AD_REQUEST);
                        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_hold);
                    }
                }

            }else if(intent.hasExtra(getResources().getString(R.string.notification_type_cards))){
                int pageIndex = Integer.parseInt(intent.getStringExtra(getResources().getString(R.string.notification_type_cards)));
                openHomeFragmentForCards(pageIndex);

            }else if(intent.hasExtra(getResources().getString(R.string.notification_type_trending))){

                openTrendingTab();

            }else if(intent.hasExtra(getResources().getString(R.string.notification_type_magazine))){

                if(intent.getStringExtra(getResources().getString(R.string.notification_type_magazine)) != null) {
                    openStoriesTab(Integer.parseInt(intent.getStringExtra(getResources().getString(R.string.notification_type_magazine))));
                }else {
                    openStoriesTab();
                }

            }else if(intent.hasExtra(getResources().getString(R.string.notification_type_category))){

                int index = Integer.parseInt(intent.getStringExtra(getResources().getString(R.string.notification_type_category)));
                openCategoryNotification(index);

            }else if(intent.hasExtra(getResources().getString(R.string.notification_object))){

                IFNotificationObject notificationObject;
                String notificationObjectJson = intent.getStringExtra(getResources().getString(R.string.notification_object));
                Gson gson = new Gson();
                notificationObject = gson.fromJson(notificationObjectJson,IFNotificationObject.class);

                if(notificationObject != null) {
                    if(notificationObject.getType().equals("0")){
                        if (notificationObject.getDataUrl() != null) {
                            if (notificationObject.getDataUrl().length() > 0) {
                                Intent browserIntent = new Intent(this, BrowserActivity.class);
                                browserIntent.putExtra("visit", notificationObject.getDataUrl());
                                browserIntent.putExtra(getResources().getString(R.string.show_ad_on_back), true);
                                startActivityForResult(browserIntent, AD_REQUEST);
                                overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_hold);
                            }
                        }
                    } else if (notificationObject.getType().equals("1")) {
                        openHomeFragmentForCards(Integer.parseInt(notificationObject.getTypeIndex()));
                    } else if (notificationObject.getType().equals("2")) {
                        openTrendingTab();
                    } else if (notificationObject.getType().equals("3")) {
                        if(notificationObject.getTypeIndex() != null) {
                            openStoriesTab(Integer.parseInt(notificationObject.getTypeIndex()));
                        }else {
                            openStoriesTab();
                        }
                    } else if (notificationObject.getType().equals("4")) {
                        openCategoryNotification(Integer.parseInt(notificationObject.getTypeIndex()));
                    }
                }

            }else {
                Log.d(TAG, "resolveIntents: No intent match");
            }
        }
    }

    private void openCategoryNotification(int index){

        if(index >=0 && index < CATEGORIES.length) {
            Category category = new Category(CATEGORIES[index]);

            Gson gson = new Gson();
            String categoryJson = gson.toJson(category);
            Intent intent = new Intent(this, ArticleListActivity.class);
            intent.putExtra("categoryJson", categoryJson);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_hold);
        }
    }

    private void openHomeFragmentForCards(int pageIndex){

        if(fragHomeFeed != null){
            loadFragment(fragHomeFeed);
            fragHomeFeed.showCards(this, pageIndex);
        }else {
            fragHomeFeed = new FargHomeFeed();
            loadFragment(fragHomeFeed);
            fragHomeFeed.showCards(this, pageIndex);
        }
        bottomNavigationView.setCurrentItem(0);
    }

    private void openTrendingTab(){
        if(fragmentTrendingFeeds != null){
            loadFragment(fragmentTrendingFeeds);
        }else {
            fragmentTrendingFeeds = new TrendingFeeds();
            loadFragment(fragmentTrendingFeeds);
        }
        bottomNavigationView.setCurrentItem(2);
    }

    private void openStoriesTab(){
        if(fragmentStories != null){
            loadFragment(fragmentStories);
        }else {
            fragmentStories = new StoriesFragment();
            loadFragment(fragmentStories);
        }
        bottomNavigationView.setCurrentItem(3);
    }

    private void openStoriesTab(int index){
        if(fragmentStories != null){
            fragmentStories.setTabIndex(index);
            loadFragment(fragmentStories);
        }else {
            fragmentStories = new StoriesFragment();
            fragmentStories.setTabIndex(index);
            loadFragment(fragmentStories);
        }
        bottomNavigationView.setCurrentItem(3);
    }

    public void setupToolbar(){
        toolBar = findViewById(R.id.toolBar);
        toolBar.getBackButton().setVisibility(View.GONE);
        toolBar.getTitleLabel().setText(getResources().getText(R.string.app_name));
        toolBar.setTopbarListener(this);
    }

    private void showToolBar(Boolean bool){
        if(!bool) {
            toolBar.setVisibility(View.GONE);
        }else {
            toolBar.setVisibility(View.VISIBLE);
        }
    }

    public void checkUserPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        if (!preferences.contains("asked_before")) {
            onBoarding = true;
            Intent intent = new Intent(HomeTabActivity.this, IntroActivity.class);
            intent.putExtra("onBoarding",onBoarding);
            startActivity(intent);
        }
    }

    public void setupBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Feeds", R.drawable.if_home, R.color.colorWhite);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.title_bookmarks), R.drawable.if_bookmark, R.color.colorWhite);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.trending_title), R.drawable.if_hot, R.color.colorWhite);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.magazine_title), R.drawable.if_magazine, R.color.colorWhite);

        // Add items
        bottomNavigationView.addItem(item1);
        bottomNavigationView.addItem(item2);
        bottomNavigationView.addItem(item3);
        bottomNavigationView.addItem(item4);

        bottomNavigationView.setBehaviorTranslationEnabled(true);

        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigationView.setCurrentItem(0);
        // Change colors
        bottomNavigationView.setAccentColor(getResources().getColor(R.color.themeColorSecondaryDark));
        bottomNavigationView.setInactiveColor(getResources().getColor(R.color.themeColorSecondary));


        bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasTabSelected) {
                switch (position) {
                    case 0:
                        showToolBar(true);
                        toolBar.getTitleLabel().setText("InstaFeed+");
                        if (fragHomeFeed == null) {
                            fragHomeFeed = new FargHomeFeed();
                        }
                        loadFragment(fragHomeFeed);
                        return true;
                    case 1:
                        showToolBar(true);
                        toolBar.getTitleLabel().setText(getResources().getString(R.string.title_bookmarks));
                        if (bookmarks == null) {
                            bookmarks = new Bookmarks();
                        }
                        loadFragment(bookmarks);
                        return true;
                    case 2:
                        showToolBar(false);
//                        toolBar.getTitleLabel().setText(getResources().getString(R.string.trending_title));
                        if (fragmentTrendingFeeds == null) {
                            fragmentTrendingFeeds = new TrendingFeeds();
                        }
                        loadFragment(fragmentTrendingFeeds);
                        return true;
                    case 3:
                        showToolBar(false);
//                        toolBar.getTitleLabel().setText(getResources().getString(R.string.magazine_title));
                        if (fragmentStories == null) {
                            fragmentStories = new StoriesFragment();
                        }
                        loadFragment(fragmentStories);
//                        Intent intent = new Intent(MainActivity.this,HotStoriesActivity.class);
//                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PREFERENCE_REQUEST){
                fragHomeFeed = new FargHomeFeed();
                loadFragment(fragHomeFeed);
            }else if(requestCode == AD_REQUEST){
                showAd();
            }
        }
    }

    public void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
//        transaction.commitAllowingStateLoss();
        transaction.commit();
    }

    @Override
    public void backButtonCalled() {

    }

    @Override
    public void onSearchClick() {
        Intent intent = new Intent(this, SearchNewsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    @Override
    public void onPreferencesClick() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivityForResult(intent,PREFERENCE_REQUEST);
    }

    @Override
    public void onBookmarksClick() {
//        if (bookmarks == null) {
//            bookmarks = Bookmarks.newInstance();
//        }
//        loadFragment(bookmarks);


        Intent intent = new Intent(this, VideoPayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRateUsClick() {
        rateApp();
    }

    @Override
    public void onPrivacyPolicyClick() {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("page_title","PRIVACY POLICY");
        intent.putExtra("visit", PRIVACY_POLICY_URL);
        startActivity(intent);
    }

    @Override
    public void onComplete(@NonNull Task<InstanceIdResult> task) {
        if (!task.isSuccessful()) {
            Log.w(TAG, "getInstanceId failed", task.getException());
            return;
        }

        // Get new Instance ID token
        String token = task.getResult().getToken();
        //appendLog(token);

        // Log and toast
        String msg = getResources().getString(R.string.msg_token_fmt) + token;
        Log.d(TAG, msg);
//        Toast.makeText(HomeTabActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void appendLog(String text) {
        File logFile = new File("sdcard/instaFeedLog.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addSubTopics(){

        subTopics.add(Constants.IF_TOPICS[0]);

        if(SELECTED_COUNTRY.equals("in")){
            subTopics.add(Constants.IF_TOPICS[1]);
            subTopics.add(Constants.IF_TOPICS[4]);
        }else if(SELECTED_COUNTRY.equals("us")){
            subTopics.add(Constants.IF_TOPICS[3]);
        }else if(SELECTED_COUNTRY.equals("gb")){
            subTopics.add(Constants.IF_TOPICS[2]);
            subTopics.add(Constants.IF_TOPICS[5]);
        }else {

            for(String country : Constants.countriesInEurope){
                if(SELECTED_COUNTRY.equals(country)){
                    subTopics.add(Constants.IF_TOPICS[5]);
                    break;
                }
            }

            for(String country : Constants.countriesINAsia){
                if(SELECTED_COUNTRY.equals(country)){
                    subTopics.add(Constants.IF_TOPICS[4]);
                    break;
                }
            }

        }
    }

    public void setupCustomDialog(){
        customDialog = findViewById(R.id.customDialog);
        customDialog.setVisibility(View.GONE);
        customDialog.setCallback(this);


        if(AppUtils.getClickCount(this) == 0){
            showRateDialog();
            AppUtils.setClickCount(this,"15");
        }else {
            checkAppVersion();
        }
    }

    public void showRateDialog(){
        customDialog.setFLAG(FLAG_RATE);
        customDialog.getDialogTitle().setText(getResources().getString(R.string.rate_app_title));
        customDialog.getDialogMessage().setText(getResources().getString(R.string.rate_app_message));
        customDialog.getDialogActionNeg().setText(getResources().getString(R.string.rate_app_action_neg));
        customDialog.getDialogActionPos().setText(getResources().getString(R.string.rate_app_action_pos));
        customDialog.setVisibility(View.VISIBLE);
    }

    public void showUpdateDialog(){
        customDialog.setFLAG(FLAG_UPDATE);
        customDialog.getDialogTitle().setText(getResources().getString(R.string.update_app_title));
        customDialog.getDialogImage().setImageDrawable(getResources().getDrawable(R.drawable.update));
        customDialog.getDialogMessage().setText(getResources().getString(R.string.update_app_message));
        customDialog.getDialogActionNeg().setText(getResources().getString(R.string.update_app_action_neg));
        customDialog.getDialogActionPos().setText(getResources().getString(R.string.update_app_action_pos));
        customDialog.getDialogActionNeg().setAlpha(0);
        customDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowOutsideClick() {
        if(customDialog.getFLAG()==FLAG_RATE) {
            customDialog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogActionNeg(int flag) {
        if(customDialog.getFLAG() == FLAG_RATE) {
            customDialog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogActionPos(int flag) {
        if(flag == FLAG_RATE) {
            customDialog.setVisibility(View.GONE);
            rateApp();
        }else if(flag == FLAG_UPDATE){
            rateApp();
        }
    }

    void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void showAd(){
        if (INTERSTITIAL_AD.isLoaded()) {
            INTERSTITIAL_AD.show();
        }else{
            Log.d("InterstitialAd", "failedToLoad");
        }
    }

    public void checkAppVersion(){

        ApiInterface apiService = ApiClient.getNewApiClient().create(ApiInterface.class);
        Call<AppVersion> call = apiService.getAppVersion();

        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                if(response.code() == 200){
                    if(Integer.parseInt(response.body().getVersion()) > BuildConfig.VERSION_CODE){
                        showUpdateDialog();
                    }
                }else{
                    Log.d(TAG, "onResponse: "+ response.code() + " : "+ response.message());
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

}
