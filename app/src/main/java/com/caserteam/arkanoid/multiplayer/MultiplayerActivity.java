package com.caserteam.arkanoid.multiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class MultiplayerActivity extends AppCompatActivity implements DialogCodeRoom.DialogCodeRoomListener {
   private static final String TAG = "MultiplayerActivity";
    private static final String ROOT = "https://arkanoid-d46b0-default-rtdb.europe-west1.firebasedatabase.app/";


    DatabaseReference roomRef;
    GoogleSignInAccount account;
    FirebaseUser accountF;
    FirebaseAuth auth;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
    public void onClickCreateRoomListener(String code) {
        roomRef =  FirebaseDatabase.getInstance(ROOT).getReference("rooms/"+code);
        room = new Room(account.getEmail(),"",0,0,0,0);
        roomRef.setValue(room);
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo che qualcuno acceda");

        addRoomEventListener(loadingDialog);
        //roomRef.child(Keys).child("idRoom").setValue(code);
        //roomRef.child(Keys).child("player1").setValue(account.getEmail());
    }


    @Override
    public void onClickJoinRoomListener(String code) {
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo il caricamento della partita");
        roomRef =  FirebaseDatabase.getInstance(ROOT).getReference("rooms/"+code+"/player2");
        roomRef.setValue(account.getEmail());

        //accedo al gioco
        Intent intent = new Intent(MultiplayerActivity.this, ActualGameActivity.class);
        startActivity(intent);

        System.out.println(roomRef.get().toString());
        System.out.println(roomRef.getKey());

    }

    private void addRoomEventListener(LoadingDialog load){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.child("player2").getValue().toString() != ""){
                    load.dismissDialog();
                    Intent intent = new Intent(MultiplayerActivity.this,ActualGameActivity.class);
                    startActivity(intent);
                }
                //aggregazione alla stanza
                Log.w(TAG, snapshot.child("player2").getValue().toString());
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


