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
import android.widget.Toast;

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.Constants;

public class SelectCountryFragment extends Fragment {

    ImageView emptyStateImage;
    TextView emptyStateText;
    TextView emptyStateTitle;
    TextView buttonAction;
    Callback callback;
    Spinner country_spinner;

    Context mContext;
    String selected_country = null, selected_language = null;


    public interface Callback{
        void onCountrySelected(String country);
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

        country_spinner = view.findViewById(R.id.country_selector);
        setup();

        return view;
    }

    void setup(){
        emptyStateImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.country));
        emptyStateTitle.setText(mContext.getResources().getString(R.string.select_country_title));
        emptyStateText.setText(mContext.getResources().getString(R.string.select_country_msg));
        countrySelectorSetup();
    }

    public void countrySelectorSetup() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(mContext, R.layout.spinner_item, Constants.countries);
        country_spinner.setAdapter(arrayAdapter);
        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int positionOffset = position - 1;

                if(positionOffset >= 0){
                    selected_country = Constants.country_options_abr[positionOffset];
                    if(callback != null) {
                        callback.onCountrySelected(selected_country);
                    }else {
                        Toast.makeText(mContext,getResources().getString(R.string.error_selection),Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_country = null;
            }

        });
    }

    public void languageSelectorSetup() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, Constants.languages);
        country_spinner.setAdapter(arrayAdapter);
        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_language = Constants.languages_options_abr[position];
                callback.onLanguageSelected(selected_language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_language = "null";
            }
        });
    }

}
