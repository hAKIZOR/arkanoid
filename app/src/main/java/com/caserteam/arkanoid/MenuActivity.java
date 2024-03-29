package com.caserteam.arkanoid;

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
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caserteam.arkanoid.NetworkCheck.NetworkUtil;
import com.caserteam.arkanoid.NetworkCheck.OfflineFragment;
import com.caserteam.arkanoid.audio.AudioUtils;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.gameClasses.GameActivity;
import com.caserteam.arkanoid.multiplayer.MultiplayerActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.caserteam.arkanoid.ui.leaderboard.LeaderBoardActivity;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static com.caserteam.arkanoid.AppContractClass.*;

public class MenuActivity extends AppCompatActivity  {
    private static final String TAG = "MenuActivity = ";

    private GoogleSignInClient mGoogleSignInClient;
    private NetworkUtil networkControl;
    SharedPreferences prefs = null;
    OfflineFragment offlineFragment;
    GoogleSignInAccount account;


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
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if(isUserLogged()){
            networkControl = new NetworkUtil();
            networkControl.checkDialogPresence(this,this);
        }


        Button buttonArcade = findViewById(R.id.button_arcade);
        FloatingActionButton buttonSettings = findViewById(R.id.button_settings);
        Button buttonLeaderBoard = findViewById(R.id.button_leaderboard);
        FloatingActionButton buttonLogout = findViewById(R.id.floatingActionLogoutButton);
        TextView name = findViewById(R.id.name);
        ImageView photoProfile = findViewById(R.id.imageProfile);

        Button buttonEditor = findViewById(R.id.button_editor);
        Button buttonMultiplayer = findViewById(R.id.button_multiplayer);
        Button buttonExit = findViewById(R.id.button_exit);

        ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);


        setImageLoop(backgroundOne,backgroundTwo);

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
            SharedPreferences preferences = getSharedPreferences(KEY_PREFERENCES_USER_INFORMATION,MODE_PRIVATE);
            Map<String,String> data = new HashMap<>();
            data.putAll((Map<String,String>) preferences.getAll());
            name.setText(getResources().getString(R.string.welcome_text) + " " + data.get(KEY_NICKNAME_PREFERENCES));
            Picasso.get().load(account.getPhotoUrl()).into(photoProfile);
        } catch (Exception e){

        }

        try {
            prefs = getSharedPreferences(getResources().getString(R.string.package_name), MODE_PRIVATE);

            String systemLanguage = Locale.getDefault().getLanguage();
            int languageToSet = Settings.lang.valueOf(systemLanguage).ordinal();

            if (prefs.getBoolean(FIRST_RUN_INSTALLATION_STATE, true)){
                Log.d(TAG, "primo avvio");

                Settings settings = new Settings(1,languageToSet ,1);
                IOUtils.writeObjectToFile(this,FILE_NAME,settings);

            } else if(prefs.getBoolean(FIRST_RUN_STATE,true)){

                Settings settings = IOUtils.readObjectFromFile(this,FILE_NAME);
                settings.setLanguage(languageToSet);
                IOUtils.writeObjectToFile(this,FILE_NAME,settings);

            }


        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(isUserLogged()){
                    boolean status = networkControl.checkDialogPresence(getApplicationContext(),MenuActivity.this);
                    if(status){
                        Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mGoogleSignInClient.signOut();
                        startActivity(myIntent);
                        finish();
                    }
                } else {
                        Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mGoogleSignInClient.signOut();
                        startActivity(myIntent);
                        finish();
                }



            }
        });

        buttonArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                    AudioUtils.stopBackgroundSound(MenuActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(isUserLogged()){
                    boolean status = networkControl.checkDialogPresence(getApplicationContext(),MenuActivity.this);
                    if(status) {
                        Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
                        startActivity(myIntent);
                    }
                } else {
                    Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
                    startActivity(myIntent);
                }

            }
        });
         buttonLeaderBoard.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                 } catch (IOException e) {
                     e.printStackTrace();
                 } catch (ClassNotFoundException e) {
                     e.printStackTrace();
                 }
                 Intent myIntent = new Intent(MenuActivity.this, LeaderBoardActivity.class);
                 MenuActivity.this.startActivity(myIntent);
             }

         });
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Intent myIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });


        buttonEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(isUserLogged()){
                    boolean status = networkControl.checkDialogPresence(getApplicationContext(),MenuActivity.this);
                    if(status) {
                        Intent intent = new Intent(MenuActivity.this, EditorActivity.class);
                        startActivity(intent);
                    }
                } else  {
                    Toast t =Toast.makeText(MenuActivity.this,R.string.need_login,Toast.LENGTH_SHORT);
                    centerText(t.getView());
                    t.show();
                }
            }
        });


        buttonMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AudioUtils.playEffectNoLoop(MenuActivity.this,R.raw.button_sound);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(isUserLogged()) {
                    boolean status = networkControl.checkDialogPresence(getApplicationContext(),MenuActivity.this);
                    if(status) {
                        Intent intent = new Intent(MenuActivity.this, MultiplayerActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast t =Toast.makeText(MenuActivity.this,R.string.need_login,Toast.LENGTH_SHORT);
                    centerText(t.getView());
                    t.show();
                }
            }
        });

    buttonExit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finishAndRemoveTask();
        }
    });
    }

    private boolean isUserLogged() {
        return account != null;
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
        prefs.edit().putBoolean(FIRST_RUN_STATE, true).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        if(isUserLogged()){
            networkControl.checkDialogPresence(this,this);
        }

        if(prefs.getBoolean(FIRST_RUN_STATE,true)){
            prefs.edit().putBoolean(FIRST_RUN_STATE,false).commit();
        }
        if (prefs.getBoolean(FIRST_RUN_INSTALLATION_STATE, true)) {
            prefs.edit().putBoolean(FIRST_RUN_INSTALLATION_STATE, false).commit();
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
