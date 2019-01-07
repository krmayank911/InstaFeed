package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;


public class SearchBar extends FrameLayout implements View.OnClickListener {

    FrameLayout searchBox;
    ImageView backButton;
    ImageView clearButton;
    ImageView searchIcon;
    EditText searchText;
//    View searchBackground;
    Context mContext;

    public interface Callbacks{
        void onSearchClick(String searchString);
        void onBackButtonClick();
    }

    private Callbacks callbacks;

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.search_bar_layout,this,true);

        mContext = context;
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(this);
        clearButton = findViewById(R.id.clear_icon);
        clearButton.setOnClickListener(this);
        searchText = findViewById(R.id.searchText);
        searchText.setVisibility(GONE);
        searchBox = findViewById(R.id.search_bar);
//        searchBackground = findViewById(R.id.search_background);

        searchText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    callbacks.onSearchClick(searchText.getText().toString());
                    showClearButton();
                }
                return false;
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showSearchButton();
            }
        });

    }

    public void showTextView(){
        int duration = 500;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ChangeBounds myTransition = new ChangeBounds();
            myTransition.setDuration(duration);

            TransitionManager.beginDelayedTransition((ViewGroup) this.searchBox.getParent(), myTransition);
            searchText.setVisibility(VISIBLE);
//            searchBackground.setVisibility(VISIBLE);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(mContext.getResources().getDimensionPixelOffset(R.dimen.margin_left),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.margin_top),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.margin_right),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.margin_bottom));
            searchBox.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search_icon){
            callbacks.onSearchClick(searchText.getText().toString());
            showClearButton();
        }else if(v.getId() == R.id.backButton){
            callbacks.onBackButtonClick();
        }else if(v.getId() == R.id.clear_icon){
            searchText.setText("");
        }
    }

    public ImageView getSearchIcon() {
        return searchIcon;
    }

    public void setSearchIcon(ImageView searchIcon) {
        this.searchIcon = searchIcon;
    }

    public EditText getSearchText() {
        return searchText;
    }

    public void setSearchText(EditText searchText) {
        this.searchText = searchText;
    }

//    public View getSearchBackground() {
//        return searchBackground;
//    }

//    public void setSearchBackground(View searchBackground) {
//        this.searchBackground = searchBackground;
//    }

    void showSearchButton(){
        clearButton.setVisibility(GONE);
        searchIcon.setVisibility(VISIBLE);
    }

    void showClearButton(){
        searchIcon.setVisibility(GONE);
        clearButton.setVisibility(VISIBLE);
    }
}
