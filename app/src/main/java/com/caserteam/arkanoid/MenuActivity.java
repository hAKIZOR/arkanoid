package com.caserteam.arkanoid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.caserteam.arkanoid.gameClasses.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
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

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        Button buttonArcade = findViewById(R.id.button_arcade);
        Button buttonSettings = findViewById(R.id.button_settings);
        Button buttonLeaderBoard = findViewById(R.id.button_leaderboard);
        Button buttonEditor = findViewById(R.id.button_editor);
        FloatingActionButton buttonLogout = findViewById(R.id.floatingActionLogoutButton);
        TextView name = findViewById(R.id.name);
        ImageView photoProfile = findViewById(R.id.imageProfile);

        try {
            name.setText(account.getDisplayName());
            Picasso.get().load(account.getPhotoUrl()).into(photoProfile);
        } catch (Exception e){

        }

        try {
            prefs = getSharedPreferences("com.caserteam.arkanoid", MODE_PRIVATE);

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



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                MenuActivity.this.startActivity(myIntent);
                finish();
            }
        });

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
