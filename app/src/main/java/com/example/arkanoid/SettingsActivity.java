package com.example.arkanoid;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button button = (Button) findViewById(R.id.button_save);
        RadioGroup group = (RadioGroup) findViewById(R.id.groupLanguage);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Is the button now checked?
                int checked = group.getCheckedRadioButtonId();

                // Check which radio button was clicked
                switch (checked) {
                    case R.id.radioEng:
                            setAppLocale("en");
                            System.out.println("EN");
                        break;
                    case R.id.radioIta:
                            setAppLocale("");
                        System.out.println("IT");
                        break;
                    case R.id.radioEsp:
                            setAppLocale("es");
                        System.out.println("ES");
                        break;

                }
                Intent refresh = new Intent(SettingsActivity.this, MenuActivity.class);
                finish();
                startActivity(refresh);


            }
        });


    }



    public void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            conf.setLocale(new Locale(localeCode.toLowerCase()));
        }else {
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }
}
