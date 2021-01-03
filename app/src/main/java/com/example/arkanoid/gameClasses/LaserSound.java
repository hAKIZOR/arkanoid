package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import com.example.arkanoid.R;

public class LaserSound extends View {

    private Bitmap laser;
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    public LaserSound(Context context, float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
        xSpeed = 0;
        ySpeed = -20;
        skin(0);

    }

    private void skin(int a) {
        switch (a) {
            case 0:
                laser = BitmapFactory.decodeResource(getResources(), R.drawable.laser_sound2);
                laser = Bitmap.createScaledBitmap(laser, 20, 40, false);
                break;
        }
    }

    protected void move() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    //se il colpo è vicino ad un mattone
    private boolean isClosedBrick(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;
    }

    //se il colpo entra in collisione con un mattone
    protected boolean hitBrick(float xBrick, float yBrick) {
        if (isClosedBrick(xBrick, yBrick, getX(), getY())) {
            return true;
        } else return false;
    }

    public Bitmap getLaser() {
        return laser;
    }

    public void setLaser(Bitmap laser) {
        this.laser = laser;
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

}
