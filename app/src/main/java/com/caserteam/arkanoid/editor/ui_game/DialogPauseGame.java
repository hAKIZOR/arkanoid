package com.caserteam.arkanoid.editor.ui_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogPauseGame extends DialogFragment{

    ImageButton buttonResume ;
    ImageButton buttonExitGame;
    Activity activity;
    GameSearched.GameSearchedListener listener;
    public DialogPauseGame(Activity activity, GameSearched.GameSearchedListener listener) {
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
               Intent intent = new Intent(activity, LevelsSearchActivity.class);
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
