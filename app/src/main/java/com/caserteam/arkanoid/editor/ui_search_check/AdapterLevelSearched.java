package com.caserteam.arkanoid.editor.ui_search_check;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.audio.AudioUtils;
import com.caserteam.arkanoid.editor.ui_game.GameSearchedActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import static com.caserteam.arkanoid.AppContractClass.*;

class AdapterLevelSearched extends ArrayAdapter<LevelSearchedModel> {

    private Context mContext;
    private int  mResource;
    private ArrayList<LevelSearchedModel> levelsSearched;
    private Activity activity;
    private String pathCollection;
    private ImageButton buttonPlay;
    private TextView textViewLevel;
    private TextView textAuthor;
    FirebaseFirestore db;
    private static final String TAG = "LevelsSearchActivity";
    private static final String FIELD_NAME_LEVEL = "nomeLivello";



    public AdapterLevelSearched(Context context, int resource, ArrayList<LevelSearchedModel> levelsSearched,
                                Activity activity, String pathCollection) {

        super(context,resource,levelsSearched);
        this.mContext = context;
        this.mResource = resource;
        this.levelsSearched = levelsSearched;
        this.activity = activity;
        this.pathCollection = pathCollection;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView =layoutInflater.inflate(mResource,parent,false);

        db = FirebaseFirestore.getInstance();

        String structure = getItem(position).getStruttura();
        String nameLevel = getItem(position).getNomeLivello();
        String autore = getItem(position).getNickname();

        textViewLevel = convertView.findViewById(R.id.textViewAuthor);
        textViewLevel.setText(activity.getResources().getString(R.string.author_level) + autore);

        textViewLevel = convertView.findViewById(R.id.nicknamePlayer);
        textViewLevel.setText(nameLevel);

        buttonPlay = (ImageButton) convertView.findViewById(R.id.playButton);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //faccio partire il livello così da permettere al giocatore di giocarci
                try {
                    AudioUtils.stopBackgroundSound(activity);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, GameSearchedActivity.class);
                intent.putExtra(STRUCTURE_GAME_EXTRA,structure);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }
}
