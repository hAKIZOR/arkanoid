package com.caserteam.arkanoid.editor.ui_game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.AudioUtils;
import com.caserteam.arkanoid.gameClasses.GameListener;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

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
        getDialog().setCanceledOnTouchOutside(false);
       buttonResume.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               listener.onResumeGame();
           }
       });

       buttonExitGame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try {
                   AudioUtils.playBackgroundSound(activity,R.raw.welcome_audio);
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               }
               NavUtils.navigateUpFromSameTask(activity);
           }
       });
       return v;
    }

    private void closeDialog() {
        this.dismiss();
    }

}
