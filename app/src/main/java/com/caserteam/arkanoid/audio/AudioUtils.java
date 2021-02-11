package com.caserteam.arkanoid.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.caserteam.arkanoid.IOUtils;
import com.caserteam.arkanoid.Settings;

import java.io.IOException;

import static  com.caserteam.arkanoid.AppContractClass.*;

public class AudioUtils {

    public AudioUtils(){}

    public static void playEffectNoLoop(Context context, int rawSong) throws IOException, ClassNotFoundException {
        if(isEnabledGeneralAudioSettings(context)){
            MediaPlayer song = MediaPlayer.create(context,rawSong);
            song.setLooping(false);
            song.start();
        }
    }
    public static void enableTrackSoundService(Context context,int rawSong)  {
            Intent serviceSound = new Intent(context, BackgroundSoundService.class);
            serviceSound.putExtra(SOUND_EXTRA,rawSong);
            context.startService(serviceSound);
    }

    public static  void disableTrackSoundService(Activity activity) {
            activity.stopService(new Intent(activity,BackgroundSoundService.class));
    }

    public static void playBackgroundSound(Context context,int rawSong) throws IOException, ClassNotFoundException {
        if(isEnabledGeneralAudioSettings(context)){
            Intent serviceSound = new Intent(context, BackgroundSoundService.class);
            serviceSound.putExtra(SOUND_EXTRA,rawSong);
            context.startService(serviceSound);
        }
    }

    public static  void stopBackgroundSound(Activity activity) throws IOException, ClassNotFoundException {
        if(isEnabledGeneralAudioSettings(activity)) {
            activity.stopService(new Intent(activity,BackgroundSoundService.class));
        }
    }

    private static boolean isEnabledGeneralAudioSettings(Context context) throws IOException, ClassNotFoundException {
        boolean result;
        Settings settings =  IOUtils.readObjectFromFile(context,FILE_NAME);

        if(settings.getAudio() == AUDIO_ON){
            result = true;
        } else {
            result = false;
        }

        return result;
    }

}
