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
import com.google.android.gms.tasks.Tasks;
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
                                    String nicknameInserted = nickname.getText().toString();

                                    boolean nicknameUsable = checkIfNicnameIsUsable(nicknameInserted,task);

                                    if(nicknameUsable){
                                        insertNicknameToDatabase(db,nicknameInserted,account);
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Nickname esistente",Toast.LENGTH_SHORT);
                                    }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

    }

    public void  insertNicknameToDatabase(FirebaseFirestore db,String nicknameInserted,GoogleSignInAccount account){

        Map<String, Object> utente = new HashMap<>();
        utente.put("nickname", nicknameInserted);

        db.collection("utenti").document(account.getEmail())
                .set(utente)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myIntent.putExtra("nickname",nicknameInserted);
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
    public boolean checkIfNicnameIsUsable(String nickname,Task<QuerySnapshot> task){
        Map<String, Object> nicknameTmp = new HashMap<>();
        boolean result = true;
        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());
            nicknameTmp.putAll(document.getData());
            if (nicknameTmp.get("nickname").toString().equals(nickname)) {
                Toast.makeText(RegistrationActivity.this, "Nickname già esistente!", Toast.LENGTH_SHORT).show();
                result = false;
                break;
            }
        }

        return result;

    }

}
