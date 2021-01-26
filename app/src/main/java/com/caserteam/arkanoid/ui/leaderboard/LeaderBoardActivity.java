package com.caserteam.arkanoid.ui.leaderboard;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.caserteam.arkanoid.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class LeaderBoardActivity extends AppCompatActivity {
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_leader_board);
        manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        Fragment fragmentOutput = new FragmentListScore(this);
        fragmentTransaction.replace(R.id.fragment,fragmentOutput);
        fragmentTransaction.commit();



    }
}
