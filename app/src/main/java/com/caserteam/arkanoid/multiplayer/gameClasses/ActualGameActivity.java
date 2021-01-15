package com.caserteam.arkanoid.multiplayer.gameClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;
import com.caserteam.arkanoid.multiplayer.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ActualGameActivity extends AppCompatActivity {


    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;
    private String code;
    private GestureDetectorCompat gestureDetector;
    private Room room;
    private int counter;
    SharedPreferences preferences;
    private String nickname;
    DatabaseReference roomRef;
    private String playerRole;
    String p1,p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);
        counter=0;
        code = getIntent().getStringExtra(MultiplayerActivity.STATE_CODE);
        playerRole = getIntent().getStringExtra(MultiplayerActivity.CODE_PLAYER);
        roomRef =  FirebaseDatabase.getInstance(MultiplayerActivity.ROOT).getReference("rooms/"+code);

        //imposta l'orientamento dello schermo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(playerRole.equals("player1")){
            p1 = "xPaddlePlayer1";
            p2 = "xPaddlePlayer2";
        } else {
            p1 = "xPaddlePlayer2";
            p2 = "xPaddlePlayer1";
        }

        game = new GameViewPortrait(this, 3, 0,roomRef,p1,p2);

        if(playerRole.equals("player1")) {
            game.getBall().setSpeed(7,-14);
        } else {
            game.getBall().setSpeed(-7,14);
        }

        gestureDetector = game.getGestureDetector();
        setContentView(game);






        // crea handler e thread
        createHandler();
        myThread = new UpdateThread(updateHandler);
        myThread.start();
        getSupportActionBar().hide();
    }

    @SuppressLint("HandlerLeak")
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