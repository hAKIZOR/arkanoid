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
import com.caserteam.arkanoid.editor.editor_module.Editor;
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
    private  String nickname;
    public static final String COLLECTION_SHARED_LEVEL = "livelliCondivisi";
    public static final String STRUCTURE_GAME_EXTRA = "structure";

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

        listViewLevelsSearched = findViewById(R.id.listLevelsToPlay);
        levelsSearched = new ArrayList<LevelSearchedModel>();

        loadingDialog = new LoadingDialog(LevelsSearchActivity.this);
        loadingDialog.startDialog(getResources().getString(R.string.load_level_to_search));

        nickname = getIntent().getStringExtra(EditorActivity.STATE_CURRENT_USER_NICKNAME);
        db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_SHARED_LEVEL).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // travaso le info dal database a levelsSearched
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot levelShared : task.getResult()) {
                                if(!levelShared.get(FIELD_NICKNAME).equals(nickname)){
                                    levelsSearched.add(levelShared.toObject(LevelSearchedModel.class));
                                }
                            }
                            //aggiorno l'adapter per poi settare la listView che contiene i livelli

                            adapterLevelSearched = new AdapterLevelSearched(LevelsSearchActivity.this,R.layout.row_layout_leves_to_play,
                            levelsSearched,LevelsSearchActivity.this,COLLECTION_SHARED_LEVEL);
                            listViewLevelsSearched.setAdapter(adapterLevelSearched);
                            loadingDialog.dismissDialog();

                        } else {

                        }


                    }
                });


    }
}