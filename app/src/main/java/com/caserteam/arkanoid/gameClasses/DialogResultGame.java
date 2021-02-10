package com.caserteam.arkanoid.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.BackgroundSoundService;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;

public class DialogResultGame extends DialogFragment {
    private String textResult;
    private TextView textViewResult;
    private ImageView imageView;
    private Activity activity;
    private Button buttonGoOn;
    private ImageButton shareButton;
    private String score;
    public DialogResultGame(String textResult, Activity activity, String score) {
        this.textResult = textResult;
        this.activity = activity;
        this.score = score;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.finish_game_dialog, container, false);
        textViewResult = (TextView) v.findViewById(R.id.resultGameTextView);
        imageView =(ImageView) v.findViewById(R.id.imageResultGame);
        buttonGoOn = (Button) v.findViewById(R.id.buttonGoOn);
        shareButton = (ImageButton) v.findViewById(R.id.shareButton);

        textViewResult.setText(textResult);

        if(textResult.equals(activity.getResources().getString(R.string.win_game))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_multiplayer,null));
        } else if(textResult.equals(activity.getResources().getString(R.string.win_game_arcade))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_arcade,null));
        } else if(textResult.equals(activity.getResources().getString(R.string.lose_game))) {
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_lose_multiplayer,null));
        } else if(textResult.equals("Complimenti hai effettuato un nuovo Record")){
            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_win_multiplayer,null));

        }
        buttonGoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(activity, BackgroundSoundService.class);
                activity.startService(service);
                Intent intent = new Intent(activity, LevelsSearchActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Ciao amico guarda il mio nuovo Record sul fantastico gioco ArkanMusic:"+ score +"! Sai fare di meglio?");

                try {
                    activity.startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity,"Whatsapp have not been installed.",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return v;
    }
    private void closeDialog() {
        this.dismiss();
    }
}
