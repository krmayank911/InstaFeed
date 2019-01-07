package com.buggyarts.instafeedplus.fragments.introFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.Constants;

public class SelectLanguageFragment extends Fragment {

    ImageView emptyStateImage;
    TextView emptyStateText;
    TextView emptyStateTitle;
    TextView buttonAction;
    Callback callback;
    Spinner language_spinner;

    Context mContext;
    String selected_language = null;


    public interface Callback{
        void onLanguageSelected(String language);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro,container,false);

        mContext = container.getContext();
        emptyStateImage = view.findViewById(R.id.empty_state_image);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        emptyStateTitle = view.findViewById(R.id.empty_state_title);
        buttonAction = view.findViewById(R.id.buttonAction);
        buttonAction.setVisibility(View.GONE);

        language_spinner = view.findViewById(R.id.country_selector);

        setup();

        return view;
    }

    void setup(){
        emptyStateImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.language));
        emptyStateTitle.setText(mContext.getResources().getString(R.string.select_language_title));
        emptyStateText.setText(mContext.getResources().getString(R.string.select_language_msg));
        languageSelectorSetup();
    }


    public void languageSelectorSetup() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, Constants.languages);
        language_spinner.setAdapter(arrayAdapter);
        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int positionOffset = position - 1;
                if(positionOffset>=0) {
                    selected_language = Constants.languages_options_abr[positionOffset];
                    callback.onLanguageSelected(selected_language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_language = null;
            }
        });
    }

}
