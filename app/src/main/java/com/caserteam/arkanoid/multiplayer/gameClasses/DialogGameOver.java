package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;

public class DialogGameOver extends DialogFragment {
    private String textResult;
    private TextView textViewResult;
    private Activity activity;
    private Button buttonGoOn;
    private String scoreMessage;
    private TextView textViewScore;

    public DialogGameOver(String textResult, Activity activity, String score) {
        this.textResult = textResult;
        this.activity = activity;
        this.scoreMessage = score;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_over_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        textViewResult = (TextView) v.findViewById(R.id.resultGameTextView);
        textViewScore = (TextView) v.findViewById(R.id.textViewScore);
        buttonGoOn = (Button) v.findViewById(R.id.buttonGoOn);

        textViewResult.setText(textResult);
        textViewScore.setText(scoreMessage);

        buttonGoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
