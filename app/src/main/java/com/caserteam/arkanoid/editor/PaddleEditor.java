package com.caserteam.arkanoid.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import com.caserteam.arkanoid.R;

public class PaddleEditor extends View {

    public static final int PADDLE_SKIN1 = 1;
    public static final int PADDLE_SKIN2 = 2;
    public static final int PADDLE_SKIN3 = 3;
    private Bitmap paddleSkin;
    private float x;
    private float y;
    private int height;
    private int width;
    private static final int WIDTH = 200; //larghezza iniziale paddle
    private static final int HEIGHT = 40; //altezza iniziale paddle
    private static final int MIN_WIDTH = 100; //larghezza minima paddle
    private static final int MAX_WIDTH = 450; //larghezza massima paddle


    public PaddleEditor(Context context, float x, float y, int typeSkin) {
        super(context);
        this.x = x;
        this.y = y;
        this.height=HEIGHT;
        this.width=WIDTH;
        skin(typeSkin);
    }
    public void skin(int typeSkin) {
        switch (typeSkin) {
            case PADDLE_SKIN1:
                paddleSkin = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
                break;
        }
    }

    public Bitmap getPaddleSkin() {
        return paddleSkin;
    }

    public static int getMinWidth() {
        return MIN_WIDTH;
    }

    public static int getMaxWidth() {
        return MAX_WIDTH;
    }

    public int getPaddleHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPaddleWidth() {
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
