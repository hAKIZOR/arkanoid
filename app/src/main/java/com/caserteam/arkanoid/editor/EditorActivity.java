package com.caserteam.arkanoid.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.editor_module.Editor;
import com.caserteam.arkanoid.editor.editor_module.EditorViewLandScape;
import com.caserteam.arkanoid.editor.editor_module.EditorViewPortrait;
import com.caserteam.arkanoid.editor.ui_plus_check.BottomSheetDialog;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailBricks;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailsObstacles;
import com.caserteam.arkanoid.editor.ui_save_check.DialogSaveLevel;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.editor.ui_upload_check.UploadLevelActivity;
import com.caserteam.arkanoid.editor.PromptUtils;
import com.caserteam.arkanoid.gameClasses.UpdateThread;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


/*
TO DO:
- implementazione visiva dell'Editor in modalità Landscape
- switch del landscape con il floating action button
- garantire lo svolgimento delle selezioni, aggiunte e rimozioni anche in modalità landscape
*/
public class EditorActivity extends AppCompatActivity  implements
        FragmentDetailBricks.FragmentDetailBricksListener,
        FragmentDetailsObstacles.FragmentDetailsObstaclesListener,
        FloatingActionButtonPlus.FloatingActionButtonPlusListener,
        FloatingActionButtonMinus.FloatingActionButtonMinusListener

{
    public static final String STATE_CURRENT_USER = "currentUser";
    public static final String STATE_NAME_LEVEL = "nameLevel";
    public static final String STATE_STRUCTURE = "structure";
    public static final String STATE_NICKNAME = "nickname";
    private String nackname;
    private boolean fullScreen = false;
    private Editor editor;
    private Handler updateHandler;
    private UpdateThread myThread;
    private GestureDetectorCompat gestureDetector;
    private String structure;
    private String nameLevel;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getNickameFromPreferences();

        initializeSessionOption();

        initializeEditor(savedInstanceState);

        setContentView(editor);

        //inizializza l'orientamento dello schermo
        initializeOrientation();

        //inizializza l'action bar assieme al floating action button
        initializeActionBarAndFloatingActionButton();

        // crea handler e thread
        createHandlerAndThread();

    }

    private void getNickameFromPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.getString(STATE_NICKNAME,null);

    }

    private void initializeSessionOption() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    }


    private void initializeEditor(Bundle savedInstanceState) {
        Log.d("EditorActivity","passo");
        if(savedInstanceState != null){
            nameLevel = savedInstanceState.getString(STATE_NAME_LEVEL);
            structure = savedInstanceState.getString(STATE_STRUCTURE);

        } else {
            structure = getIntent().getStringExtra(STATE_STRUCTURE);
            nameLevel = getIntent().getStringExtra(STATE_NAME_LEVEL);
        }

        if(structure != null){
            loadLevelUpdated(structure);
        } else {
            loadDefaultEditor();
        }
        gestureDetector = editor.getGestureDetector();

    }

    private void loadDefaultEditor() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            editor = new EditorViewLandScape(this, EditorActivity.this);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            editor = new EditorViewPortrait(this, EditorActivity.this);
        }
    }

    private void loadLevelUpdated(String structure) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            editor = new EditorViewLandScape(this, EditorActivity.this,structure);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("EditorActivity","passo");
            editor = new EditorViewPortrait(this, EditorActivity.this,structure);
        }
    }


    private void initializeActionBarAndFloatingActionButton(){
        editor.buildActionButtonPlus(EditorActivity.this);
        editor.buildActionButtonMinus(EditorActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");


    }
    private void initializeOrientation() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void createHandlerAndThread() {
        updateHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                editor.invalidate();
                super.handleMessage(msg);
            }
        };
        myThread = new UpdateThread(updateHandler);
        myThread.start();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_main_clear:
                // cancello tutti i mattoni inseriti
                editor.clearBricks();
                break;
            case R.id.menu_main_upload:
                /*
                   Al click di questo tasto deve essere possibile il caricamento dei propri livelli mediante un'altra activity
                */
                Intent intent = new Intent(EditorActivity.this, UploadLevelActivity.class);
                intent.putExtra(STATE_CURRENT_USER,account.getEmail());
                startActivity(intent);

                break;
            case R.id.menu_main_save:
                /*
                Al click di questo tasto deve essere possibile l'inserimento del nome del livello attraverso una finestra di
                dialogo con i bottoni "salva" e "annulla" oltre che la EditText del nome del livello
                */
                structure = editor.convertListBrickToString();
                if(structure != null){

                        Bundle bundle = new Bundle();
                        bundle.putString(STATE_STRUCTURE, structure);
                        bundle.putString(STATE_NAME_LEVEL, nameLevel);
                        //bundle.getString(STATE_NICKNAME, nickname);
                        bundle.putString(STATE_CURRENT_USER,account.getEmail());
                        DialogSaveLevel dialogSaveLevel = new DialogSaveLevel();
                        dialogSaveLevel.setArguments(bundle);
                        dialogSaveLevel.show(getSupportFragmentManager(),"DialogFragmentSave");


                } else {
                    editor.getPromptUtils().showMessage(PromptUtils.SAVE_FAILED_NO_BRICK_IN_THE_GRID);
                }

                break;

            case R.id.menu_main_rotation:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case android.R.id.home:

                Intent intent1 = new Intent(EditorActivity.this, MenuActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.menu_main_search:
                /*
                   Al click di questo tasto deve essere possibile il caricamento di tutti livelli creati da altri utenti mediante un'altra activity
                */
                Intent intent2 = new Intent(EditorActivity.this, LevelsSearchActivity.class);
                intent2.putExtra(STATE_CURRENT_USER,account.getEmail());
                startActivity(intent2);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(gestureDetector != null){
            gestureDetector.onTouchEvent(e);
        }

        if(e.getAction() == MotionEvent.ACTION_UP) {
            if(editor.ismIsScrolling()) {
                editor.setmIsScrolling (false);
                editor.replaceFromGridBrickToTempBrick();
            }
        }

        return super.onTouchEvent(e);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }


    @Override
    public void onBrickClicked(int color) {
        editor.createbrick(color);
    }


    @Override
    public void onObstacleClicked(int obstacleSkin) {
        editor.createbrick(obstacleSkin);
    }


    @Override
    public void onFloatingActionButtonPlusClicked() {
        // parte il fragment da sotto per l'aggiunta dei mattoni o modifica della palla o del paddle
        BottomSheetDialog bottomSheet = new BottomSheetDialog();
        bottomSheet.show(getSupportFragmentManager(),"exampleBottomSheet");


    }


    @Override
    public void onFloatingActionButtonMinusClicked() {
        editor.deleteBrickSelected();
    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        structure = editor.convertListBrickToString();
        if(structure!=null) {
            Log.d(STATE_STRUCTURE, structure);
            outState.putString(STATE_NAME_LEVEL, nameLevel);
            outState.putString(STATE_STRUCTURE, structure);
        }

        super.onSaveInstanceState(outState);
    }

}