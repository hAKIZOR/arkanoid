package com.caserteam.arkanoid;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import java.io.*;


public class SettingsActivity extends AppCompatActivity {
    private static final String DEBUG_STRING = "SettingsActivity = ";
    private static final int ITALIANO =  Settings.lang.valueOf(Settings.LANGUAGE_IT).ordinal();
    private static final int INGLESE =  Settings.lang.valueOf(Settings.LANGUAGE_ENG).ordinal();
    private static final int SPAGNOLO =  Settings.lang.valueOf(Settings.LANGUAGE_ESP).ordinal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Button button = (Button) findViewById(R.id.button_save);
        RadioGroup groupLanguage = (RadioGroup) findViewById(R.id.groupLanguage);
        Switch switchGameControl = (Switch) findViewById(R.id.switchControlGame);
        Switch switchAudioControl = (Switch) findViewById(R.id.switchControlAudio);

        try {
            loadSettingsFromFile(groupLanguage,switchGameControl,switchAudioControl);
        } catch (IOException | ClassNotFoundException e) {
            Log.d(DEBUG_STRING,"il file è danneggiato");
            e.printStackTrace();
        }

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(DEBUG_STRING,"SONO DENTRO");
                // Is the button now checked?
                int checkedLanguage = groupLanguage.getCheckedRadioButtonId();
                boolean checkedGameControl = switchGameControl.isChecked();
                boolean checkedAudioControl = switchAudioControl.isChecked();


                int  settedLanguage = setLanguage(checkedLanguage);
                int settedAudio = setAudioControl (checkedAudioControl);
                int settedGameControl = setGameControl (checkedGameControl);

                try {
                    saveSettingsInFile(settedGameControl,settedLanguage,settedAudio);
                } catch (IOException e) {
                    Log.e(DEBUG_STRING,"errore saveSettingsException");
                    e.printStackTrace();
                }

                Intent refresh = new Intent(SettingsActivity.this, MenuActivity.class);
                finish();
                startActivity(refresh);


            }
        });


    }

    private void loadSettingsFromFile(RadioGroup groupLanguage, Switch switchGameControl, Switch switchAudioControl) throws IOException, ClassNotFoundException {

        //Deserializzazione dell'oggetto Settings

            Settings settings =  IOUtils.readObjectFromFile(this,Settings.FILE_NAME);

            if(settings != null){
                Log.d(DEBUG_STRING,"dati caricati " + settings.toString());

                if(settings.getLanguage() == INGLESE){
                    groupLanguage.check(R.id.radioEng);
                } else if(settings.getLanguage() == ITALIANO){
                    groupLanguage.check(R.id.radioIta);
                } else if(settings.getLanguage() == SPAGNOLO){
                    groupLanguage.check(R.id.radioEsp);
                }


                if(settings.getAudio() == Settings.AUDIO_ON){
                    switchAudioControl.setChecked(true);
                } else {
                    switchAudioControl.setChecked(false);
                }

                if(settings.getControlMode() == Settings.SYSTEM_CONTROL_SENSOR)
                { switchGameControl.setChecked(true);}
                else {switchGameControl.setChecked(false);}
            }

    }



    public void saveSettingsInFile(int gameControl, int language,int audio) throws IOException {

        Settings settingstmp = new Settings(gameControl,language,audio);

        Log.d(DEBUG_STRING,"dati da salvare " + settingstmp.toString());
        Log.e(DEBUG_STRING,"salvataggio in corso...");
        IOUtils.writeObjectToFile(SettingsActivity.this,Settings.FILE_NAME,settingstmp);
        Log.e(DEBUG_STRING,"salvataggio completato");

    }

    public int setLanguage(int checkedLanguage) {
        // Check which radio button was clicked
        int  language = 0; //initialize tmp variable
        switch (checkedLanguage) {
            case R.id.radioEng:
                setAppLocale(Settings.LANGUAGE_ENG);
                language = Settings.lang.valueOf(Settings.LANGUAGE_ENG).ordinal();
                System.out.println("EN");
                break;
            case R.id.radioIta:
                setAppLocale("");
                language = Settings.lang.valueOf(Settings.LANGUAGE_IT).ordinal();

                System.out.println("IT");
                break;
            case R.id.radioEsp:
                setAppLocale(Settings.LANGUAGE_ESP);
                language = Settings.lang.valueOf(Settings.LANGUAGE_ESP).ordinal();
                System.out.println("ES");
                break;
        }
        return language;
    }
    public int setAudioControl(boolean audioChecked) {
        if(audioChecked) {
            //audio on
            return Settings.AUDIO_ON;
        } else {
            //audio off
            return Settings.AUDIO_OFF;
        }

    }
    public int setGameControl(boolean gameControl) {
        if(gameControl) {
            //game control sensor
            return Settings.SYSTEM_CONTROL_SENSOR;
        } else {
            // game control slider
            return Settings.SYSTEM_CONTROL_SCROLL;
        }
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
