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
import android.view.Display;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;

import com.caserteam.arkanoid.R;
import com.google.firebase.database.DatabaseReference;


public class GameViewPortrait extends Game {

    private Bitmap background;
    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;


    public GameViewPortrait(Context context, int lifes, int score,DatabaseReference room,String p1, String p2){
        super(context, lifes,score,room,p1,p2);
        paint = new Paint();
        setBackground(context);
        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeX(size.x);
        setSizeY(size.y);



        //setta posizione della palla e della barra
        if(p1.equals("xPaddlePlayer1")){
            getBall().setX(size.x / 2);
            getBall().setY((float) ((size.y*0.10)+getPaddle().getHeightp())-30);
        }else{
            getBall().setX(size.x / 2);
            getBall().setY((float) (size.y - (size.y*0.10)+getPaddle().getHeightp()+30));
        }
        getPaddle().setX(size.x / 2);
        getPaddle().setY((float) (size.y - (size.y*0.10)+getPaddle().getHeightp()));
        getPaddle2().setX(size.x / 2);
        getPaddle2().setY((float) (size.y*0.10)+getPaddle2().getHeightp());

        //setto i bordi
        setUpBoard(150);
        setDownBoard(size.y);
        setLeftBoard(0);
        setRightBoard(getSizeX() - 60);

        //setto colonne e righe dei mattoni
        setColumns(9);
        setRow(3);

        //setto altezza e base del mattone
        setBrickBase((size.x-40)/getColumns());
        setBrickHeight((size.y/9)/getRow());

        //setto il padding del campo di gioco
        setPaddingLeftGame(20);
        setPaddingTopGame((size.y/2)-(getBrickHeight()*3/2));


        generateBricks(context, getLevel(),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());

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
        canvas.drawBitmap(background, 0, 0, paint);
        // disegna la pallina
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawBitmap(getBall().getSkin(), getBall().getX(), getBall().getY(), paint);

        // disegna la barra
        paint.setColor(Color.WHITE);
        r = new RectF(getPaddle().getX(), getPaddle().getY(), getPaddle().getX() + getPaddle().getWidthp(), getPaddle().getY() + getPaddle().getHeightp());
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), null, r, paint);
        // disegna la barra2
        paint.setColor(Color.WHITE);
        r = new RectF(getPaddle2().getX(), getPaddle2().getY(), getPaddle2().getX() + getPaddle2().getWidthp(), getPaddle2().getY() + getPaddle2().getHeightp());
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

        // in caso di sconfitta stampa "GameOver"
        if (isGameOver()) {
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game over!", size.x / 4, size.y / 2, paint);
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
