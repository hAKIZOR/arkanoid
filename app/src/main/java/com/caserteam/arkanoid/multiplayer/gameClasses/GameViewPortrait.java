package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.caserteam.arkanoid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;


public class GameViewPortrait extends Game {

    private Bitmap background;
    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;




    public GameViewPortrait(Context context, int lifes, int score, String playerRole, DatabaseReference roomRef){
        super(context, lifes, score, playerRole, roomRef);
        paint = new Paint();
        setBackground(context);

        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeX(1080);
        setSizeY(1920);


        //setta posizione della palla e della barra
        getBall().setX(getSizeX() / 2);
        getBall().setY(getSizeY() - 280);

        fieldxBall="xBall";
        fieldyBall="yBall";
        fieldxSpeedBall="xSpeedBall";
        fieldySpeedBall="ySpeedBall";
        fieldStarted="started";

        paddle.setWidth((int) (getSizeX()*0.1));
        paddle2.setWidth((int) (getSizeX()*0.1));

        if(playerRole.equals("player1")) {
            paddle.setX((size.x/2) - (paddle.getWidthp()));
            paddle.setY((float) (size.y - (size.y / 50)));
            Log.d("Game", "x---->" + String.valueOf(size.x - ((size.x/4))));
            paddle2.setX(size.x/2);
            paddle2.setY((float) (size.y - (size.y / 50)));

            minPositionPaddle = 0;
            maxPositionPaddle = size.x/2;

            fieldXPaddleThisDevice = "xPaddlePlayer1";
            fieldXPaddleOtherDevice = "xPaddlePlayer2";


        }else {
            paddle.setX((size.x/2));
            paddle.setY((float) (size.y - (size.y / 50)));
            paddle2.setX((size.x/2) - (paddle.getWidthp()));
            paddle2.setY((float) (size.y - (size.y / 50)));

            minPositionPaddle = size.x/2;
            maxPositionPaddle = size.x;

            fieldXPaddleThisDevice = "xPaddlePlayer2";
            fieldXPaddleOtherDevice = "xPaddlePlayer1";


        }



        //setto i bordi
        setUpBoard(((size.y-getSizeY())/2)+150);
        setDownBoard(getSizeY());
        setLeftBoard((size.x-getSizeX())/2);
        setRightBoard(size.x - (size.x-getSizeX())/2);


        //setto colonne e righe dei mattoni
        setColumns(9);
        setRow(10);

        //setto altezza e base del mattone
        setBrickBase((getSizeX()-40)/getColumns());
        setBrickHeight((getSizeY()-1200)/getRow());

        //setto il padding del campo di gioco
        setPaddingLeftGame(((size.x-getSizeX())/2)+22); //20
        setPaddingTopGame(((size.y-getSizeY())/2)+150);

        for(Level l: getLevels()) {
            if(l.getNumberLevel()==getNumberLevel()) {
                generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());
            }
        }

        this.setOnTouchListener(this);
    }

    // impostare lo sfondo
    private void setBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score));
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        paint.setColor(Color.RED);

    }

    protected void onDraw(Canvas canvas) {
        // crea uno sfondo solo una volta
        //canvas.drawBitmap(background, 0, 0, paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(getLeftBoard(),getUpBoard(),getLeftBoard(),getDownBoard(),paint);
        canvas.drawLine(getRightBoard(),getUpBoard(),getRightBoard(),getDownBoard(),paint);
        canvas.drawLine(getLeftBoard(),getUpBoard(),getRightBoard(),getUpBoard(),paint);
        canvas.drawLine(getLeftBoard(),getDownBoard(),getRightBoard(),getDownBoard(),paint);
        // disegna la pallina
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawBitmap(getBall().getSkin(), getBall().getX(), getBall().getY(), paint);

        // disegna la barra
        paint.setColor(Color.WHITE);
        r = new RectF(getPaddle().getX(), getPaddle().getY(), getPaddle().getX() + getPaddle().getWidthp(), getPaddle().getY() + getPaddle().getHeightp());
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), null, r, paint);


        // disegna mattoni
        paint.setColor(Color.GREEN);
        int molt=1;
        for (int i = 0; i < getBrickList().size(); i++) {

                Brick b = getBrickList().get(i);
                r = new RectF(b.getX(), b.getY(), b.getX() + getBrickBase(), b.getY()+ getBrickHeight());
                canvas.drawBitmap(b.getBrick(), null, r, paint);
        }


        // disegna testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.font);
        paint.setTypeface(typeface);
        canvas.drawText("HP:" + getLifes(), 1, 100, paint);
        canvas.drawText("PT:" + getScore(), 200, 100, paint);
        canvas.drawText("Level: " + getNumberLevel(), (float) (size.x-(size.x*(0.60))), (float) (size.y), paint);

        // in caso di sconfitta stampa "GameOver"
        if (isGameOver()) {
            if(levelCompleted()){
                if(getNumberLevel()<=15) {
                    try {
                        gameListener.onWinLevel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else gameListener.onWinGame();

            } else {
                try {
                    gameListener.onGameOver();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //cambiare accelerometro
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getPaddle().setX(getPaddle().getX() - (event.values[0] * getSens()));

            if (getPaddle().getX() + event.values[0] > size.x - getPaddle().getWidthp()) {
                getPaddle().setX(size.x - getPaddle().getWidthp());
            } else if (getPaddle().getX() - event.values[0] <= 20) {
                getPaddle().setX(20);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



}
