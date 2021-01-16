package com.caserteam.arkanoid.multiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class MultiplayerActivity extends AppCompatActivity implements
        DialogCodeRoom.DialogCodeRoomListener,
        LoadingDialog.LoadingDialogClickListener {
    private static final String TAG = "MultiplayerActivity";
    public static final String ROOT = "https://arkanoid-d46b0-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String STATE_CODE = "gameCode";
    public static final String CODE_PLAYER = "player";


    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    GoogleSignInAccount account;
    FirebaseUser accountF;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    Room room;
    SharedPreferences preferences;
    DialogCodeRoom dialogCodeRoom;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        preferences = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);

        firebaseDatabase = FirebaseDatabase.getInstance(ROOT);

        roomsRef = firebaseDatabase.getReference("rooms");

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
                dialogCodeRoom = new DialogCodeRoom();
                dialogCodeRoom.show(getSupportFragmentManager(),"DialogFragmentCodeRoom");
            }
        });

    }
    @Override
    public void onClickJoinRoom(String code) {
        loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo il caricamento della partita");

        roomRef = firebaseDatabase.getReference("rooms/"+code);
        DatabaseReference playerRef = firebaseDatabase.getReference("rooms/"+code +"/player2");
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);

        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d(TAG,snapshot.child("player1").getValue().toString());
                ValueEventListener thisValueEventListener = this;
                if(snapshot.exists()){

                    playerRef.setValue(nickname, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            loadingDialog.dismissDialog();
                            //accedo al gioco
                            Intent intent = new Intent(MultiplayerActivity.this, ActualGameActivity.class);
                            intent.putExtra(STATE_CODE,code);
                            intent.putExtra(CODE_PLAYER,"player2");
                            roomsRef.removeEventListener(thisValueEventListener);
                            startActivity(intent);
                        }
                    });


                } else {
                    loadingDialog.dismissDialog();
                    dialogCodeRoom.dismiss();
                    Toast.makeText(MultiplayerActivity.this,"stanza ancora non creata",Toast.LENGTH_SHORT).show();
                    roomsRef.removeEventListener(thisValueEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        System.out.println(roomRef.get().toString());
        System.out.println(roomRef.getKey());

    }


    @Override
    public void onClickCreateRoom(String code) {

        loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog("attendo che qualcuno acceda");
        loadingDialog.setDataToCancel(code);
        loadingDialog.setVisibleClick(true);

        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(LoginActivity.KEY_NICKNAME_PREFERENCES);

        addRoomsEventListener(loadingDialog,code,nickname);
        //roomRef.child(Keys).child("idRoom").setValue(code);
        //roomRef.child(Keys).child("player1").setValue(account.getEmail());
    }


    private void addRoomsEventListener(LoadingDialog load, String code, String nickname){


        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mostra le liste di rooms
                if(!snapshot.hasChild(code)){
                    // non esiste nelle rooms una room con il codice inserito
                    DataSnapshot roomToHaveAccess = snapshot.child(code);
                    createRoom(load,code,nickname,roomToHaveAccess);

                } else {
                    //esiste nelle rooms una room con il codice inserito
                    load.dismissDialog();
                    //dialogCodeRoom.dismiss();
                    Toast.makeText(MultiplayerActivity.this,"esiste una room con questo codice",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }

    public void createRoom(LoadingDialog load, String code, String nickname, DataSnapshot roomToHaveAccess){

        DatabaseReference roomRef = firebaseDatabase.getReference("rooms/"+code);
        room = new Room(nickname,"",0,0,7,-14);
        roomRef.setValue(room, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                ref.child("player2").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null){
                            if(!snapshot.getValue().equals("")){
                                if(!snapshot.getValue().equals(nickname)){
                                    // il giocatore accede alla stanza poichè
                                    load.dismissDialog();
                                    Intent intent = new Intent(MultiplayerActivity.this,ActualGameActivity.class);
                                    intent.putExtra(STATE_CODE,code);
                                    intent.putExtra(CODE_PLAYER,"player1");
                                    ref.child("player2").removeEventListener(this);
                                    startActivity(intent);


                                } else {
                                    loadingDialog.dismissDialog();
                                    dialogCodeRoom.dismiss();
                                    Toast.makeText(MultiplayerActivity.this,"Risulti loggato in un altro dispositivo, fai il serio ed esci",Toast.LENGTH_SHORT);
                                    ref.child("player2").removeEventListener(this);
                                }
                            }
                        } else {
                            ref.child("player2").removeEventListener(this);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



    }


    @Override
    public void onClickButtonCancel(Object roomToCancel) {

        String room =(String) roomToCancel;
        DatabaseReference roomToDelete = firebaseDatabase.getReference("rooms").child(room);
        roomToDelete.removeValue( new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                loadingDialog.dismissDialog();
                dialogCodeRoom.dismiss();
            }
        });

    }
}


