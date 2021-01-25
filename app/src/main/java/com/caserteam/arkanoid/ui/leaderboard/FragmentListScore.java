package com.caserteam.arkanoid.ui.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailBricks;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailsObstacles;
import com.caserteam.arkanoid.editor.ui_plus_check.FragmentVoid;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
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
                        setLocalListView(v);
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

        // riempio l'arrayList di valori
        lModel = new ArrayList<LeaderBoardModel>();
        lModel.add(new LeaderBoardModel("minnie","100"));
        lModel.add(new LeaderBoardModel("topolino","200"));
        lModel.add(new LeaderBoardModel("minnie","100"));
        lModel.add(new LeaderBoardModel("topolino","200"));
        lModel.add(new LeaderBoardModel("minnie","100"));
        lModel.add(new LeaderBoardModel("topolino","200"));
        lModel.add(new LeaderBoardModel("minnie","100"));
        lModel.add(new LeaderBoardModel("topolino","200"));
        lModel.add(new LeaderBoardModel("minnie","100"));
        lModel.add(new LeaderBoardModel("topolino","200"));

        //setto l'adapter
        adapterListViewScore = new AdapterListViewScore(context,R.layout.row_layout_leader_board,lModel);
        listScore.setAdapter(adapterListViewScore);
    }

    private void setLocalListView(View view) {
        listScore = (ListView) view.findViewById(R.id.listScore);

        // riempio l'arrayList di valori
        lModel = new ArrayList<LeaderBoardModel>();
        lModel.add(new LeaderBoardModel("pippo","100"));
        lModel.add(new LeaderBoardModel("pluto","200"));
        lModel.add(new LeaderBoardModel("pippo","100"));
        lModel.add(new LeaderBoardModel("pluto","200"));
        lModel.add(new LeaderBoardModel("pippo","100"));
        lModel.add(new LeaderBoardModel("pluto","200"));
        lModel.add(new LeaderBoardModel("pippo","100"));
        lModel.add(new LeaderBoardModel("pluto","200"));
        lModel.add(new LeaderBoardModel("pippo","100"));
        lModel.add(new LeaderBoardModel("pippo","100"));

        //setto l'adapter
        adapterListViewScore = new AdapterListViewScore(context,R.layout.row_layout_leader_board,lModel);
        listScore.setAdapter(adapterListViewScore);
    }

}