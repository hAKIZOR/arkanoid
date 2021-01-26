package com.caserteam.arkanoid.ui.leaderboard;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailBricks;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailsObstacles;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentVoid;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.gameClasses.Level;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;


public class FragmentListScore extends Fragment {
    private TabLayout tabLayout;
    private ListView listScore;
    private ArrayList<LeaderBoardModel> lModel;
    private AdapterListViewScore adapterListViewScore;
    private Context context;
    public static final int TAB_LOCAL = 0;
    public static final int TAB_GLOBAL = 1;

    public FragmentListScore(){
        super();
    }

    public FragmentListScore(Context context){
        this.context = context;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leader_board,container,false);

        tabLayout = (TabLayout) v.findViewById(R.id.tabLayoutLocalGlobal);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
               
                switch (position){
                    case TAB_LOCAL:
                        //clicco il tab di classifica locale
                        try {
                            setLocalListView(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case TAB_GLOBAL:
                        //clicco il tab di classifica globale
                        setGlobalListView(v);
                        break;


                }
                
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    private void setGlobalListView(View view) {
        listScore = (ListView) view.findViewById(R.id.listScore);
        lModel = new ArrayList<>();
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog("caricamento");
        // riempio l'arrayList di valori
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("leaderboard").orderBy("score", Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    lModel.add(documentSnapshot.toObject(LeaderBoardModel.class));
                }
                loadingDialog.dismissDialog();
                if(lModel.size() > 0) {
                    adapterListViewScore = new AdapterListViewScore(context,R.layout.row_layout_leader_board,lModel);
                    listScore.setAdapter(adapterListViewScore);
                } else {
                    Toast.makeText(context,"nessun giocatore presente nella classifica",Toast.LENGTH_SHORT);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"impossibile caricare i dati",Toast.LENGTH_SHORT);
            }
        });
        //setto l'adapter


    }

    private void setLocalListView(View view) throws IOException {
        listScore = (ListView) view.findViewById(R.id.listScore);
        lModel = new ArrayList<>();
        Cursor c = null;
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        myDbHelper.openDataBase();
        c = myDbHelper.query("leaderboard", null, null, null, null, null, "score"+" DESC");

        // riempio l'arrayList di valori
        while(c.moveToNext()) {
            lModel.add(new LeaderBoardModel(c.getString(c.getColumnIndexOrThrow("nickname")),c.getColumnIndexOrThrow("score")));
        }

        c.close();
        myDbHelper.close();


        //setto l'adapter
        adapterListViewScore = new AdapterListViewScore(context,R.layout.row_layout_leader_board,lModel);
        listScore.setAdapter(adapterListViewScore);
    }

}