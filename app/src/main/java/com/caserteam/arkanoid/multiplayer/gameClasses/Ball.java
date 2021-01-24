package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.caserteam.arkanoid.R;

public class Ball extends View {


    private double sizeball;
    private double halfball;
    protected float xSpeed;
    protected float ySpeed;
    private float x;
    private float y;
    private Bitmap skin;
    private float sizeX;
    private float sizeY;

    public Ball (Context context, float x, float y, int a,float sizeX,float sizeY) {
        super(context);
        this.x = x;
        this.y = y;
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.sizeball= (0.017*(sizeX*sizeY))/1000;
        this.halfball= sizeball/2;
        createSpeed();
        skin(a);
    }

    private void skin(int a) {
        switch (a) {
            case 0:
                skin = BitmapFactory.decodeResource(getResources(), R.drawable.redball); //<-- SPAZIO VUOTO
                skin = Bitmap.createScaledBitmap(skin, (int)sizeball, (int)sizeball, false);
                break;
        }
    }

    // crea una palla di velocità casuale
    // crear una bola de velocidad aleatoria
    protected void createSpeed() {
        int maxX = 13;
        int minX = 7;
        int maxY = -17;
        int minY = -23;
        int rangeX = maxX - minX + 1;
        int rangeY = maxY - minY + 1;

        xSpeed = (int) (Math.random() * rangeX) + minX;
        ySpeed = (int) (Math.random() * rangeY) + minY;
    }

    //aumentare la velocità in base al livello
    //aumentar la velocidad según el nivel
    protected void increaseSpeed(int level) {
        xSpeed = xSpeed + (1 * level);
        ySpeed = ySpeed - (1 * level);
    }


    //cambia direzione a seconda del muro che ha toccato e della velocità
    //cambia de dirección según la pared que ha golpeado y la velocidad
    protected void changeDirection(String wall) {
        if (xSpeed > 0 && ySpeed < 0 && wall.equals("right")) {
            reversexSpeed();
        } else if (xSpeed >= 0 && ySpeed < 0 && wall.equals("up")) {
            reverseySpeed();
        } else if (xSpeed <= 0 && ySpeed < 0 && wall.equals("up")) {
            reverseySpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && wall.equals("left")) {
            reversexSpeed();
        } else if (xSpeed < 0 && ySpeed > 0 && wall.equals("left")) {
            reversexSpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && wall.equals("right")) {
            reversexSpeed();
        }
    }

    //cambia direzione a seconda della parte di paddle che ha toccato
    //cambia de dirección según la pared que ha golpeado y la velocidad
    protected void changeDirectionPaddle(Paddle paddle) {

        if (this.x>=paddle.getX() && this.x<paddle.getX()+paddle.getWidthp()/7) {
            setSpeed(-13, -14);
        } else if (this.x>=paddle.getX()+paddle.getWidthp()/7 && this.x<paddle.getX()+(paddle.getWidthp()*2)/7) {
            setSpeed(-10, -14);
        } else if (this.x>=paddle.getX()+(paddle.getWidthp()*2)/7 && this.x<paddle.getX()+(paddle.getWidthp()*3)/7) {
            setSpeed(-7, -14);
        } else if (this.x>=paddle.getX()+(paddle.getWidthp()*3)/7 && this.x<paddle.getX()+(paddle.getWidthp()*4)/7) {
            setSpeed(0, -20);
        } else if (this.x>=paddle.getX()+(paddle.getWidthp()*4)/7 && this.x<paddle.getX()+(paddle.getWidthp()*5)/7) {
            setSpeed(7, -14);
        } else if (this.x>=paddle.getX()+(paddle.getWidthp()*5)/7 && this.x<paddle.getX()+(paddle.getWidthp()*6)/7) {
            setSpeed(10,-14);
        } else if (this.x>=paddle.getX()+(paddle.getWidthp()*6)/7 && this.x<paddle.getX()+paddle.getWidthp()) {
            setSpeed(13, -14);
        }
    }

