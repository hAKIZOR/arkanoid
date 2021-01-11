package com.caserteam.arkanoid.editor.ui_upload_check;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.caserteam.arkanoid.editor.FirebaseUtility;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.UpdateThread;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class UploadLevelActivity extends AppCompatActivity implements AsyncTaskGetResult.ListenerAsyncData{
    private static final String PATH_COLLECTION = "utenti/Davide/livelli";
    ListView listViewLevels;
    AdapterLevelCreated adapterLevelCreated;
    ArrayList<LevelCreated> levelCreateds;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_level);
        listViewLevels = findViewById(R.id.listLevels);

        loadingDialog = new LoadingDialog(UploadLevelActivity.this);
        loadingDialog.startDialog(LoadingDialog.MESSAGE_UPLOAD_LEVEL);


        AsyncTaskGetResult asyncTaskGetResult = new AsyncTaskGetResult(UploadLevelActivity.this,this);
        asyncTaskGetResult.execute();



        /*
        db = FirebaseFirestore.getInstance();
        db.collection("utenti/Davide/livelli")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d("UploadLevelActivity","upload  riuscito");
                            levelCreateds = new ArrayList<>();
                            for(DocumentSnapshot document: task.getResult()){
                                levelCreateds.add(document.toObject(LevelCreated.class));
                            }
                            adapterLevelCreated = new AdapterLevelCreated(UploadLevelActivity.this,R.layout.row_layout,levelCreateds,UploadLevelActivity.this);
                            listViewLevels.setAdapter(adapterLevelCreated);
                            loadingDialog.dismissDialog();
                            listViewLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("UploadLevelActivity","item" + adapterLevelCreated.getItem(i).toString());
                                }
                            });

                        } else {
                            Log.d("UploadLevelActivity","upload non riuscito");
                        }
                    }
                });
            */



    }

    @Override
    protected void onStart() {
        //Log.d("AsyncTask",levelCreateds.toString());
        super.onStart();
    }

    @Override
    public void transferResult(ArrayList<LevelCreated> levelCreateds) {
        this.levelCreateds = levelCreateds;
        adapterLevelCreated = new AdapterLevelCreated(UploadLevelActivity.this,R.layout.row_layout,levelCreateds,UploadLevelActivity.this);
        listViewLevels.setAdapter(adapterLevelCreated);
        loadingDialog.dismissDialog();
        listViewLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("UploadLevelActivity","item" + adapterLevelCreated.getItem(i).toString());
            }
        });

    }

    public void setLevelCreateds(ArrayList<LevelCreated> levelCreateds){
        this.levelCreateds = levelCreateds;
    }


}