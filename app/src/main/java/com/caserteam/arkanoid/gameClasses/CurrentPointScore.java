package com.caserteam.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.caserteam.arkanoid.R;

import java.util.Random;

public class CurrentPointScore extends View {
    private Bitmap scoreImage;
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    public CurrentPointScore(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        xSpeed = 0;
        ySpeed = 15;
        scoreImage = BitmapFactory.decodeResource(getResources(), R.drawable.plus_one_brick);
    }


    protected void move() {
        x = x + xSpeed;
        y = y - ySpeed;
    }

    public Bitmap getScoreImage() {
        return scoreImage;
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

