package com.caserteam.arkanoid.editor.ui_game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;
import com.caserteam.arkanoid.gameClasses.Game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class GameSearchedActivity extends AppCompatActivity implements GameSearched.GameSearchedListener {


    private static final String TAG = "GameSearchedActivity";
    private GameSearched game;
    private HandlerThread thread;
    private Handler updateHandler;
    private GameSearched.GameSearchedListener listener;
    private DialogPauseGame dialogPauseGame;
    private GestureDetectorCompat gestureDetector;
    private String structure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGame();

        initializeOrientation();

        startGame();

    }




    private void initializeGame() {
        structure = getIntent().getStringExtra(LevelsSearchActivity.STRUCTURE_GAME_EXTRA);
        if(structure != null){
            loadLevelFromStucture(structure);

        } else {
            game = null;
        }

    }

    private void loadLevelFromStucture(String structure) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            game = new GameViewPortrait(getApplicationContext(),3,0,structure);
        } else {
            game = new GameViewLandscape(getApplicationContext(),3,0);
        }
        listener = this;
        game.setGameSearchedListener(listener);

        gestureDetector = game.getGestureDetector();

    }
    private void startGame() {

        setContentView(game);
        listener = this;
        thread = new HandlerThread ("MyHandlerThread");
        thread.start ();
        Looper looper = thread.getLooper ();
        updateHandler = new Handler(looper){
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG,"PASSO");
                switch (msg.what){
                    case 2:
                        DialogResultGame dialogWinGame = new DialogResultGame(getResources().getString(R.string.win_game),GameSearchedActivity.this);
                        dialogWinGame.show(getSupportFragmentManager(),"dialogWinGame");
                        break;
                    case 3:
                        DialogResultGame dialogLoseGame = new DialogResultGame(getResources().getString(R.string.lose_game),GameSearchedActivity.this);
                        dialogLoseGame.show(getSupportFragmentManager(),"dialogLoseGame");
                        break;
                }
            }
        };
        updateHandler.post (new Runnable () {
            @Override
            public void run() {
                while (!game.isGameOver()){
                    try {
                        thread.sleep(30);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!game.isPaused()){
                        game.invalidate();
                        game.update();
                    } else {
                        try {
                            thread.sleep(30);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });

        GameSearchedActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.initializeButtonPause(GameSearchedActivity.this);

            }
        });

        getSupportActionBar().hide();
    }

    private void initializeOrientation() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    protected void onStop() {
        thread.quit();
        super.onStop();

    }

    protected void onPause() {
        thread.quit();
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


    @Override
    public void onGameOver() {

        updateHandler.sendEmptyMessage(3);

    }
    @Override
    public void onResumeGame() {
        dialogPauseGame.dismiss();
        game.setPause(false);
        game.getFabButtonPause().setButtonPauseDrawable(getResources().getDrawable(R.drawable.pause_on,null));

    }
    @Override
    public void onWinGame() {

        updateHandler.sendEmptyMessage(2);

    }

    @Override
    public void onPauseGame(boolean pause) {
        if(pause) {
            game.getFabButtonPause().setButtonPauseDrawable(getResources().getDrawable(R.drawable.pause_off,null));
            dialogPauseGame = new DialogPauseGame(GameSearchedActivity.this,listener);
            dialogPauseGame.show(getSupportFragmentManager(),"dialogPauseGame");
        }
    }



    @Override
    protected void onDestroy() {
        thread.quit();
        super.onDestroy();

    }
}
