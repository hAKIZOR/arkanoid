package com.caserteam.arkanoid.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import com.caserteam.arkanoid.R;

import static com.caserteam.arkanoid.AppContractClass.*;

public class BrickEditor extends View {

    private Bitmap brick;
    private float x;
    private float y;

    public BrickEditor(Context context, float x, float y, Bitmap skin){
        super(context);
        this.x = x;
        this.y = y;
        brick = skin;
    }
    public BrickEditor(Context context, float x, float y, int a) {
        super(context);
        this.x = x;
        this.y = y;
        skin(a);

    }

    //assegna un'immagine al mattone in base al numero a
    public void skin(int a) {
        switch (a) {
            case 0:
                brick=null;//<-- SPAZIO VUOTO
                break;
            case 1:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_inv); //<-- MATTONE INVISIBILE
                break;
            case BRICK_GREEN:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                break;
            case BRICK_ORANGE:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                break;
            case BRICK_LIME:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                break;
            case BRICK_PURPLE:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                break;
            case BRICK_RED:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                break;
            case BRICK_YELLOW:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                break;
            case BRICK_AQUA:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                break;
            case BRICK_BLUE:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                break;
            case BRICK_GREEN2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2);
                break;
            case BRICK_ORANGE2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange2);
                break;
            case BRICK_LIME2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime2);
                break;
            case BRICK_PURPLE2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple2);
                break;
            case BRICK_RED2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red2);
                break;
            case BRICK_YELLOW2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow2);
                break;
            case BRICK_AQUA2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua2);
                break;
            case BRICK_BLUE2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue2);
                break;
            case BRICK_BROWN_OSTACLE:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown3);
                break;
            case BRICK_OSTACLE1:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_grey);
                break;
        }
    }
    public  int  getIdSkinById(Bitmap skin) {
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_inv).sameAs(skin)) { return 1; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_green).sameAs(skin)) { return BRICK_GREEN; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange).sameAs(skin)) { return BRICK_ORANGE; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime).sameAs(skin)) { return BRICK_LIME; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple).sameAs(skin)) { return BRICK_PURPLE; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_red).sameAs(skin)) { return BRICK_RED; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow).sameAs(skin)) { return BRICK_YELLOW; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua).sameAs(skin)) { return BRICK_AQUA; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue).sameAs(skin)) { return BRICK_BLUE; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2).sameAs(skin)) { return BRICK_GREEN2; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange2).sameAs(skin)) { return BRICK_ORANGE2; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime2).sameAs(skin)) { return BRICK_LIME2; }
        if (BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple2).sameAs(skin)) { return BRICK_PURPLE2; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_red2).sameAs(skin)) { return BRICK_RED2; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow2).sameAs(skin)) { return BRICK_YELLOW2; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua2).sameAs(skin)) { return BRICK_AQUA2; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue2).sameAs(skin)) { return BRICK_BLUE2; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown3).sameAs(skin)) { return BRICK_BROWN_OSTACLE; }
        if ( BitmapFactory.decodeResource(getResources(), R.drawable.brick_grey).sameAs(skin)) { return BRICK_OSTACLE1; }
        return 0;
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
    public void setBrick(Bitmap brick) {
        this.brick = brick;
    }


}