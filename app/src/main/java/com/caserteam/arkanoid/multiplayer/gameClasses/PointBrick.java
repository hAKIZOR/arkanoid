package com.caserteam.arkanoid.multiplayer.gameClasses;

public class PointBrick {
    private float x;
    private float y;
    String position; // potrà essere up,down,right,left

    public PointBrick(float x, float y, String position) {
        this.x = x;
        this.y = y;
        this.position = position;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
