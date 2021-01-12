package com.caserteam.arkanoid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
        private SignInButton signInButton;
        private GoogleSignInClient mGoogleSignInClient;
        private  String TAG = "LoginActivity";
        private FirebaseAuth mAuth;
        private Button btnSignOut;
        private Button guestButton;
        private int RC_SIGN_IN = 1;
        GoogleSignInAccount account;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            signInButton = findViewById(R.id.sign_in_button);
            mAuth = FirebaseAuth.getInstance();
            btnSignOut = findViewById(R.id.sign_out_button);
            guestButton = findViewById(R.id.guest_button);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoogleSignInClient.signOut();
                    Toast.makeText(LoginActivity.this,"You are Logged Out",Toast.LENGTH_SHORT).show();
                    btnSignOut.setVisibility(View.INVISIBLE);
                }
            });

            guestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtra("nickname","giocatore guest");
                    startActivity(intent);
                }
            });
        }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void signIn(){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == RC_SIGN_IN){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }

        private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
            try{

                GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
                Toast.makeText(LoginActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth(acc);
            }
            catch (ApiException e){
                Toast.makeText(LoginActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth(null);
            }
        }

        private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
            //check if the account is null
            if (acct != null) {
                AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
            }
            else{
                Toast.makeText(LoginActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateUI(FirebaseUser fUser){
            btnSignOut.setVisibility(View.VISIBLE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if(account !=  null){
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personEmail = account.getEmail();
                Uri personPhoto = account.getPhotoUrl();


                db.collection("utenti").document(personEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                               //loggati
                                Map<String, Object> data = new HashMap<>();
                                data.putAll(document.getData());
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("nickname",data.get("nickname").toString());
                                startActivity(intent);

                                }else {
                                //vai nella sezione aggiungi nickname
                                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                                startActivity(intent);

                            }
                        }else {Log.d(TAG, "get failed with ", task.getException());}
                    }});

            } else btnSignOut.setVisibility(View.INVISIBLE);

        }

}