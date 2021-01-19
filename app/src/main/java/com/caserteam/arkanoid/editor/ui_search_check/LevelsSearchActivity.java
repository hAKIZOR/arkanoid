package com.caserteam.arkanoid.editor.ui_search_check;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.caserteam.arkanoid.R;

public class LevelsSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_search);
        getSupportActionBar().setTitle(R.string.select_level_to_play);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}