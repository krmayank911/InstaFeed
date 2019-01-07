package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

public class EmptyStateView extends FrameLayout implements View.OnClickListener {

    ImageView emptyStateImage;
    TextView emptyStateText;
    TextView emptyStateTitle;
    TextView buttonAction;
    Callback callback;

    public interface Callback{
        void onEmptyStateActionClick();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_empty_state,this,true);

        emptyStateImage = findViewById(R.id.empty_state_image);
        emptyStateText = findViewById(R.id.empty_state_text);
        emptyStateTitle = findViewById(R.id.empty_state_title);
        buttonAction = findViewById(R.id.buttonAction);

        hideActionButton();
    }

    public void showActionButton(){
        buttonAction.setVisibility(VISIBLE);
        buttonAction.setOnClickListener(this);
    }

    public void hideActionButton(){
        buttonAction.setVisibility(GONE);
    }

    public ImageView getEmptyStateImage() {
        return emptyStateImage;
    }

    public void setEmptyStateImage(ImageView emptyStateImage) {
        this.emptyStateImage = emptyStateImage;
    }

    public TextView getEmptyStateText() {
        return emptyStateText;
    }

    public void setEmptyStateText(TextView emptyStateText) {
        this.emptyStateText = emptyStateText;
    }

    public TextView getEmptyStateTitle() {
        return emptyStateTitle;
    }

    public void setEmptyStateTitle(TextView emptyStateTitle) {
        this.emptyStateTitle = emptyStateTitle;
    }

    public TextView getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(TextView buttonAction) {
        this.buttonAction = buttonAction;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonAction){
            callback.onEmptyStateActionClick();
        }
    }
}
