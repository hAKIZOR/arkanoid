package com.caserteam.arkanoid.audio;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.caserteam.arkanoid.R;
import static com.caserteam.arkanoid.AppContractClass.*;
public class BackgroundSoundService extends Service {
    private static final String TAG = "ServiceAudio";
    MediaPlayer player;
    int audioRaw;
    public IBinder onBind(Intent arg) {


        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
            audioRaw = intent.getIntExtra(SOUND_EXTRA, -1);
            player = MediaPlayer.create(this, audioRaw);
            player.setLooping(true); // Set looping
            player.setVolume(100,100);
            player.start();

        return super.onStartCommand(intent, flags,startId);
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
