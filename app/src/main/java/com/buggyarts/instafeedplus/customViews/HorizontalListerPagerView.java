package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.pagerAdapters.CardPagerAdapter;
import com.buggyarts.instafeedplus.customClasses.ShadowTransformer;

import java.util.ArrayList;

public class HorizontalListerPagerView extends FrameLayout implements ViewPager.OnPageChangeListener, CardPagerAdapter.Callback {

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private TextView itemCount;

    private Context mContext;
    private Callback callback;

    public interface Callback{
        void onPageCellClick(NewsSpecial newsSpecial);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public ArrayList<NewsSpecial> cards = new ArrayList<>();


    public HorizontalListerPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.hl_pager_view,this,true);

        mViewPager = findViewById(R.id.viewPager);
        itemCount = findViewById(R.id.itemCount);
        itemCount.setVisibility(GONE);
        mContext = context;

    }

    public void setupData(ArrayList<NewsSpecial> itemList){

        cards = itemList;

        mCardAdapter = new CardPagerAdapter(mContext);
        mCardAdapter.setCallback(this);

        for(NewsSpecial item : cards){
            mCardAdapter.addCardItem(item);
        }

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(this);

        mCardShadowTransformer.enableScaling(true);

//        String textCount = 1 + "/" + cards.size();
//        itemCount.setText(textCount);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String textCount = position + 1 + "/" + cards.size();
        itemCount.setText(textCount);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageCellClick(NewsSpecial newsSpecial) {
        callback.onPageCellClick(newsSpecial);
    }
}
