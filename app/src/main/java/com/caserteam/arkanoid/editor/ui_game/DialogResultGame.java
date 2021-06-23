package com.caserteam.arkanoid.editor.ui_game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.AudioUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

public class DialogResultGame extends DialogFragment {
    private String textResult;
    private TextView textViewResult;
    private TextView textViewScore;
    private ImageView imageView;
    private Activity activity;
    private Button buttonGoOn;
    private String score;

    public DialogResultGame(String textResult, Activity activity,String score) {
        this.textResult = textResult;
        this.activity = activity;
        this.score = score;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_game_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        textViewResult = (TextView) v.findViewById(R.id.resultGameTextView);
        textViewScore = (TextView) v.findViewById(R.id.textViewScore);
        imageView =(ImageView) v.findViewById(R.id.imageResultGame);
        buttonGoOn = (Button) v.findViewById(R.id.buttonGoOn);

        textViewResult.setText(textResult);
        textViewScore.setText(score);

        if(textResult.equals(activity.getResources().getString(R.string.win_game))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_multiplayer,null));
        } else if(textResult.equals(activity.getResources().getString(R.string.win_game_arcade))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_arcade,null));
        } else if(textResult.equals(activity.getResources().getString(R.string.lose_game))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_lose_multiplayer,null));
        }else if(textResult.equals(activity.getString(R.string.success_record))){
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_arcade,null));
        }else if(textResult.equals(activity.getString(R.string.failure_record))){
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_lose_multiplayer,null));
        }

        buttonGoOn.setOnClickListener(new View.OnClickListener() {
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
