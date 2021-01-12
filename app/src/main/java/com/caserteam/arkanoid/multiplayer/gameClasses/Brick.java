package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.caserteam.arkanoid.R;

import java.util.ArrayList;

public class Brick extends View {

    private Bitmap brick;

    private float x;
    private float y;

    private ArrayList<PointBrick> points;

    private int soundName;
    private int hitted; // se 0 non è stato hittato, 1 hittato una volta , 2 hittato due volte
    private int hit;
    private int skin;

    public Brick(Context context, float x, float y, int a, float base, float height) {
        super(context);
        this.x = x;
        this.y = y;
        hitted = 0;
        hit = 0;
        skin = a;
        skin(a);
        points = new ArrayList<PointBrick>();
        generatePoints(x, y, base, height);



    }
    private void generatePoints(float x, float y,float base, float height){

        for(float i=0; i<=base; i=i+(base/10)){
            if(i==0) points.add(new PointBrick((x+i+1), (y+1),"upLeftCorner"));
            else if(i==base) points.add(new PointBrick((x+i-1), (y+1),"upRightCorner"));
            else points.add(new PointBrick((x+i), (y),"up"));

        }

        for(float i=0; i<=base; i=i+(base/10)){
            if(i==0) points.add(new PointBrick((x+i+1), (y+height-1),"downLeftCorner"));
            else if(i==base) points.add(new PointBrick((x+i-1), (y+height-1),"downRightCorner"));
            else points.add(new PointBrick((x+i), (y+height),"down"));

        }

        for(float i=0; i<height; i=i+(height/6)){
            if(i!=0) points.add(new PointBrick((x), (y + i), "left"));

        }

        for(float i=0; i<height; i=i+(height/6)){
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
                hit=0;
                break;
            case 2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                soundName=2;
                hit=0;
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=3;
                hit=0;
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                soundName=4;
                hit=0;
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                soundName=5;
                hit=0;
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                soundName=6;
                hit=0;
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                soundName=7;
                hit=0;
                break;
            case 8:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                soundName=7;
                hit=0;
                break;
            case 9:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                soundName=7;
                hit=0;
                break;
            case 10:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                    soundName=2;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2);
                    soundName=2;
                }
                break;
            case 11:
                if (hitted==0){
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                soundName=3;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange2);
                    soundName=3;
                }
                break;
            case 12:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime);
                    soundName=4;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lime2);
                    soundName=4;
                }
                break;
            case 13:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                    soundName=5;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple2);
                    soundName=5;
                }
                break;
            case 14:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                    soundName=6;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red2);
                    soundName=6;
                }
                break;
            case 15:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                    soundName=7;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow2);
                    soundName=7;
                }
                break;
            case 16:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                    soundName=7;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua2);
                    soundName=7;
                }
                break;
            case 17:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                    soundName=7;
                    hit=1;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue2);
                    soundName=7;
                }
                break;
            case 18:
                if (hitted==0){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown);
                    soundName=7;
                    hit=2;
                }else if(hitted==1){
                    brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown2);
                    soundName=7;
                }else if(hitted==2){
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown3);
                soundName=7;
                }
                break;
            case 20:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_grey);
                hit=-1; // -1 OSTACOLO
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

    public int getHitted() {
        return hitted;
    }

    public void setSkinById(int a){ skin(a); }
    public void setSkin(Bitmap b){ brick=b; }

    public int getSkin(){
        return skin;
    }

    public void setHitted(int hitted) {
        this.hitted = hitted;
    }
    public void hittedOnce() {
        this.hitted++;
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

    public int getHit() {
        return hit;
    }
}

