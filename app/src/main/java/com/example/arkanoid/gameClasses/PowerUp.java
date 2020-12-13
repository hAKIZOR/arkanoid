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

    public PowerUp(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        xSpeed = 0;
        ySpeed = 10;
        skin(randomSkin());
    }


    private void skin(int a) {
        switch (a) {
            case 0:
                power = null;
                Log.e("pu","0");
                break;
            case 1:
                power = BitmapFactory.decodeResource(getResources(), R.drawable.hp_up); //<-- MATTONE INVISIBILE
                Log.e("pu","1");
                break;
        }
    }

    public int randomSkin(){
        Random random = new Random();
        int randomN = random.nextInt(100);
        if (randomN < 20){ // 20%
            return 1;
        }else return 0;

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
}
