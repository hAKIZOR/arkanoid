package com.caserteam.arkanoid.multiplayer.gameClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import  com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.multiplayer.Room;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

public class ActualGameActivity extends AppCompatActivity {


    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;
    private GestureDetectorCompat gestureDetector;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //imposta l'orientamento dello schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        game = new GameViewPortrait(this, 3, 0);
        gestureDetector = game.getGestureDetector();
        setContentView(game);


        // creare un nuova partita



        // crea handler e thread
        createHandler();
        myThread = new UpdateThread(updateHandler);
        myThread.start();
        getSupportActionBar().hide();
    }

    private void createHandler() {
        updateHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                game.invalidate();
                game.update();

                //QUI INSERISCI VALORI NEL DB
                float[] multiPlayerData = game.getMultiplayerData(); // contiene a [0]xPaddle, [1]xSpeedBall, [2]ySpeedBall

                //QUI RECUPERI VALORI DAL DB
                game.setMultiplayerData();

                super.handleMessage(msg);
            }
        };
    }


    protected void onPause() {
        super.onPause();
        game.pauseGame();
    }

    protected void onResume() {
        super.onResume();
        game.resumeGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(gestureDetector != null){
            gestureDetector.onTouchEvent(e);
        }
        return super.onTouchEvent(e);

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