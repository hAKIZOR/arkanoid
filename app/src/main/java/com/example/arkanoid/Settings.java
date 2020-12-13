package com.example.arkanoid;

public class Settings {

    private boolean music;
    private boolean control;
    private String language;

    public Settings(boolean music, boolean control, String language) {
        this.music = music;
        this.control = control;
        this.language = language;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
