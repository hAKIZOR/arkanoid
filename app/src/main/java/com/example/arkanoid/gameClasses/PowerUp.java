package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.example.arkanoid.R;

import java.util.Random;

public class PowerUp extends View {
    private Bitmap power;
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;
    private static final int max = 1;
    private static final int min = 0;
    private int typePower;

    public PowerUp(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        xSpeed = 0;
        ySpeed = 15;

        skin(randomSkin());
    }


    private void skin(int a) {
        switch (a) {
            case 0:
                power = null;
                break;
            case 1:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.hp_up); //<-- hp+1
                break;
            case 2:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.hp_down); //<-- hp-1
                break;
            case 3:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.paddle_up); //<-- paddle aumenta larghezza
                break;
            case 4:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.paddle_down); //<-- paddle aumenta larghezza
                break;
            case 5:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.hands_piano); //<-- power elimina 3 brick con dito
                break;
        }
    }

    public int randomSkin(){
        Random random = new Random();
        int randomN = random.nextInt(100);
        if (randomN < 4){ // 4%
            this.typePower=1;
            return typePower;
        }else if (randomN < 8){ // 4%
            this.typePower=2;
            return typePower;
        }else if (randomN < 12){ // 4%
            this.typePower=3;
            return typePower;
        }else if (randomN < 16){ // 4%
            this.typePower=4;
            return typePower;
        }else if (randomN < 18){ // 2%
            this.typePower=5;
            return typePower;
        }else { // 82%
            this.typePower=0;
            return typePower;}

    }

    protected void move() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public Bitmap getPower() {
        return power;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public int getTypePower() {
        return typePower;
    }
}
