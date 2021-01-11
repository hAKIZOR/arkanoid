package com.caserteam.arkanoid;

import java.io.Serializable;

public class Settings implements Serializable {
    public static final String FILE_NAME ="config.txt";
    public static final String LANGUAGE_IT = "it";
    public static final String LANGUAGE_ESP = "es";
    public static final String LANGUAGE_ENG = "en";

    public enum lang {
        it,
        en,
        es
    }

    public static final int SYSTEM_CONTROL_SENSOR = 1;
    public static final int SYSTEM_CONTROL_SCROLL = 0;


    public static final int AUDIO_ON = 1;
    public static final int AUDIO_OFF = 0;


    private int controlMode;
    private int language;
    private int audio;

    public Settings(int controlMode,int language,int audio) {
        this.controlMode = controlMode;
        this.language = language;
        this.audio = audio;
    }

    public int getControlMode() {
        return controlMode;
    }

    public void setControlMode(int controlMode) {
        this.controlMode = controlMode;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "controlMode=" + String.valueOf(controlMode) +
                ", language=" + language +
                ", audio=" + String.valueOf(audio) +
                '}';
    }
}
