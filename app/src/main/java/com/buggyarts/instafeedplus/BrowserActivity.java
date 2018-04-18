package com.buggyarts.instafeedplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.http.*;
import android.widget.Toast;

import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

import static com.buggyarts.instafeedplus.utils.Constants.INTERATITIAL_AD;
import static com.buggyarts.instafeedplus.utils.Constants.INTERSTITIAL_AD_GAP;

public class BrowserActivity extends AppCompatActivity {

    WebView webView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra("visit");

        String base_url = url.replace("https", "").replace("http", "")
                .replace("://", "").replace("www.", "");

        int index = base_url.indexOf('/');

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle(base_url.substring(0, index));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
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

        INTERATITIAL_AD.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                INTERATITIAL_AD.loadAd(new AdRequest.Builder().build());
            }
        });

        webView.loadUrl(url);

        if(INTERSTITIAL_AD_GAP == 0){
            Log.d("InterstitialAd", "call");
            showAd();
            Random r = new Random();
            INTERSTITIAL_AD_GAP = r.nextInt(((6-2)+1)+2);
        }else{
            INTERSTITIAL_AD_GAP--;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(this)) {
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void showAd(){
        if (INTERATITIAL_AD.isLoaded()) {
            INTERATITIAL_AD.show();
        }else{
            Log.d("InterstitialAd", "failedToLoad");
        }
    }

}
