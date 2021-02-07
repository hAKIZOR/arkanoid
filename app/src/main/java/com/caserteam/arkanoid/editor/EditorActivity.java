package com.caserteam.arkanoid.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.LoginActivity;
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
import android.app.TaskStackBuilder;
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
import static  com.caserteam.arkanoid.AppContractClass.*;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;


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

    private String nickname;
    private boolean fullScreen = false;
    private Editor editor;
    private Handler updateHandler;
    private UpdateThread myThread;
    private GestureDetectorCompat gestureDetector;
    private String structure;
    private String nameLevel;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


    private void initializeSessionOption() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        preferences = getSharedPreferences(KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        nickname = preferences.getString(KEY_NICKNAME_PREFERENCES,"");
    }


    private void initializeEditor(Bundle savedInstanceState) {
        Log.d("EditorActivity","passo");
        if(savedInstanceState != null){
            nameLevel = savedInstanceState.getString(NAME_LEVEL_EXTRA);
            structure = savedInstanceState.getString(STRUCTURE_GAME_EXTRA);

        } else {
            structure = getIntent().getStringExtra(STRUCTURE_GAME_EXTRA);
            nameLevel = getIntent().getStringExtra(NAME_LEVEL_EXTRA);
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
                intent.putExtra(CURRENT_USER_EMAIL_EXTRA,account.getEmail());
                intent.putExtra(CURRENT_USER_NICKNAME_EXTRA,nickname);
                startActivity(intent);

                break;
            case R.id.menu_main_save:
                /*
                Al click di questo tasto deve essere possibile l'inserimento del nome del livello attraverso una finestra di
                dialogo con i bottoni "salva" e "annulla" oltre che la EditText del nome del livello
                */

                Log.d("EditorActivity----->", nickname);
                structure = editor.convertListBrickToString();
                if(structure != null){
                        DialogSaveLevel dialogSaveLevel = new DialogSaveLevel(structure,nameLevel,nickname,account.getEmail());
                        dialogSaveLevel.show(getSupportFragmentManager(),"DialogFragmentSave");
                } else {
                    editor.getPromptUtils().showMessage(getResources().getString(R.string.save_level_no_brick));
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

                Intent upIntent = NavUtils.getParentActivityIntent (this);
                if (NavUtils.shouldUpRecreateTask (this, upIntent)){
                    Log.d("EditorActivity", "passo true");
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent).
                            startActivities();
                } else{ Log.d("EditorActivity", "passo false");NavUtils.navigateUpTo(this, upIntent); }
                    return true;

            case R.id.menu_main_search:
                /*
                   Al click di questo tasto deve essere possibile il caricamento di tutti livelli creati da altri utenti mediante un'altra activity
                */
                Intent intent2 = new Intent(EditorActivity.this, LevelsSearchActivity.class);
                intent2.putExtra(CURRENT_USER_NICKNAME_EXTRA,nickname);
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
            Log.d(STRUCTURE_GAME_EXTRA, structure);
            outState.putString(NAME_LEVEL_EXTRA, nameLevel);
            outState.putString(STRUCTURE_GAME_EXTRA, structure);
        }

        super.onSaveInstanceState(outState);
    }

}