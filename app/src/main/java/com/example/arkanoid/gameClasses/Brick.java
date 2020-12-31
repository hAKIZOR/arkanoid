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
    private boolean hitted;
    private int skin;

    public Brick(Context context, float x, float y, int a) {
        super(context);
        this.x = x;
        this.y = y;
        hitted = false;
        skin = a;
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
                soundName=7;
                hitted=true;
                break;
            case 2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                soundName=1;
                hitted=true;
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=2;
                hitted=true;
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                soundName=3;
                hitted=true;
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                soundName=4;
                hitted=true;
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                soundName=5;
                hitted=true;
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                soundName=6;
                hitted=true;
                break;
            case 8:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                soundName=7;
                hitted=true;
                break;
            case 9:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                soundName=7;
                hitted=true;
                break;
            case 10:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                    soundName=1;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2);
                    soundName=1;
                }
                break;
            case 11:
                if (!hitted){
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=2;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange2);
                    soundName=2;
                }
                break;
            case 12:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                    soundName=3;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime2);
                    soundName=3;
                }
                break;
            case 13:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                    soundName=4;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple2);
                    soundName=4;
                }
                break;
            case 14:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                    soundName=5;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red2);
                    soundName=5;
                }
                break;
            case 15:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                    soundName=6;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow2);
                    soundName=6;
                }
                break;
            case 16:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                    soundName=7;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua2);
                    soundName=7;
                }
                break;
            case 17:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                    soundName=7;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue2);
                    soundName=7;
                }
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

    public boolean isHitted() {
        return hitted;
    }

    public void setSkinById(int a){ skin(a); }
    public void setSkin(Bitmap b){ brick=b; }

    public int getSkin(){
        return skin;
    }

    public void setHitted(boolean hitted) {
        this.hitted = hitted;
    }
}

