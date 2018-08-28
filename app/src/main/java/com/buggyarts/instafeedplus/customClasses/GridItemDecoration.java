package com.buggyarts.instafeedplus.customClasses;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemDecoration extends RecyclerView.ItemDecoration{
    private int space;
    private boolean showTopPadding;

    public GridItemDecoration(int space) {
        this.space = space;
    }

    public GridItemDecoration(int space, boolean showTopPadding) {
        this.space = space;
        this.showTopPadding = showTopPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (showTopPadding){
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.top = space;
            }else {
                outRect.top = 0 ;
            }
        }else {
            outRect.top = 0 ;
        }
    }
}

