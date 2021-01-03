package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.example.arkanoid.R;

public class Paddle extends View {
    private float x;
    private float y;
    private int height;
    private int width;
    private static final int WIDTH = 200; //larghezza iniziale paddle
    private static final int HEIGHT = 40; //altezza iniziale paddle
    private static final int MIN_WIDTH = 100; //larghezza minima paddle
    private static final int MAX_WIDTH = 450; //larghezza massima paddle
    private Bitmap skin;

    public Paddle(Context context, float x, float y, int a) {
        super(context);
        this.x = x;
        this.y = y;
        this.height = HEIGHT;
        this.width = WIDTH;
        skin(a);
    }

    private void skin(int a) {
        switch (a) {
            case 0:
                skin = BitmapFactory.decodeResource(getResources(), R.drawable.paddle); //<-- SPAZIO VUOTO
                break;
            default:
                skin = BitmapFactory.decodeResource(getResources(), R.drawable.paddle); //<-- SPAZIO VUOTO
                break;
        }
    }

        public Bitmap getSkin () {
            return skin;
        }

        public void setSkin (Bitmap skin){
            this.skin = skin;
        }

        public static int getMinWidth () {
            return MIN_WIDTH;
        }

        public static int getMaxWidth () {
            return MAX_WIDTH;
        }


        public void setHeight ( int height){
            this.height = height;
        }


        public int getHeightp() {
        return height;
        }


        public int getWidthp() {
        return width;
        }

    public void setWidth (int width){
            this.width = width;
        }

        public void resetPaddle () {
            this.width = WIDTH;
        }

        public float getX () {
            return x;
        }

        public float getY () {
            return y;
        }

        public void setX ( float x){
            this.x = x;
        }

        public void setY ( float y){
            this.y = y;
        }
}

