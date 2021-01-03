package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.content.res.AssetManager;
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

import com.example.arkanoid.R;

import androidx.core.content.res.ResourcesCompat;



public class GameViewLandscape extends Game{

    private Bitmap background;
    private Bitmap stretch;


    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;


    public GameViewLandscape(Context context, int lifes, int score){
        super(context, lifes, score);
        paint = new Paint();
        setSens(4); // <-- setta la sensitività dell'accellerometro
        setBackground(context);

        setSizeX(size.x);
        setSizeY(size.y);

        //crea una bitmap per la palla e la barra


        //setta posizione della palla, della barra e dei 4 bordi
        getBall().setX(size.x / 2);
        getBall().setY(size.y - 280);
        getPaddle().setX(size.x / 2);
        getPaddle().setY(size.y - 200);
        setBrickBase((size.y-40)/9);
        setBrickHeight((size.x-1200)/10);
        setUpBoard(0);
        setDownBoard(getSizeY());
        setLeftBoard(0);
        setRightBoard(getSizeX());
        setColumns(5);
        setRow(18);
        setW(getBrickHeight());
        setH(getBrickBase());
        setPaddW(150);
        setPaddH(20);

        //caricamento del livello con la generazione dei mattoni
        for(Level l: getLevels()) {
            if(l.getNumberLevel()==getNumberLevel()) {

                generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getW(),getH(),getPaddW(),getPaddH());
            }
        }
        this.setOnTouchListener(this);
    }

    // impostare lo sfondo
    private void setBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.background_score));
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    protected void onDraw(Canvas canvas) {
        // crea uno sfondo solo una volta
       /* if (stretch == null) {
            stretch = Bitmap.createScaledBitmap(background, size.x, size.y, false);
        }*/
        canvas.drawBitmap(background, 0, 0, paint);

        // disegna la pallina
        paint.setColor(Color.RED);
        canvas.drawBitmap(getBall().getSkin(), getBall().getX(), getBall().getY(), paint);

        // disegna la barra
        paint.setColor(Color.WHITE);
        r = new RectF(getPaddle().getX(), getPaddle().getY(), getPaddle().getX() + getPaddle().getWidthp(), getPaddle().getY() + getPaddle().getHeightp());
        canvas.drawBitmap(getPaddle().getSkin(), null, r, paint);

        // disegna mattoni
        paint.setColor(Color.GREEN);
        int molt=1;
        for (int i = 0; i < getBrickList().size(); i++) {

                Brick b = getBrickList().get(i);
                r = new RectF(b.getX(), b.getY(), b.getX() + 120, b.getY()+ 80);
                canvas.drawBitmap(b.getBrick(), null, r, paint);


        }

        //disegna powerUp
        paint.setColor(Color.GREEN);
        for(int j = 0; j < getPowerUps().size(); j++){
            PowerUp p = getPowerUps().get(j);
            r = new RectF(p.getX(), p.getY(), p.getX(), p.getY());
            canvas.drawBitmap(p.getPower(), p.getX(), p.getY(), paint);
            Log.e("pu", p.getX()+"");
        }

        //disegna powerUp
        paint.setColor(Color.GREEN);
        for(int j = 0; j < getLaserDropped().size(); j++){
            LaserSound p = getLaserDropped().get(j);
            r = new RectF(p.getX(), p.getY(), p.getX(), p.getY());
            canvas.drawBitmap(p.getLaser(), p.getX(), p.getY(), paint);
        }
        // disegna testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.play_prented);
        paint.setTypeface(typeface);
        canvas.drawText("LIFES : " + getLifes(), size.x-200, 80, paint);
        canvas.drawText("SCORE : "  + getScore(), size.x-200, 240, paint);

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
            getPaddle().setX(getPaddle().getX() - (event.values[1]*getSens()));

            if (getPaddle().getX() + event.values[1] > size.y + 520 + getPaddle().getWidthp()) {
                getPaddle().setX(size.y + 520 + getPaddle().getWidthp());
            } else if (getPaddle().getX() - event.values[1] <= 20) {
                getPaddle().setX(20);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
