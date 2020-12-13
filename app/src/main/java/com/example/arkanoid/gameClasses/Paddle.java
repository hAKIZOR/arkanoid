package com.example.arkanoid.gameClasses;

public class Paddle {
    private float x;
    private float y;
    private int height;
    private int width;
    private static final int WIDTH = 200; //larghezza iniziale paddle
    private static final int HEIGHT = 40; //altezza iniziale paddle
    private static final int MIN_WIDTH = 100; //larghezza minima paddle
    private static final int MAX_WIDTH = 450; //larghezza massima paddle


    public Paddle(float x, float y) {
        this.x = x;
        this.y = y;
        this.height=HEIGHT;
        this.width=WIDTH;
    }

    public static int getMinWidth() {
        return MIN_WIDTH;
    }

    public static int getMaxWidth() {
        return MAX_WIDTH;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void resetPaddle() {
        this.width = WIDTH;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
