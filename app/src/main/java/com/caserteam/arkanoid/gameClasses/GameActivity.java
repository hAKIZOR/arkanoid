package com.caserteam.arkanoid.gameClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_game.DialogPauseGame;
import com.caserteam.arkanoid.editor.ui_game.DialogResultGame;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity implements GameListener {

    private static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_SCORE = "score";
    public static final String COLLECTION_LEADERBOARD ="leaderboard";
    private static final String TAG = "GameSearchedActivity";
    private Game game;
    private HandlerThread thread;
    private Handler updateHandler;
    private GameListener listener;
    private DialogPauseGame dialogPauseGame;
    private GestureDetectorCompat gestureDetector;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGame();

        initializeOrientation();

        startGame();

    }

    private void initializeGame() {
        //structure = getIntent().getStringExtra(LevelsSearchActivity.STRUCTURE_GAME_EXTRA);
        loadLevels();

    }

    private void loadLevels() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            game = new GameViewPortrait(getApplicationContext(),3,0);
        } else {
            game = new GameViewLandscape(getApplicationContext(),3,0);
        }
        listener = this;
        game.setGameSearchedListener(listener);

        gestureDetector = game.getGestureDetector();

    }
    private void startGame() {

        setContentView(game);
        getSupportActionBar().hide();
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
                        DialogResultGame dialogHighScore = new DialogResultGame(getResources().getString(R.string.success_record), GameActivity.this,"score: " + game.getScore());
                        dialogHighScore.show(getSupportFragmentManager(), "dialogNewScore");
                        break;
                    case 3:
                        DialogResultGame dialogLoseGame = new DialogResultGame(getResources().getString(R.string.lose_game), GameActivity.this,"score: " + game.getScore());
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
        GameActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.initializeButtonPause(GameActivity.this);

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
    public void onGameOver() throws IOException {
        saveScore();
        //updateHandler.sendEmptyMessage(3);

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
    public void onWinLevel() throws IOException {
        saveScore();
        //updateHandler.sendEmptyMessage(2);

    }

    @Override
    public void onPauseGame(boolean pause) {
        if(pause) {
            game.getFabButtonPause().setButtonPauseDrawable(getResources().getDrawable(R.drawable.pause_off,null));
            dialogPauseGame = new DialogPauseGame(GameActivity.this,listener);
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

    private void saveScore() throws IOException {
        db = FirebaseFirestore.getInstance();
        SharedPreferences account = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        String accountName = account.getString(LoginActivity.KEY_NICKNAME_PREFERENCES,"");
        DocumentSnapshot ref = null;
        int finalScore = game.getScore();

        if(!accountName.equals(LoginActivity.NICKNAME_GUEST_PLAYER)) {
            updateRecordInRemoteDB(finalScore,accountName);
        }else {
            updateRecordInLocalDB(finalScore);
        }

    }

    private void updateRecordInLocalDB(int finalScore) {
        DialogSaveGuestScore dialogSaveGuestScore = new DialogSaveGuestScore(finalScore,this, GameActivity.this);
        dialogSaveGuestScore.show(getSupportFragmentManager(),"dialogNewGuestScore");
    }

    private void updateRecordInRemoteDB(int finalScore,String accountName) {
        Map<String, Object> leaderboard = new HashMap<>();
        leaderboard.put(FIELD_NICKNAME,accountName);
        leaderboard.put(FIELD_SCORE,finalScore);

        db.collection(COLLECTION_LEADERBOARD)
                .whereEqualTo(FIELD_NICKNAME, accountName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = null;
                            int record = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                                record = document.getDouble("score").intValue();
                            }
                            if (id != null && record <= finalScore) {
                                //sovrascrivi il record
                                db.collection(COLLECTION_LEADERBOARD).document(id).delete();
                                db.collection(COLLECTION_LEADERBOARD)
                                        .add(leaderboard)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                updateHandler.sendEmptyMessage(2);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Errore nel salvataggio dello score", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            } else if (id == null) {
                                // crea un nuovo record
                                db.collection(COLLECTION_LEADERBOARD)
                                        .add(leaderboard)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                updateHandler.sendEmptyMessage(2);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Errore nel salvataggio dello score", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            } else if ( record > finalScore){
                                //non aggiorna nulla nel leaderboard perchè il record non è stato battuto
                                DialogResultGame dialogLowScore = new DialogResultGame(getResources().getString(R.string.failure_record) , GameActivity.this,"record: " + record);
                                dialogLowScore.show(getSupportFragmentManager(), "dialogNewScore");
                            }

                        }
                    }
                });
    }
}
