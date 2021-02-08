package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.annotation.SuppressLint;
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

import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_game.DialogResultGame;
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
                while (!game.isGameOver()){
                    try {
                        thread.sleep(30);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    game.invalidate();
                    game.update();

                }
            }

        });
        getSupportActionBar().hide();
    }




    protected void onPause() {
        super.onPause();
        game.pauseGame();
        thread.quit();
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


    @Override
    public void onGameOver() throws IOException {

    }

    @Override
    public void onWinLevel() throws IOException {

    }

    @Override
    public void onWinGame() {

    }

    @Override
    public void onPauseGame(boolean pause) {

    }

    @Override
    public void onResumeGame() {

    }
}