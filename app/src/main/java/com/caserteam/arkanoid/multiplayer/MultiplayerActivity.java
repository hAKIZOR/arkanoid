package com.caserteam.arkanoid.multiplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.caserteam.arkanoid.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.List;

public class MultiplayerActivity extends AppCompatActivity {
   private static final String TAG = "MultiplayerActivity";

    FirebaseDatabase firebaseDatabase ;
    DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Button matchMakingButton = findViewById(R.id.button_matchmaking);
        Button privateButton = findViewById(R.id.button_private);
         firebaseDatabase = FirebaseDatabase.getInstance();
         roomRef = firebaseDatabase.getReference("message");

        matchMakingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
