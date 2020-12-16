package com.example.arkanoid;

import android.content.Context;

import com.example.arkanoid.Settings;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Set;
import  java.io.*;
public class FileManager { ;
    private static final String FILE_NAME = "config.txt";
    Settings settings;

    public FileManager(){ }
    public FileManager(Settings settings){
        this.settings = settings;
    }

    public Settings readFromConfigFile(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fIn= context.openFileInput(FILE_NAME);
        ObjectInputStream inputStream = new ObjectInputStream(fIn);
        Settings  settings = (Settings) inputStream.readObject();
        this.settings = settings;
        fIn.close();
        inputStream.close();
        return settings;
    }
    public void writeToConfigFile(Context context) throws IOException {
        FileOutputStream fOut = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream outputStream = new ObjectOutputStream(fOut);
        outputStream.writeObject(settings);
        fOut.close();
        outputStream.close();
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
