package com.example.arkanoid;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //imposta l'orientamento dello schermo
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            game = new GameViewLandscape(this, 3, 0);
            setContentView(game);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            game = new GameViewPortrait(this, 3, 0);
            setContentView(game);
        }


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

}
