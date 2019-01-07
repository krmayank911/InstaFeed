package com.buggyarts.instafeedplus.adapters.pagerAdapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.CardItem;
import com.buggyarts.instafeedplus.Models.news.Block;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customClasses.CardAdapter;
import com.buggyarts.instafeedplus.customClasses.GlideApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter, View.OnClickListener {

    private List<CardView> mViews;
    private List<NewsSpecial> mData;
    private float mBaseElevation;
    private Context mContext;
    private Callback callback;

    public CardPagerAdapter(Context context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.mContext = context;
    }

    public void addCardItem(NewsSpecial item) {
        mViews.add(null);
        mData.add(item);
    }

    public interface Callback{
        void onPageCellClick(NewsSpecial newsSpecial);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.cell_hl_pager, container, false);
        container.addView(view);
        setViews(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
//        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
//        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
//        titleTextView.setText(item.getTitle());
//        contentTextView.setText(item.getText());
    }

    private void setViews(NewsSpecial item, View view){
        ImageView imageView = view.findViewById(R.id.cell_iv);
        TextView labelTextView = view.findViewById(R.id.labelText);
        FrameLayout pageCell = view.findViewById(R.id.page_cell);

        pageCell.setTag(R.string.page_object,item);
        pageCell.setOnClickListener(this);

        labelTextView.setText(item.getPageTitle());

        if(item.getPageThumbnailUrl().length()>4) {
            GlideApp.with(mContext).load("https://www.dailynews.com/wp-content/uploads/2017/09/img_3776.jpg")
                    .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_landscape))
                    .centerInside().into(imageView);
        }else if(item.getCards()){

            Random random = new Random();
            int articleIndex = 0;
            if(item.getCardsList().size() > 0) {
                if (item.getCardsList().size() > 1) {
                    articleIndex = random.nextInt(item.getCardsList().size());
                }

                GlideApp.with(mContext)
                        .load(item.getCardsList().get(articleIndex).getThumbnail_url())
                        .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_landscape))
                        .centerInside().into(imageView);
            }else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder_landscape));
            }

        }else {

            if(item.getBlocks().size() > 0) {
                Random random = new Random();
                int blockIndex = 0;
                if (item.getBlocks().size() > 1) {
                    blockIndex = random.nextInt(item.getBlocks().size());
                }

                Block block = item.getBlocks().get(blockIndex);

                if(block.getCallResult().size() > 0) {
                    int articleIndex = 0;
                    if (block.getCallResult().size() > 1) {
                        articleIndex = random.nextInt(block.getCallResult().size());
                    }

                    GlideApp.with(mContext)
                            .load(block.getCallResult().get(articleIndex).getThumbnail_url())
                            .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_landscape))
                            .centerInside().into(imageView);
                }else {
                    imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder_landscape));
                }
            }else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder_landscape));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.page_cell){
            NewsSpecial newsSpecial = (NewsSpecial) view.getTag(R.string.page_object);
            callback.onPageCellClick(newsSpecial);
        }
    }

}
