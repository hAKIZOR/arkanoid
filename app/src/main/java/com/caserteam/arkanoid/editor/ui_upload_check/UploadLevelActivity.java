package com.caserteam.arkanoid.editor.ui_upload_check;

import androidx.appcompat.app.AppCompatActivity;

import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class UploadLevelActivity extends AppCompatActivity implements AsyncTaskLoadResult.ListenerAsyncData{
    private static final String PATH_COLLECTION = "utenti/Davide/livelli";
    private ListView listViewLevels;
    private AdapterLevelCreated adapterLevelCreated;
    private ArrayList<LevelCreated> levelCreateds;
    private String pathOfCollection;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_level);
        listViewLevels = findViewById(R.id.listLevels);

        String currentUser = getIntent().getStringExtra(EditorActivity.STATE_CURRENT_USER);
        pathOfCollection = "utenti/" + currentUser + "/livelli";

        loadingDialog = new LoadingDialog(UploadLevelActivity.this);
        loadingDialog.startDialog(LoadingDialog.MESSAGE_UPLOAD_LEVEL);


        AsyncTaskLoadResult taskLoadingLevel =
                new AsyncTaskLoadResult(UploadLevelActivity.this,this,pathOfCollection);
        taskLoadingLevel.execute();


    }

    @Override
    protected void onStart() {
        //Log.d("AsyncTask",levelCreateds.toString());
        super.onStart();
    }

    @Override
    public void onDataOfLevelCreatedChange(ArrayList<LevelCreated> levelCreateds) {
        this.levelCreateds = levelCreateds;
        //aggiorno l'adapter per poi settare la listView che contiene i livelli
        adapterLevelCreated =
                new AdapterLevelCreated(UploadLevelActivity.this,R.layout.row_layout,levelCreateds,UploadLevelActivity.this,pathOfCollection);
        listViewLevels.setAdapter(adapterLevelCreated);
        loadingDialog.dismissDialog();
        listViewLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("UploadLevelActivity","item" + adapterLevelCreated.getItem(i).toString());
            }
        });

    }



}