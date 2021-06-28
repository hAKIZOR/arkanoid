package com.caserteam.arkanoid.editor.ui_search_check;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import com.caserteam.arkanoid.NetworkCheck.OfflineFragment;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.caserteam.arkanoid.AppContractClass.*;
import java.util.ArrayList;

public class LevelsSearchActivity extends AppCompatActivity {
    private ArrayList<LevelSearchedModel> levelsSearched;
    private AdapterLevelSearched adapterLevelSearched;
    private ListView listViewLevelsSearched;
    private LoadingDialog loadingDialog;
    private  String nickname;



    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_search);

        getSupportActionBar().setTitle(R.string.select_level_to_play);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listViewLevelsSearched = findViewById(R.id.listLevelsToPlay);
        levelsSearched = new ArrayList<LevelSearchedModel>();

        loadingDialog = new LoadingDialog(LevelsSearchActivity.this);
        loadingDialog.startDialog(getResources().getString(R.string.load_level_to_search));

        nickname = getIntent().getStringExtra(CURRENT_USER_NICKNAME_EXTRA);
        db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_SHARED_LEVELS).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // travaso le info dal database a levelsSearched
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot levelShared : task.getResult()) {
                                levelsSearched.add(levelShared.toObject(LevelSearchedModel.class));
                            }
                            int count = levelsSearched.size();
                            String quantity = getResources().getQuantityString(R.plurals.numberOfLevelSearched,count,count);
                            Toast.makeText(LevelsSearchActivity.this, quantity ,Toast.LENGTH_LONG).show();
                            //aggiorno l'adapter per poi settare la listView che contiene i livelli

                            adapterLevelSearched = new AdapterLevelSearched(LevelsSearchActivity.this,R.layout.row_layout_leves_to_play,
                            levelsSearched,LevelsSearchActivity.this,COLLECTION_SHARED_LEVELS);
                            listViewLevelsSearched.setAdapter(adapterLevelSearched);
                            loadingDialog.dismissDialog();

                        } else {
                            loadingDialog.dismissDialog();
                            OfflineFragment offlineFragment = new OfflineFragment();
                            offlineFragment.show(getSupportFragmentManager(),"Dialog");
                        }
                    }
                });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask (this);
                break;
        }
        return true;
    }
}