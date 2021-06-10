package com.caserteam.arkanoid;

import java.io.Serializable;

public class Settings implements Serializable {


    public enum lang {
        it,
        en,
        es
    }

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
