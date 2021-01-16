package com.caserteam.arkanoid;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.gameClasses.MainActivity;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;
import com.caserteam.arkanoid.multiplayer.Room;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity = ";
    private static final String FIRST_RUN_INSTALLATION_STATE ="firstruninstallation";
    private static final String FIRST_RUN_STATE ="firstrun";
    private GoogleSignInClient mGoogleSignInClient;
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();
        TextView textArka =  findViewById(R.id.arkanTextView);
        animate(textArka);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        Button buttonArcade = findViewById(R.id.button_arcade);
        FloatingActionButton buttonSettings = findViewById(R.id.button_settings);
        Button buttonLeaderBoard = findViewById(R.id.button_leaderboard);
        FloatingActionButton buttonLogout = findViewById(R.id.floatingActionLogoutButton);
        TextView name = findViewById(R.id.name);
        ImageView photoProfile = findViewById(R.id.imageProfile);

        Button buttonEditor = findViewById(R.id.button_editor);
        Button buttonMultiplayer = findViewById(R.id.button_multiplayer);

        Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        Animation animationBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        Animation animationTop = AnimationUtils.loadAnimation(this, R.anim.top_animation);


        buttonSettings.setAnimation(animationScale);
        buttonLogout.setAnimation(animationScale);

        buttonArcade.setAnimation(animationTop);
        buttonMultiplayer.setAnimation(animationTop);
        buttonLeaderBoard.setAnimation(animationBottom);
        buttonEditor.setAnimation(animationBottom);


        try {
            SharedPreferences preferences = getSharedPreferences(LoginActivity.KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
            Map<String,String> data = new HashMap<>();
            data.putAll((Map<String,String>) preferences.getAll());
            name.setText("Benvenuto  " + data.get(LoginActivity.KEY_NICKNAME_PREFERENCES));
            Picasso.get().load(account.getPhotoUrl()).into(photoProfile);
        } catch (Exception e){

        }

        try {
            prefs = getSharedPreferences("com.caserteam.arkanoid", MODE_PRIVATE);

            String systemLanguage = Locale.getDefault().getLanguage();
            int languageToSet = Settings.lang.valueOf(systemLanguage).ordinal();

            if (prefs.getBoolean(this.FIRST_RUN_INSTALLATION_STATE, true)){
                Log.d(TAG, "primo avvio");

                Settings settings = new Settings(1,languageToSet ,1);
                IOUtils.writeObjectToFile(this,Settings.FILE_NAME,settings);

            } else if(prefs.getBoolean(this.FIRST_RUN_STATE,true)){

                Settings settings = IOUtils.readObjectFromFile(this,Settings.FILE_NAME);
                settings.setLanguage(languageToSet);
                IOUtils.writeObjectToFile(this,Settings.FILE_NAME,settings);

            }


        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mGoogleSignInClient.signOut();
                startActivity(myIntent);
                finish();
            }
        });

        buttonArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });
         buttonLeaderBoard.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent myIntent = new Intent(MenuActivity.this, LeaderBoardActivity.class);
                 MenuActivity.this.startActivity(myIntent);
             }

         });
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });


        buttonEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account!=null){
                Intent intent = new Intent(MenuActivity.this, EditorActivity.class);
                startActivity(intent);
                }else  {
                    Toast t =Toast.makeText(MenuActivity.this,R.string.need_login,Toast.LENGTH_SHORT);
                    centerText(t.getView());
                    t.show();
                }
            }
        });


        buttonMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account!=null){
                    Intent intent = new Intent(MenuActivity.this, MultiplayerActivity.class);
                    startActivity(intent);
                }else {
                    Toast t =Toast.makeText(MenuActivity.this,R.string.need_login,Toast.LENGTH_SHORT);
                    centerText(t.getView());
                    t.show();
                }
            }
        });
    }
    void centerText(View view) {
        if( view instanceof TextView){
            ((TextView) view).setGravity(Gravity.CENTER);
        }else if( view instanceof ViewGroup){
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for( int i = 0; i<n; i++ ){
                centerText(group.getChildAt(i));
            }
        }
    }
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");
    }
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
    }
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.edit().putBoolean(this.FIRST_RUN_STATE, true).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        if (prefs.getBoolean(this.FIRST_RUN_INSTALLATION_STATE, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(this.FIRST_RUN_INSTALLATION_STATE, false).commit();
        }
        if (prefs.getBoolean(this.FIRST_RUN_STATE, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(this.FIRST_RUN_STATE, false).commit();
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

}
