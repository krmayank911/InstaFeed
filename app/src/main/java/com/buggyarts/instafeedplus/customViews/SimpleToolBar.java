package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;


public class SimpleToolBar extends FrameLayout implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private TextView titleLabel;
    private ImageView backButton;
    private ImageView overflowButton;
    private RelativeLayout backGroundView;

    private TopBarCallback topCallback;

    public interface TopBarCallback {
        void backButtonCalled();
    }

    public void setTopbarListener(TopBarCallback mCallback) {
        this.topCallback = mCallback;
    }

    public SimpleToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_topbar, this, true);
        titleLabel = findViewById(R.id.title);
        backGroundView = findViewById(R.id.backgroundView);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        overflowButton = findViewById(R.id.overflowButton);
        overflowButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.backButton){
            topCallback.backButtonCalled();
        }else if(view.getId() == R.id.overflowButton){
            showPopup(view);
        }
    }

    public RelativeLayout getBackGroundView() {
        return backGroundView;
    }

    public void setBackGroundView(RelativeLayout backGroundView) {
        this.backGroundView = backGroundView;
    }

    public ImageView getBackButton() {
        return backButton;
    }

    public TextView getTitleLabel() {
        return titleLabel;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_to_favourites:
//                return true;
//            case R.id.save_to_device:
//                return true;
//            default:
//                return false;
//        }
        return false;
    }

}
