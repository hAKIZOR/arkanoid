package com.example.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    public static final String TAG = "MenuActivity = ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button = (Button) findViewById(R.id.button_arcade);
        Button button2 = (Button) findViewById(R.id.button_settings);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });

    }

    protected void onPause() { super.onPause(); }

    protected void onResume() { super.onResume(); }

}
