package com.caserteam.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.caserteam.arkanoid.audio.AudioUtils;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    //
    private Animation topAnim,bottomAnim;
    private ImageView logo;
    private TextView name_app,developed,credits;
    private static int SPLASH_SCREEN_TIME=3000;
    MediaPlayer mpintro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        //Animations
        topAnim= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.top_animation);

        bottomAnim= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.bottom_animation);

        //Hooks
        logo = findViewById(R.id.logo);
        name_app = findViewById(R.id.name_app);
        developed=findViewById(R.id.developed);
        credits = findViewById(R.id.credits);

        logo.setAnimation(topAnim);
        name_app.setAnimation(bottomAnim);
        developed.setAnimation(bottomAnim);
        credits.setAnimation(bottomAnim);

        try {
            AudioUtils.playBackgroundSound(this,R.raw.welcome_audio);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent myIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            }
        },SPLASH_SCREEN_TIME);



    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}