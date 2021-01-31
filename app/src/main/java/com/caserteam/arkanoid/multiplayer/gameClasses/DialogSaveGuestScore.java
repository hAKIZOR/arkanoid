package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.R;

import java.io.IOException;

public class DialogSaveGuestScore extends DialogFragment {
    private static final String USERS_COLLECTION = "utenti";
    private static final String LEVELS_COLLECTION = "livelli";

    private Button buttonSave;
    private EditText editTextPlayerName;
    private int score;
    private String nickname;
    private Context context;
    private Activity activity;

    public DialogSaveGuestScore(int score, Context context, Activity activity) {
        this.score = score;
        this.context = context;
        this.activity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.guest_score_fragment_dialog,container,false);


       buttonSave = v.findViewById(R.id.buttonCreateRoom);
       editTextPlayerName = v.findViewById(R.id.editTextNicknameInsert);

        if(nickname != null){
            editTextPlayerName.setText(nickname);
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = null;
                try {
                    dbHelper = new DatabaseHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    Log.d("dialogSaveGuestScore",String.valueOf(score));
                    values.put("score", score);
                    values.put("nickname", editTextPlayerName.getText().toString());
                    db.insertOrThrow("leaderboard", null, values);
                } catch (SQLException e) {
                    Toast.makeText(context, "Punteggio gia esistente con questo nickname", Toast.LENGTH_SHORT).show();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                dismiss();
                activity.finish();
            }
        });
       return v;
    }
}
