package com.example.arkanoid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    public static final String TAG = "MenuActivity = ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();

    }
    public void moveToGame(View view) {
        final Button launchButton = findViewById(R.id.button_gioca);

        launchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });


    }

}
