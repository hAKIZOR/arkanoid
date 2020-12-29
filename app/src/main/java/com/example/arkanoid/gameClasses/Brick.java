package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.example.arkanoid.R;

public class Brick extends View {

    private Bitmap brick;
    private float x;
    private float y;
    private int soundName;

    public Brick(Context context, float x, float y, int a) {
        super(context);
        this.x = x;
        this.y = y;
        skin(a);


    }

    //assegna un'immagine al mattone in base al numero a
    private void skin(int a) {
        switch (a) {
            case 0:
                brick=null;//<-- SPAZIO VUOTO
                break;
            case 1:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_inv); //<-- MATTONE INVISIBILE
                soundName=2;
                break;
            case 2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                soundName=2;
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=3;
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                soundName=4;
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                soundName=5;
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                soundName=6;
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                soundName=5;
                break;
            case 8:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                soundName=8;
                break;
            case 9:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                soundName=8;
                break;
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getBrick() {
        return brick;
    }

    public int getSoundName() {
        return soundName;
    }

}

