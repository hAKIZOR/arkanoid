package com.caserteam.arkanoid.multiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.caserteam.arkanoid.LoginActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.multiplayer.gameClasses.ActualGameActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import static com.caserteam.arkanoid.AppContractClass.*;

public class MultiplayerActivity extends AppCompatActivity implements
        DialogCodeRoom.DialogCodeRoomListener,
        LoadingDialog.LoadingDialogClickListener {
    private static final String TAG = "MultiplayerActivity";


    DatabaseReference roomsRef;
    FirebaseDatabase firebaseDatabase;
    Room room;
    SharedPreferences preferences;
    DialogCodeRoom dialogCodeRoom;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        preferences = getSharedPreferences(KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);

        firebaseDatabase = FirebaseDatabase.getInstance(ROOT_DB_REALTIME_DATABASE);

        roomsRef = firebaseDatabase.getReference(ROOMS_NODE);

        Button matchMakingButton = findViewById(R.id.button_matchmaking);
        Button privateButton = findViewById(R.id.button_private);


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
        loadingDialog.startDialog(getResources().getString(R.string.wait_loading_game_room));

        DatabaseReference player2Ref = firebaseDatabase.getReference(ROOMS_NODE+ "/" + code + "/" + PLAYER2_NODE);
        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(KEY_NICKNAME_PREFERENCES);

        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ValueEventListener thisValueEventListener = this;
                if(snapshot.hasChild(code)){
                    if(!snapshot.child(code).child(PLAYER1_NODE).getValue().equals(nickname)){
                        if(snapshot.child(code).child(PLAYER2_NODE).getValue().equals(EMPTY_STRING)){
                            player2Ref.setValue(nickname, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    loadingDialog.dismissDialog();
                                    //accedo al gioco
                                    Intent intent = new Intent(MultiplayerActivity.this, ActualGameActivity.class);
                                    intent.putExtra(CODE_ROOM_EXTRA,code);
                                    intent.putExtra(CODE_PLAYER_EXTRA,PLAYER2_NODE);
                                    roomsRef.removeEventListener(thisValueEventListener);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            //esiste già un giocatore 2 all'interno della stanza
                            showErrorMessage(getResources().getString(R.string.error_room_already_taken));
                            roomsRef.removeEventListener(thisValueEventListener);

                        }
                    } else {
                        //il giocatore tenta di accedere ad una stanza con lo stesso profilo (CASO LIMITE DA PREVEDERE)
                        showErrorMessage(getResources().getString(R.string.error_access_room_with_same_profile));
                        roomsRef.removeEventListener(thisValueEventListener);
                    }

                } else {
                    // non è possibile accedere alla stanza poichè non è stata ancora creata
                    showErrorMessage(getResources().getString(R.string.error_room_already_created));
                    roomsRef.removeEventListener(thisValueEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onClickCreateRoom(String code) {

        loadingDialog = new LoadingDialog(MultiplayerActivity.this);
        loadingDialog.startDialog(getResources().getString(R.string.wait_access_room));
        loadingDialog.setDataToCancel(code);
        loadingDialog.setVisibleClick(true);

        HashMap<String,String> data = new HashMap<>();
        data.putAll((Map<String,String>) preferences.getAll());
        String nickname = data.get(KEY_NICKNAME_PREFERENCES);

        addRoomsEventListener(loadingDialog,code,nickname);

    }


    private void addRoomsEventListener(LoadingDialog load, String code, String nickname){


        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot nodeRooms) {
                //mostra le liste di rooms
                if(!nodeRooms.hasChild(code)){
                    // non esiste nelle rooms una room con il codice inserito
                    DataSnapshot roomToHaveAccess = nodeRooms.child(code);
                    createRoom(load,code,nickname,roomToHaveAccess);

                } else {
                    //esiste nelle rooms una room con il codice inserito
                    showErrorMessage(getResources().getString(R.string.error_exist_room));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }

    public void createRoom(LoadingDialog load, String code, String nickname, DataSnapshot roomToHaveAccess) {
        // creazione della stanza con i relativi controlli
        DatabaseReference roomRef = firebaseDatabase.getReference(ROOMS_NODE + "/" + code);
        room = new Room(nickname,EMPTY_STRING,0,0,0,0,0,0,false,0,0,0,0,0,3);

        roomRef.setValue(room, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                ref.child(PLAYER2_NODE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot fieldPlayer2) {
                        if(fieldPlayer2.getValue() != null){
                            if(! fieldPlayer2.getValue().equals(EMPTY_STRING)){
                                // il giocatore accede alla stanza poichè passa tutti i controlli sulla disponibilità del posto in stanza
                                load.dismissDialog();
                                Intent intent = new Intent(MultiplayerActivity.this,ActualGameActivity.class);
                                intent.putExtra(CODE_ROOM_EXTRA,code);
                                intent.putExtra(CODE_PLAYER_EXTRA,PLAYER1_NODE);
                                ref.child(PLAYER2_NODE).removeEventListener(this);
                                startActivity(intent);
                            }
                        } else {
                            ref.child(PLAYER2_NODE).removeEventListener(this);
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
        // durante il caricamento della stanza il giocatore annulla il tentativo di creazione stanza
        String room =(String) roomToCancel;

        DatabaseReference roomToDelete = firebaseDatabase.getReference(ROOMS_NODE).child(room);
        roomToDelete.removeValue( new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                loadingDialog.dismissDialog();
                dialogCodeRoom.dismiss();
            }
        });

    }

    public void showErrorMessage(String message){
        loadingDialog.dismissDialog();
        Toast.makeText(MultiplayerActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}