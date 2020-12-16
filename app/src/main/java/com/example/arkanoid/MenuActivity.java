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
    Settings config;
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

            if (prefs.getBoolean("firstrun", true)){
                Log.d(TAG, "primo avvio");

                Settings settings = new Settings(1, systemLanguage,1);
                FileManager fileManager = new FileManager(settings);
                fileManager.writeToConfigFile(this);

            } else {
                FileManager fileManager = new FileManager();
                Settings settings = fileManager.readFromConfigFile(this);
                settings.setLanguage(systemLanguage);
                fileManager.setSettings(settings);
                fileManager.writeToConfigFile(this);

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


    protected void onPause() { super.onPause(); }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }


}
