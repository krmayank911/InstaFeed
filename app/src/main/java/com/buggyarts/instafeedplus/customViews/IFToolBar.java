package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

public class IFToolBar extends Toolbar implements View.OnClickListener{

    private TextView titleLabel;
    private ImageView backButton;
    private FrameLayout backGroundView;

    private Callback callback;

    public interface Callback {
        void backButtonCalled();
    }

    public void setCallback(Callback mCallback) {
        this.callback = mCallback;
    }

    public IFToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.if_tool_bar, this, true);
        titleLabel = findViewById(R.id.title);
        backGroundView = findViewById(R.id.backgroundView);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.backButton){
            callback.backButtonCalled();
        }
    }

    public FrameLayout getBackGroundView() {
        return backGroundView;
    }

    public void setBackGroundView(FrameLayout backGroundView) {
        this.backGroundView = backGroundView;
    }

    public ImageView getBackButton() {
        return backButton;
    }

    public TextView getTitleLabel() {
        return titleLabel;
    }

    public void updateToolBarAlpha(float alpha, Context context){
        //float colorPercentage = 255 * alpha;
        backGroundView.setAlpha(alpha);
        titleLabel.setAlpha(alpha);
        if (alpha  < 0.2){
            backButton.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            backButton.setBackground(context.getResources().getDrawable(R.drawable.circular_feedback_inverse));
            titleLabel.setTextColor(ContextCompat.getColor(context,R.color.transparent));
        }else {
            backButton.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            backButton.setBackground(context.getResources().getDrawable(R.drawable.circular_feedback_inverse));
            titleLabel.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
        }
    }


}
