package com.caserteam.arkanoid.multiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_save_check.DialogSaveLevel;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class MultiplayerActivity extends AppCompatActivity implements
        DialogCodeRoom.DialogCodeRoomListener {
   private static final String TAG = "MultiplayerActivity";

    FirebaseDatabase firebaseDatabase ;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Button matchMakingButton = findViewById(R.id.button_matchmaking);
        Button privateButton = findViewById(R.id.button_private);
         firebaseDatabase = FirebaseDatabase.getInstance();
         roomRef = firebaseDatabase.getReference();

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
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo che qualcuno acceda");
        roomRef = firebaseDatabase.getReference();
        //addRoomEventListener();
        roomRef.child("room").push().setValue(code);
    }

    @Override
    public void onClickJoinRoomListener(String code) {
        LoadingDialog loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo il caricamento della partita");
        roomsRef.orderByChild("idRoom").equalTo(code).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            // ...
        });
    }

    private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //aggregazione alla stanza
                Log.w(TAG, "loadPost:onDataChange");
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
/*
    private void addRoomsEventListener(){
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


