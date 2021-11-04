package com.taxialaan.drivers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.taxialaan.drivers.Helper.CustomDialog;
import com.taxialaan.drivers.Helper.LocaleUtils;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.R;

/**
 * Created by Esack N on 9/27/2017.
 */

public class ActivitySettings extends AppCompatActivity {

    private RadioButton radioEnglish, radioArabic, radioKurdish;

    private LinearLayout lnrEnglish, lnrArabic, lnrKurdish;

    private CustomDialog customDialogNew;

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {

        radioEnglish = findViewById(R.id.radioEnglish);
        radioArabic = findViewById(R.id.radioArabic);
        radioKurdish = findViewById(R.id.radioKurdish);

        lnrEnglish = findViewById(R.id.lnrEnglish);
        lnrArabic = findViewById(R.id.lnrArabic);
        lnrKurdish = findViewById(R.id.lnrKurdish);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("en")){
            radioEnglish.setChecked(true);
        }else if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("ar")){
            radioArabic.setChecked(true);
        }else if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("ku")){
            radioKurdish.setChecked(true);
        }else{
            radioEnglish.setChecked(true);
        }

        lnrEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioArabic.setChecked(false);
                radioKurdish.setChecked(false);
                radioEnglish.setChecked(true);
            }
        });

        lnrKurdish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioArabic.setChecked(false);
                radioKurdish.setChecked(true);
            }
        });

        lnrArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioKurdish.setChecked(false);
                radioArabic.setChecked(true);
            }
        });

        radioKurdish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioEnglish.setChecked(false);
                    radioArabic.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "language", "ku");
                    setLanguage();
                    GoToMainActivity();
                }
            }
        });

        radioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioEnglish.setChecked(false);
                    radioKurdish.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "language", "ar");
                    setLanguage();
                    GoToMainActivity();
                }
            }
        });

        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioArabic.setChecked(false);
                    radioKurdish.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "language", "en");
                    setLanguage();
                    GoToMainActivity();
                }
            }
        });


    }

    public void GoToMainActivity(){
        customDialogNew = new CustomDialog(ActivitySettings.this/*, getResources().getString(R.string.language_update)*/);
        if (customDialogNew != null)
            customDialogNew.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialogNew.dismiss();
                Intent mainIntent = new Intent(ActivitySettings.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySettings.this, "language");
        LocaleUtils.setLocale(this, languageCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
