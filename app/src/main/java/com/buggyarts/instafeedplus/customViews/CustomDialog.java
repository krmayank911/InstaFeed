package com.buggyarts.instafeedplus.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

public class CustomDialog extends FrameLayout implements View.OnClickListener {

    FrameLayout dialogWindow;
    RelativeLayout dialogContainer;

    TextView dialogTitle;
    TextView dialogMessage;
    TextView dialogActionNeg;
    TextView dialogActionPos;

    ImageView dialogImage;

    private Callback callback;
    private int FLAG;

    public interface Callback{
        void onWindowOutsideClick();
        void onDialogActionNeg(int flag);
        void onDialogActionPos(int flag);
    }

    public CustomDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        LayoutInflater.from(context).inflate(R.layout.rate_app_dialog,this,true);

        dialogWindow = findViewById(R.id.dialog_window);
        dialogContainer = findViewById(R.id.dialog_container);

        dialogTitle = findViewById(R.id.dialog_title);
        dialogImage = findViewById(R.id.dialog_image);
        dialogMessage = findViewById(R.id.dialog_message);
        dialogActionNeg = findViewById(R.id.dialog_action_neg);
        dialogActionPos = findViewById(R.id.dialog_action_pos);

        dialogWindow.setOnClickListener(this);
        dialogActionNeg.setOnClickListener(this);
        dialogActionPos.setOnClickListener(this);
    }

    public FrameLayout getDialogWindow() {
        return dialogWindow;
    }

    public RelativeLayout getDialogContainer() {
        return dialogContainer;
    }

    public TextView getDialogTitle() {
        return dialogTitle;
    }

    public TextView getDialogMessage() {
        return dialogMessage;
    }

    public TextView getDialogActionNeg() {
        return dialogActionNeg;
    }

    public TextView getDialogActionPos() {
        return dialogActionPos;
    }

    public ImageView getDialogImage() {
        return dialogImage;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setFLAG(int FLAG) {
        this.FLAG = FLAG;
    }

    public int getFLAG() {
        return FLAG;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dialog_window){
            callback.onWindowOutsideClick();
        }else if(view.getId() == R.id.dialog_action_neg){
            callback.onDialogActionNeg(FLAG);
        }else if(view.getId() == R.id.dialog_action_pos){
            callback.onDialogActionPos(FLAG);
        }
    }

}
