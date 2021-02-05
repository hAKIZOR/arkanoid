package com.caserteam.arkanoid;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public  class LoginActivity extends AppCompatActivity {
        private SignInButton signInButton;
        private GoogleSignInClient mGoogleSignInClient;
        private  String TAG = "LoginActivity";
        private FirebaseAuth mAuth;
        private Button guestButton;
        private int RC_SIGN_IN = 1;
        GoogleSignInAccount account;
        DatabaseReference roomRef;
        public static final String KEY_PREFERENCES_USER_INFORMATION ="UserInformation";
        public static final String KEY_NICKNAME_PREFERENCES = "nickname";
        private static final String USERS_COLLECTION = "utenti";
        public static final String NICKNAME_GUEST_PLAYER = "GuestPlayer";
        SharedPreferences preferences ;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            getSupportActionBar().hide();
            preferences = getSharedPreferences(KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
            TextView textArka =  findViewById(R.id.arkanTextView);
            animate(textArka);

            ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
            ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
            setImageLoop(backgroundOne,backgroundTwo);

            signInButton = findViewById(R.id.sign_in_button);
            mAuth = FirebaseAuth.getInstance();
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



            guestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    preferences.edit().putString(KEY_NICKNAME_PREFERENCES,NICKNAME_GUEST_PLAYER).commit();
                    startActivity(intent);
                }
            });
        }


    private void setImageLoop(ImageView backgroundOne, ImageView backgroundTwo) {

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = backgroundOne.getHeight();
                final float translationY = height * progress;
                backgroundOne.setTranslationY(translationY);
                backgroundTwo.setTranslationY(translationY - height);

            }
        });
        animator.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void animate (View view) {
        Animation mAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new AccelerateInterpolator());
        mAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(mAnimation);
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
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.signed_in_fmt),Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth(acc);
            }
            catch (ApiException e){
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.signin_other_error),Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.signed_in_fmt), Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.signin_other_error), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
            }
            else{
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.signin_other_error), Toast.LENGTH_SHORT).show();
            }
        }

        private void updateUI(FirebaseUser fUser){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

            if(account !=  null){

                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personEmail = account.getEmail();
                Uri personPhoto = account.getPhotoUrl();


                db.collection(USERS_COLLECTION).document(personEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                               //loggati
                                roomRef= FirebaseDatabase.getInstance().getReference();
                                System.out.println(roomRef.getRoot());
                                Map<String, Object> data = new HashMap<>();
                                data.putAll(document.getData());

                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                preferences.edit().putString(KEY_NICKNAME_PREFERENCES,data.get(KEY_NICKNAME_PREFERENCES).toString()).commit();

                                startActivity(intent);

                                }else {
                                //vai nella sezione aggiungi nickname

                                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                                startActivity(intent);

                            }
                        }else {Log.d(TAG, "get failed with ", task.getException());}
                    }});

            }

        }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        hideSystemUI();
        super.onWindowFocusChanged(hasFocus);

    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
