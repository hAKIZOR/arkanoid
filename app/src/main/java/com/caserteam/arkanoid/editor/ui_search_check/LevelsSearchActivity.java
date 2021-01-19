package com.caserteam.arkanoid.editor.ui_search_check;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.editor.ui_upload_check.UploadLevelActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LevelsSearchActivity extends AppCompatActivity {
    private ArrayList<LevelSearchedModel> levelsSearched;
    private AdapterLevelSearched adapterLevelSearched;
    private ListView listViewLevelsSearched;
    private LoadingDialog loadingDialog;
    private String pathOfCollection;
    public static final String COLLECTION_USERS = "utenti";
    public static final String COLLECTION_LEVELS = "livelli";
    public static final String FIELD_NICKNAME = "nickname";

    public static final String FIELD_NAME_LEVEL = "nomeLivello";
    public static final String FIELD_NAME_STRUCTURE_LEVEL = "struttura";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_search);
        getSupportActionBar().setTitle(R.string.select_level_to_play);
        getSupportActionBar().setHomeButtonEnabled(true);
        db = FirebaseFirestore.getInstance();

        String currentUser = getIntent().getStringExtra(EditorActivity.STATE_CURRENT_USER);

        /*db.collection(COLLECTION_USERS).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot user: task.getResult()){
                                if(user.getId() != currentUser ){

                            }
                        }
                    }
                }
        );

        // travaso le info dal database a levelsSearched



        //aggiorno l'adapter per poi settare la listView che contiene i livelli

        adapterLevelSearched = new AdapterLevelSearched(LevelsSearchActivity.this,R.layout.row_layout_levels_created,
        levelsSearched,LevelsSearchActivity.this,COLLECTION_USERS);
        listViewLevelsSearched.setAdapter(adapterLevelSearched);
        loadingDialog.dismissDialog();
         */


    }
}