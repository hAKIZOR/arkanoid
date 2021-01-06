package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.example.arkanoid.R;

import java.util.ArrayList;

public class Brick extends View {

    private Bitmap brick;

    private float x;
    private float y;

    private ArrayList<PointBrick> points;

    private int soundName;
    private boolean hitted;
    private int skin;

    public Brick(Context context, float x, float y, int a, float base, float height) {
        super(context);
        this.x = x;
        this.y = y;
        hitted = false;
        skin = a;
        skin(a);
        points = new ArrayList<PointBrick>();
        generatePoints(x, y, base, height);



    }
    private void generatePoints(float x, float y,float base, float height){

        for(float i=0; i<=base; i=i+(base/8)){
            if(i==0) points.add(new PointBrick((x+i), (y),"upLeftCorner"));
            else if(i==base) points.add(new PointBrick((x+i), (y),"upRightCorner"));
            else points.add(new PointBrick((x+i), (y),"up"));

        }

        for(float i=0; i<=base; i=i+(base/8)){
            if(i==0) points.add(new PointBrick((x+i), (y+height),"downLeftCorner"));
            else if(i==base) points.add(new PointBrick((x+i), (y+height),"downRightCorner"));
            else points.add(new PointBrick((x+i), (y+height),"down"));

        }

        for(float i=0; i<height; i=i+(height/4)){
            if(i!=0) points.add(new PointBrick((x), (y + i), "left"));

        }

        for(float i=0; i<height; i=i+(height/4)){
            if(i!=0)points.add(new PointBrick((x+base), (y+i),"right"));

        }


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
                soundName=2;
                hitted=true;
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=3;
                hitted=true;
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                soundName=4;
                hitted=true;
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                soundName=5;
                hitted=true;
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                soundName=6;
                hitted=true;
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                soundName=7;
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
                    soundName=2;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2);
                    soundName=2;
                }
                break;
            case 11:
                if (!hitted){
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=3;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange2);
                    soundName=3;
                }
                break;
            case 12:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                    soundName=4;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime2);
                    soundName=4;
                }
                break;
            case 13:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                    soundName=5;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple2);
                    soundName=5;
                }
                break;
            case 14:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                    soundName=6;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red2);
                    soundName=6;
                }
                break;
            case 15:
                if (!hitted){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                    soundName=7;
                }else {
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow2);
                    soundName=7;
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
            case 20:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_grey);
                hitted=false;
                soundName=7;
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

    public String checkPointSide(float x, float y){
        String side = null;

        for(int i=0; i<points.size(); i++){
            if(points.get(i).getX()==x && points.get(i).getY()==y){
                side = points.get(i).position;
            }
        }

        return side;
    }

    public ArrayList<PointBrick> getPoints() {
        return points;
    }
}

