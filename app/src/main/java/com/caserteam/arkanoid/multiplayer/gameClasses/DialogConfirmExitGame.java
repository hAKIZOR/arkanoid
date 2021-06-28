package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.AudioUtils;
import com.caserteam.arkanoid.multiplayer.gameClasses.GameListener;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

public class DialogConfirmExitGame extends DialogFragment {

    Button buttonNo;
    Button buttonYes;
    Activity activity;
    GameListener listener;
    public DialogConfirmExitGame(Activity activity, GameListener listener) {
        this.activity = activity;
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_exit_confirm_dialog,container,false);
        getDialog().setCanceledOnTouchOutside(false);
        buttonNo = v.findViewById(R.id.imageButtonResume);
        buttonYes = v.findViewById(R.id.imageButtonExitGame);

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onResumeGame();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onExitGame(true);
                Intent intent = NavUtils.getParentActivityIntent (activity);
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
