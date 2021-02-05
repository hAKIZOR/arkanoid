package com.caserteam.arkanoid.editor.ui_upload_check;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.editor_module.Editor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class UploadLevelActivity extends AppCompatActivity
        implements AsyncTaskLoadResult.ListenerAsyncData {

    private ListView listViewLevels;
    private AdapterLevelCreated adapterLevelCreated;
    private ArrayList<LevelCreatedModel> levelCreateds;
    private String pathOfCollection;
    private LoadingDialog loadingDialog;
    private String nickname;
    private String currentUser;
    public static final String COLLECTION_USERS = "utenti";
    public static final String COLLECTION_LEVELS = "livelli";
    private static final String TAG = "UploadLevelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_level);
        listViewLevels = findViewById(R.id.listLevels);

        getSupportActionBar().setTitle(R.string.select_level_created);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentUser = getIntent().getStringExtra(EditorActivity.STATE_CURRENT_USER);
        pathOfCollection = COLLECTION_USERS + "/" + currentUser + "/" + COLLECTION_LEVELS;

        nickname = getIntent().getStringExtra(EditorActivity.STATE_CURRENT_USER_NICKNAME);

        loadingDialog = new LoadingDialog(UploadLevelActivity.this);
        loadingDialog.startDialog(getResources().getString(R.string.load_levels_created));

        // eseguo il task che permetterà la vista di tutti i livelli creati da un utente
        AsyncTaskLoadResult taskLoadingLevel = new AsyncTaskLoadResult(UploadLevelActivity.this,this,pathOfCollection);
        taskLoadingLevel.execute();


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDataOfLevelCreatedChange(ArrayList<LevelCreatedModel> levelCreateds) {
        /*metodo di interfaccia richiamato da AsyncTaskLoadResult e da AdapterViewCreated
        al fine di aggiornare la visualizzazione e il database a fronte di un eliminazione di item nella listView
        passando il modello di dati intrinseco alla variabile levelCreateds
         */
        this.levelCreateds = levelCreateds;

        //aggiorno l'adapter per poi settare la listView che contiene i livelli
        adapterLevelCreated = new AdapterLevelCreated(
                UploadLevelActivity.this,
                R.layout.row_layout_levels_created,levelCreateds,
                UploadLevelActivity.this,
                pathOfCollection,nickname);

        listViewLevels.setAdapter(adapterLevelCreated);
        loadingDialog.dismissDialog();
        listViewLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"item" + adapterLevelCreated.getItem(i).toString());
            }
        });

    }



}