    //cambia direzione a seconda del ostacolo che ha toccato e della velocità
    protected void changeDirectionBrick(String wall) {
        if (wall.equals("left")) {
            Log.e("direzione","LEFT");
            reversexSpeed();
        }  else if (wall.equals("down")) {
            Log.e("direzione","DOWN");
            reverseySpeed();
        } else if (wall.equals("right")) {
            Log.e("direzione","RIGHT");
            reversexSpeed();
        }else if (wall.equals("up")) {
            Log.e("direzione","UP");
            reverseySpeed();
        }else if (xSpeed >= 0 && ySpeed > 0 &&  wall.equals("upLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_UP");
            reversexSpeed();
            reverseySpeed();
        }else if (xSpeed >= 0 && ySpeed < 0 &&  wall.equals("upLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_UP");
            reversexSpeed();
        }else if (xSpeed <= 0 && ySpeed > 0 && wall.equals("upLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_UP");
            reverseySpeed();
        }else if (xSpeed <= 0 && ySpeed < 0 && wall.equals("upRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_UP");
            reversexSpeed();
        }else if (xSpeed <= 0 && ySpeed > 0 && wall.equals("upRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_UP");
            reverseySpeed();
            reversexSpeed();
        }else if (xSpeed >= 0 && ySpeed > 0 && wall.equals("upRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_UP");
            reverseySpeed();
        }else if (xSpeed <= 0 && ySpeed < 0 && wall.equals("downLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_DOWN");
            reverseySpeed();
        }else if (xSpeed >= 0 && ySpeed < 0 && wall.equals("downLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_DOWN");
            reversexSpeed();
            reverseySpeed();
        }else if (xSpeed >= 0 && ySpeed > 0 && wall.equals("downLeftCorner")) {
            Log.e("direzione","CORNER_LEFT_DOWN");
            reversexSpeed();
        }else if (xSpeed >= 0 && ySpeed < 0 && wall.equals("downRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_DOWN");
            reverseySpeed();
        }else if (xSpeed <= 0 && ySpeed < 0 &&wall.equals("downRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_DOWN");
            reverseySpeed();
            reversexSpeed();
        }else if (xSpeed <= 0 && ySpeed > 0 &&wall.equals("downRightCorner")) {
            Log.e("direzione","CORNER_RIGHT_DOWN");
            reversexSpeed();
        }
    }

    //scopri se la palla è vicina a un mattone
    //averigua si la pelota está cerca de un ladrillo
    private boolean isClosedBrick(float brickX, float brickY) {
        double d = Math.sqrt(Math.pow(brickX - this.x, 2) + Math.pow(brickY - this.y, 2));
        return d < halfball;
    }

    //se la palla entra in collisione con un mattone, cambia direzione
    //si la bola choca con un ladrillo, cambia de dirección
    protected boolean hitBrick(Brick b) {
        boolean result=false;

        for(int i=0; i<b.getPoints().size(); i++){
            if (isClosedBrick(b.getPoints().get(i).getX(), b.getPoints().get(i).getY())) {
                changeDirectionBrick(b.checkPointSide(b.getPoints().get(i).getX(),b.getPoints().get(i).getY()));
                result = true;
                break;
            }
        }

        return result;
    }

    // si muove alla velocità specificata
    // se mueve a la velocidad especificada
    protected void move() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public Bitmap getSkin() {
        return skin;
    }

    public void setSkin(int a) {
        skin(a);
    }

    public void reversexSpeed() {
        xSpeed = -xSpeed;
    }

    public void reverseySpeed() {
        ySpeed = -ySpeed;
    }

    public float getX() {
        return x;
    }

    public float getY() { return y; }

    public void setX(float x) {
        this.x = (float) (x-halfball);
    }

    public void setY(float y) {
        this.y = (float) (y+halfball);
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public  double getSIZEBALL() {
        return sizeball;
    }

    public  double getHALFBALL() {
        return halfball;
    }

    public void setSpeed( float xSpeed, float ySpeed){
        this.xSpeed=xSpeed;
        this.ySpeed=ySpeed;
    }
}
