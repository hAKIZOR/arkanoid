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
import com.caserteam.arkanoid.gameClasses.GameListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import static com.caserteam.arkanoid.AppContractClass.*;

public class GameSearchedActivity extends AppCompatActivity implements GameListener {


    private static final String TAG = "GameSearchedActivity";
    private GameSearched game;
    private HandlerThread thread;
    private Handler updateHandler;
    private GameListener listener;
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
        structure = getIntent().getStringExtra(STRUCTURE_GAME_EXTRA);
        loadLevelFromStucture(structure);

    }

    private void loadLevelFromStucture(String structure) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            game = new GameViewPortrait(getApplicationContext(),3,0,structure);
        } else {
            game = new GameViewLandscape(getApplicationContext(),3,0,structure);
        }
        listener = this;
        game.setGameSearchedListener(listener);

        gestureDetector = game.getGestureDetector();

    }
    private void startGame() {

        setContentView(game);
        thread = new HandlerThread ("MyHandlerThread");
        thread.start();
        Looper looper = thread.getLooper ();

        //definisco i messaggi da poter gestire alla fine dell'esecuzione del thread
        updateHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG,"PASSO");
                switch (msg.what){
                    case 2:
                        DialogResultGame dialogWinGame = new DialogResultGame(getResources().getString(R.string.win_game),GameSearchedActivity.this,String.valueOf(game.getScore()));
                        dialogWinGame.show(getSupportFragmentManager(),"dialogWinGame");
                        break;
                    case 3:
                        DialogResultGame dialogLoseGame = new DialogResultGame(getResources().getString(R.string.lose_game),GameSearchedActivity.this,String.valueOf(game.getScore()));
                        dialogLoseGame.show(getSupportFragmentManager(),"dialogLoseGame");
                        break;
                }
            }
        };

        //setto l'esecuzione dell'handler
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

        // permetto la visualizzazione del bottone di pausa
        GameSearchedActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.initializeButtonPause(GameSearchedActivity.this);

            }
        });

    }

    private void initializeOrientation() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



    @Override
    public void onGameOver() {

        updateHandler.sendEmptyMessage(3);

    }

    @Override
    public void onWinLevel() {

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
    protected void onStop() {
        thread.quit();
        super.onStop();

    }

    @Override
    protected void onPause() {
        thread.quit();
        super.onPause();
        game.pauseGame();
    }

    @Override
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

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Nasconde la barra di navigazione e quella di stato
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
    protected void onDestroy() {
        thread.quit();
        super.onDestroy();

    }
}
