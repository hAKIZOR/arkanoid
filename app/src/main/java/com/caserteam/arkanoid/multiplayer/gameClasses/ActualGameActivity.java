package com.caserteam.arkanoid.multiplayer.gameClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.LoginActivity;
import  com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;
import com.caserteam.arkanoid.multiplayer.Room;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
    SharedPreferences preferences;
    private String nickname;
    DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);

        code = getIntent().getStringExtra(MultiplayerActivity.STATE_CODE);
        roomRef =  FirebaseDatabase.getInstance(MultiplayerActivity.ROOT).getReference("rooms/"+code);

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
                super.handleMessage(msg);
            }
        };
    }

   private void updateMultiplayerData(){
       updateMultiplayerData();
       //QUI INSERISCI VALORI NEL DB
       float[] multiPlayerDataToSend = game.getMultiplayerData(); // contiene a [0]xPaddle, [1]xSpeedBall, [2]ySpeedBall
       roomRef.child("xPaddlePlayer1").setValue(multiPlayerDataToSend[0]);
       roomRef.child("xBall").setValue(multiPlayerDataToSend[1]);
       roomRef.child("yBall").setValue(multiPlayerDataToSend[2]);
       //QUI RECUPERI VALORI DAL DB

       roomRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               float[] multiPlayerDataToReceive = {
                       Float.parseFloat(snapshot.child("xPaddlePlayer2").getValue().toString()),
                       Float.parseFloat(snapshot.child("xBall").getValue().toString()),
                       Float.parseFloat(snapshot.child("yBall").getValue().toString())
               };
               game.setMultiplayerData(multiPlayerDataToReceive[0],multiPlayerDataToReceive[1],multiPlayerDataToReceive[2]);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

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