package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.gameClasses.DialogPauseGame;
import com.caserteam.arkanoid.gameClasses.DialogResultGame;
import com.caserteam.arkanoid.gameClasses.GameActivity;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;
import com.caserteam.arkanoid.multiplayer.Room;
import com.caserteam.arkanoid.multiplayer.gameClasses.Game;
import com.caserteam.arkanoid.multiplayer.gameClasses.GameViewPortrait;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import static com.caserteam.arkanoid.AppContractClass.*;
public class ActualGameActivity extends AppCompatActivity implements GameListener {


    private Game game;
    private HandlerThread thread;
    private Handler updateHandler;
    private GestureDetectorCompat gestureDetector;
    private DialogConfirmExitGame dialogConfirmExitGame;
    private Room room;
    private int counter;
    SharedPreferences preferences;
    private String nickname;
    DatabaseReference roomRef;
    private String playerRole;
    String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        nickname = data.get(KEY_NICKNAME_PREFERENCES);

        counter=0;

        code = getIntent().getStringExtra(CODE_ROOM_EXTRA);
        playerRole = getIntent().getStringExtra(CODE_PLAYER_EXTRA);
        roomRef =  FirebaseDatabase.getInstance(ROOT_DB_REALTIME_DATABASE).getReference(ROOMS_NODE+ "/" +code);

        //imposta l'orientamento dello schermo

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        game = new GameViewPortrait(this, 3, 0, playerRole, roomRef);
        game.setGameListener(this);

        float scale = getResources().getDisplayMetrics().density * 200;
        game.setCameraDistance(scale);

        gestureDetector = game.getGestureDetector();
        setContentView(game);


        // crea handler e thread
        thread = new HandlerThread ("MyHandlerThread");
        thread.start();
        Looper looper = thread.getLooper ();
        updateHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        //setto l'esecuzione dell'handler
        updateHandler.post (new Runnable () {
            @Override
            public void run() {
                while (endThreadCondition(game.isGameOver(),game.getExitGame())){
                    try {
                        thread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!game.isPaused()){
                        game.invalidate();
                        game.update();
                    } else {
                        try {
                            thread.sleep(50);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    game.invalidate();
                    game.update();

                }
            }

        });

        ActualGameActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.initializeButtonExitGame(ActualGameActivity.this);

            }
        });
        getSupportActionBar().hide();
    }

    /**
     condizione di fine gioco:
     se la partita è in game Over ma l'uscita dal gioco non è richiesta --> restituisce false
     se la partita non è in game Over ma l'uscita dal gioco è richiesta --> restituisce false
     negli altri due casi --> il gioco può continuare --> restituisce true
    */
    public boolean endThreadCondition(boolean gameOver,boolean exitGame){
        return !( ( gameOver || exitGame ) && ! ( gameOver && exitGame ) );
    }

    /**
     metodo di interfaccia di GameListener che consente di notificare il game over del gioco
     con un dialog
     */
    @Override
    public void onGameOver() {
        DialogResultGame dialogGameOver = new DialogResultGame("Game Over!" , ActualGameActivity.this,"score: " + game.getScore());
        dialogGameOver.show(getSupportFragmentManager(), "dialogNewScore");
    }

    /**
     metodo di interfaccia di GameListener che consente di notificare il completamento di tutti i livelli di gioco
     con un dialog
     */
    @Override
    public void onWinGame() {
        DialogResultGame dialogWinGame = new DialogResultGame(getResources().getString(R.string.win_game) , ActualGameActivity.this,"score: " + game.getScore());
        dialogWinGame.show(getSupportFragmentManager(), "dialogNewScore");
    }

    /**
     metodo di interfaccia di GameListener che consente di uscire dall'activity di gioco:
     - se il ruolo del giocatore è quello di colui che vuole interrompere: role_close è true
     - se il ruolo del giocatore è quello di colui che non vuole interrompere: role_close è false
     */
    @Override
    public void onExitGame(boolean role_close) {
        if(role_close){
            roomRef.child(game.fieldExitGame).setValue(true);
        } else {//notifica con un Dialog che la partita è stata interrotta
            DialogInterruptGame dialogInterruptGame = new DialogInterruptGame(ActualGameActivity.this);
            dialogInterruptGame.show(getSupportFragmentManager(),"dialogInterruptGame");
        }
    }

    /**
     metodo di interfaccia GameListener che consente di mettere il gioco in pausa
     */
    @Override
    public void onPauseGame(boolean pause,boolean role_pause) {
        if(pause){
            if(role_pause){
                dialogConfirmExitGame = new DialogConfirmExitGame(ActualGameActivity.this,this);
                dialogConfirmExitGame.show(getSupportFragmentManager(),"dialogEexitGame");
            }

        }

    }

    /**
     metodo di interfaccia GameListener che consente di riprendere il gioco dopo la pausa di gioco
     per poi fare il dismiss del dialog di pausa
     */
    @Override
    public void onResumeGame() {
        dialogConfirmExitGame.dismiss();
        game.setPause(false);
    }

    protected void onPause() {
        super.onPause();
        game.pauseGame();
        thread.quit(); // kill del Thread di gioco
    }

    protected void onResume() {
        super.onResume();
        game.resumeGame();

    }
    @Override
    protected void onStop() {
        thread.quit();
        super.onStop();

    }
    @Override
    protected void onDestroy() {
        thread.quit();
        super.onDestroy();

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