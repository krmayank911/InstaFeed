package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.buggyarts.instafeedplus.R;
import com.facebook.shimmer.ShimmerFrameLayout;


public class FeedLoader extends FrameLayout{

    ShimmerFrameLayout shimmerLayout;
    LinearLayout categoryLoader;
    LinearLayout feedLoader;

    public FeedLoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_loader,this,true);

        shimmerLayout = findViewById(R.id.shimmerLayout);
        categoryLoader = findViewById(R.id.category_loader);
        feedLoader = findViewById(R.id.feed_loader);

        categoryLoader.setVisibility(GONE);

    }

    public void showHomeFeedLoader(){
        feedLoader.setVisibility(VISIBLE);
        categoryLoader.setVisibility(VISIBLE);
        startShimmer();
    }

    public void hideHomeFeedLoader(){
        stopShimmer();
        categoryLoader.setVisibility(GONE);
        feedLoader.setVisibility(GONE);
    }

    public void showFeedLoader(){
        startShimmer();
    }

    public void hideFeedLoader(){
        stopShimmer();
    }

    void startShimmer(){
        shimmerLayout.startShimmer();
    }

    void stopShimmer(){
        shimmerLayout.stopShimmer();
    }
}
