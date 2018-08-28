package com.buggyarts.instafeedplus;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.http.*;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.buggyarts.instafeedplus.customViews.LoadingToolBar;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static com.buggyarts.instafeedplus.utils.Constants.INTERATITIAL_AD;
import static com.buggyarts.instafeedplus.utils.Constants.INTERSTITIAL_AD_GAP;

public class BrowserActivity extends AppCompatActivity implements LoadingToolBar.TopBarCallback {

    LoadingToolBar loadingToolBar;
    WebView webView;
    LinearLayout intermediateLoaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra("visit");

        String base_url = url.replace("https", "").replace("http", "")
                .replace("://", "").replace("www.", "");

        int index = base_url.indexOf('/');

        loadingToolBar = findViewById(R.id.toolBar);
        loadingToolBar.getProgressContainer().setVisibility(View.GONE);
        loadingToolBar.getTitleLabel().setText(base_url.substring(0,index));
        loadingToolBar.setTopbarListener(this);

        intermediateLoaderView = findViewById(R.id.intermediateLoaderView);
        intermediateLoaderView.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.web_view);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingToolBar.getProgressContainer().setVisibility(View.VISIBLE);
//                intermediateLoaderView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                intermediateLoaderView.setVisibility(View.GONE);
            }


        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                loadingToolBar.getProgressBar().setProgress(newProgress);

                if(newProgress == 100){
                    loadingToolBar.getProgressContainer().setVisibility(View.GONE);
                }

            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }

        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //improve webView performance
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);

        webView.loadUrl(url);

        try {
            URL pageUrl = new URL(url);
            loadingToolBar.getTitleLabel().setText(pageUrl.getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        INTERATITIAL_AD.setAdListener(new AdListener(){
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                INTERATITIAL_AD.loadAd(new AdRequest.Builder().build());
//            }
//        });

//        if(INTERSTITIAL_AD_GAP == 0){
//            Log.d("InterstitialAd", "call");
//            showAd();
//            Random r = new Random();
//            INTERSTITIAL_AD_GAP = r.nextInt(((6-2)+1)+2);
//        }else{
//            INTERSTITIAL_AD_GAP--;
//        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(this)) {
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void backButtonCalled() {
        this.finish();
    }

    //    public void showAd(){
//        if (INTERATITIAL_AD.isLoaded()) {
//            INTERATITIAL_AD.show();
//        }else{
//            Log.d("InterstitialAd", "failedToLoad");
//        }
//    }

}
