package com.example.arkanoid;

import android.content.Context;
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

import java.io.FileReader;
import java.util.Locale;

import java.io.*;


public class SettingsActivity extends AppCompatActivity {
    private static final String FILE_NAME = "config.txt";
    private static final String DEBUG_STRING = "SettingsActivity = ";

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
        } catch (IOException e) {
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


                String settedLanguage = setLanguage(checkedLanguage);
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

    private void loadSettingsFromFile(RadioGroup groupLanguage, Switch switchGameControl, Switch switchAudioControl) throws IOException {

        //Deserializzazione dell'oggetto Settings

            Settings settings = null;

            try {
                FileInputStream fileIn = this.openFileInput(FILE_NAME);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                settings = (Settings) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                System.out.println("Employee class not found");
                c.printStackTrace();
                return;
            }


            if(settings != null){
                Log.d(DEBUG_STRING,"dati caricati " + settings.toString());
                switch (settings.getLanguage()){
                    case "en":
                        groupLanguage.check(R.id.radioEng);
                        break;
                    case "it":
                        groupLanguage.check(R.id.radioIta);
                        break;
                    case "es":
                        groupLanguage.check(R.id.radioEsp);
                        break;
                }

                if(settings.getAudio() == 1){
                    switchAudioControl.setChecked(true);
                } else {
                    switchAudioControl.setChecked(false);
                }

                if(settings.getControlMode() == 1)
                { switchGameControl.setChecked(true);}
                else {switchGameControl.setChecked(false);}
            }

    }

    public boolean controlIsEmptyFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        if (br.readLine() == null) {
            Log.e(DEBUG_STRING,"vuoto");
            br.close();
           return true;
        } else {
            Log.e(DEBUG_STRING,"pieno");
            br.close();
            return false;
        }

    }


    public void saveSettingsInFile(int GameControl, String language,int audio) throws IOException {

        Settings settings = new Settings(GameControl,language,audio);
        Log.e(DEBUG_STRING,"salvataggio in corso...");
        try {
            FileOutputStream fOut = this.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fOut);
            outputStream.writeObject(settings);
            fOut.close();
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        Log.e(DEBUG_STRING,"salvataggio completato");

    }

    public String setLanguage(int checkedLanguage) {
        // Check which radio button was clicked
        String language = ""; //initialize tmp variable
        switch (checkedLanguage) {
            case R.id.radioEng:
                setAppLocale("en");
                language = "en";
                System.out.println("EN");
                break;
            case R.id.radioIta:
                setAppLocale("");
                language = "it";
                System.out.println("IT");
                break;
            case R.id.radioEsp:
                setAppLocale("es");
                language = "es";
                System.out.println("ES");
                break;
        }
        return language;
    }
    public int setAudioControl(boolean audioChecked) {
        if(audioChecked) {
            //audio on
            return 1;
        } else {
            //audio off
            return 0;
        }

    }
    public int setGameControl(boolean gameControl) {
        if(gameControl) {
            //game control sensor
            return 1;
        } else {
            // game control slider
            return 0;
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
