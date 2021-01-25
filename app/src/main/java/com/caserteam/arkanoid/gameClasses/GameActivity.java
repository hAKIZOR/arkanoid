package com.caserteam.arkanoid.gameClasses;

import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

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

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity implements GameListener {


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
                        DialogResultGame dialogWinGame = new DialogResultGame(getResources().getString(R.string.win_game), GameActivity.this);
                        dialogWinGame.show(getSupportFragmentManager(),"dialogWinGame");
                        break;
                    case 3:
                        DialogResultGame dialogLoseGame = new DialogResultGame(getResources().getString(R.string.lose_game), GameActivity.this);
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
    public void onGameOver() {
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
    public void onWinLevel() {
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

    private void saveScore(){
        db = FirebaseFirestore.getInstance();
        SharedPreferences account = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        String accountName = account.getString(LoginActivity.KEY_NICKNAME_PREFERENCES,"");
        DocumentSnapshot ref = null;
        Map<String, String> leaderboard = new HashMap<>();
        leaderboard.put("nickname",accountName);
        leaderboard.put("score",String.valueOf(game.getScore()));

        db.collection("leaderboard")
                .whereEqualTo("nickname",accountName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String id = null;
                            int oldScore = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                                oldScore = Integer.parseInt(document.getString("score"));
                            }
                            if(id != null && oldScore<game.getScore()) {
                                db.collection("leaderboard").document(id).delete();
                                db.collection("leaderboard")
                                        .add(leaderboard)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                DialogResultGame dialogWinGame = new DialogResultGame("Complimenti hai effettuato un nuovo Record", GameActivity.this);
                                                dialogWinGame.show(getSupportFragmentManager(),"dialogNewScore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"Errore di Sistema nel caricamento dello score",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else if(id == null) {
                                db.collection("leaderboard")
                                        .add(leaderboard)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                DialogResultGame dialogWinGame = new DialogResultGame("Complimenti hai effettuato un nuovo Record", GameActivity.this);
                                                dialogWinGame.show(getSupportFragmentManager(),"dialogNewScore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"Errore di Sistema nel caricamento dello score",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    }
                });





    }
}
