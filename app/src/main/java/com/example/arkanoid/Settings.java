package com.example.arkanoid;

import java.io.Serializable;

public class Settings implements Serializable {
    private int controlMode;
    private String language;
    private int audio;

    public Settings(int controlMode,String language,int audio) {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
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
                ", language='" + language + '\'' +
                ", audio=" + String.valueOf(audio) +
                '}';
    }
}
