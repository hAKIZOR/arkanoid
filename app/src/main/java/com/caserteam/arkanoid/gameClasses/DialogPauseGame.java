package com.caserteam.arkanoid.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.BackgroundSoundService;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;

public class DialogPauseGame extends DialogFragment{

    ImageButton buttonResume ;
    ImageButton buttonExitGame;
    Activity activity;
    GameListener listener;
    public DialogPauseGame(Activity activity, GameListener listener) {
        this.activity = activity;
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.pause_game_dialog,container,false);
       buttonResume = v.findViewById(R.id.imageButtonResume);
       buttonExitGame = v.findViewById(R.id.imageButtonExitGame);

       buttonResume.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               listener.onResumeGame();
           }
       });

       buttonExitGame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent svc = new Intent(activity, BackgroundSoundService.class);
               activity.startService(svc);
               Intent intent = new Intent(activity, MenuActivity.class);
               activity.startActivity(intent);
               activity.finish();
           }
       });
       return v;
    }

    private void closeDialog() {
        this.dismiss();
    }

}
