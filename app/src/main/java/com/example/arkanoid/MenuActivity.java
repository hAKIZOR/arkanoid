package com.example.arkanoid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.arkanoid.gameClasses.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity = ";
    private static final String FIRST_RUN_INSTALLATION_STATE ="firstruninstallation";
    private static final String FIRST_RUN_STATE ="firstrun";
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        Button buttonArcade = (Button) findViewById(R.id.button_arcade);
        Button buttonSettings = (Button) findViewById(R.id.button_settings);
        Button buttonLeaderBoard = (Button) findViewById(R.id.button_leaderboard);
        Button buttonEditor = (Button) findViewById(R.id.button_editor);


        try {
            prefs = getSharedPreferences("com.example.arkanoid", MODE_PRIVATE);

            String systemLanguage = Locale.getDefault().getLanguage();
            int languageToSet = Settings.lang.valueOf(systemLanguage).ordinal();

            if (prefs.getBoolean(this.FIRST_RUN_INSTALLATION_STATE, true)){
                Log.d(TAG, "primo avvio");

                Settings settings = new Settings(1,languageToSet ,1);
                IOUtils.writeObjectToFile(this,Settings.FILE_NAME,settings);

            } else if(prefs.getBoolean(this.FIRST_RUN_STATE,true)){

                Settings settings = IOUtils.readObjectFromFile(this,Settings.FILE_NAME);
                settings.setLanguage(languageToSet);
                IOUtils.writeObjectToFile(this,Settings.FILE_NAME,settings);

            }


        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }



        buttonArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });
         buttonLeaderBoard.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent myIntent = new Intent(MenuActivity.this, LeaderBoardActivity.class);
                 MenuActivity.this.startActivity(myIntent);
             }

         });
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });


    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");
    }
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
    }
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.edit().putBoolean(this.FIRST_RUN_STATE, true).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        if (prefs.getBoolean(this.FIRST_RUN_INSTALLATION_STATE, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(this.FIRST_RUN_INSTALLATION_STATE, false).commit();
        }
        if (prefs.getBoolean(this.FIRST_RUN_STATE, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(this.FIRST_RUN_STATE, false).commit();
        }
    }


}
