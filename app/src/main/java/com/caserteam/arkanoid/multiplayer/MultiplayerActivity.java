package com.caserteam.arkanoid.multiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.multiplayer.gameClasses.ActualGameActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MultiplayerActivity extends AppCompatActivity implements DialogCodeRoom.DialogCodeRoomListener {
    private static final String TAG = "MultiplayerActivity";
    public static final String ROOT = "https://arkanoid-d46b0-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String STATE_CODE = "gameCode";
    public static final String CODE_PLAYER = "player";


    DatabaseReference roomRef;
    GoogleSignInAccount account;
    FirebaseUser accountF;
    FirebaseAuth auth;
    Room room;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
        setContentView(R.layout.activity_multiplayer);
        Button matchMakingButton = findViewById(R.id.button_matchmaking);
        Button privateButton = findViewById(R.id.button_private);
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        matchMakingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCodeRoom dialogCodeRoom = new DialogCodeRoom();
                dialogCodeRoom.show(getSupportFragmentManager(),"DialogFragmentCodeRoom");
            }
        });

    }



    @Override
    public void onClickCreateRoom(String code) {
        roomRef =  FirebaseDatabase.getInstance(ROOT).getReference("rooms/"+code);

        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);

        room = new Room(nickname,"",0,0);
        roomRef.setValue(room);
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo che qualcuno acceda");

        addRoomEventListener(loadingDialog,code);
        //roomRef.child(Keys).child("idRoom").setValue(code);
        //roomRef.child(Keys).child("player1").setValue(account.getEmail());
    }


    @Override
    public void onClickJoinRoom(String code) {
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo il caricamento della partita");
        roomRef =  FirebaseDatabase.getInstance(ROOT).getReference("rooms/"+code+"/player2");
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);
        roomRef.setValue(nickname);

        //accedo al gioco
        Intent intent = new Intent(MultiplayerActivity.this, ActualGameActivity.class);
        intent.putExtra(STATE_CODE,code);
        intent.putExtra(CODE_PLAYER,"player2");
        startActivity(intent);

        System.out.println(roomRef.get().toString());
        System.out.println(roomRef.getKey());

    }

    private void addRoomEventListener(LoadingDialog load, String code){

        roomRef.child("player2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.getValue().toString() != ""){
                    load.dismissDialog();
                    Intent intent = new Intent(MultiplayerActivity.this,ActualGameActivity.class);
                    intent.putExtra(STATE_CODE,code);
                    intent.putExtra(CODE_PLAYER,"player1");
                    startActivity(intent);
                    roomRef.child("player2").removeEventListener(this);

                }
                //aggregazione alla stanza
                Log.w(TAG, snapshot.getValue().toString());
               // Intent intent = new Intent(getApplicationContext(), ActualGameActivity.class);
                // startActivity(intent);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //errore

                Log.w(TAG, "loadPost:onCancelled", error.toException());


            }
        });
    }

    /*private void addRoomsEventListener(){
        roomsRef = firebaseDatabase.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mostra le liste di rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for (DataSnapshot dataSnapshot : rooms) {
                    roomsList.add(dataSnapshot.getKey());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectionPlayerActivity.this,
                            android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);

                }
            }


        });
    }*/
}


