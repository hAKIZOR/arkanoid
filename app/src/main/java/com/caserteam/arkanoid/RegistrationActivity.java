package com.caserteam.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity = ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        EditText nickname = findViewById(R.id.editTextNickname);
        Button avanti = findViewById(R.id.button_avanti);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());



        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("utenti")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> nicknames = new HashMap<>();
                                    boolean nicknameUsable = true;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        nicknames.putAll(document.getData());
                                        if (nicknames.get("nickname").toString().equals(nickname.getText().toString())) {
                                            Toast.makeText(RegistrationActivity.this, "Nickname già esistente!", Toast.LENGTH_SHORT).show();
                                            nicknameUsable = false;
                                            break;
                                        }

                                    }

                                    if(nicknameUsable){
                                    Map<String, Object> utente = new HashMap<>();
                                    utente.put("nickname", nickname.getText());

                                        db.collection("utenti").document(account.getEmail())
                                                .set(utente)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                        startActivity(myIntent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

    }

}
