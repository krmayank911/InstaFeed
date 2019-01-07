package com.buggyarts.instafeedplus.customClasses;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.buggyarts.instafeedplus.R;

public class QuickReturnFloaterBehavior extends CoordinatorLayout.Behavior<View> {

    private int distance;
    private Context mContext;

    public QuickReturnFloaterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && distance < 0 || dy < 0 && distance > 0) {
            child.animate().cancel();
            distance = 0;
        }
        distance += dy;
        final int height = child.getHeight() > 0 ? (child.getHeight()) : 600/*update this accordingly*/;
        if (distance > height && child.isShown()) {
            hide(child);
        } else if (distance < 0 && !child.isShown()) {
            show(child);
        }
    }

    private void hide(View view) {

        Animation slideDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_bottom);
        slideDown.setDuration(300);
        view.startAnimation(slideDown);

//        view.setVisibility(View.GONE);// use animate.translateY(height); instead
    }

    private void show(View view) {

        Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
        slideUp.setDuration(300);
        view.startAnimation(slideUp);

//        view.setVisibility(View.VISIBLE);// use animate.translateY(-height); instead
    }

}
