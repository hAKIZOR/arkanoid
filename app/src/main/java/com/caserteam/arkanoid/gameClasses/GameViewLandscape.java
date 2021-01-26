package com.caserteam.arkanoid.gameClasses;

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

import androidx.core.content.res.ResourcesCompat;

import com.caserteam.arkanoid.R;

import java.io.IOException;


public class GameViewLandscape extends Game {

    private Bitmap background;


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

        //setta posizione della palla, della barra e dei 4 bordi

        //setta posizione della palla e della barra
        getBall().setX(size.x / 2);
        getBall().setY(size.y - 250);
        getPaddle().setX(size.x / 2);
        getPaddle().setY(size.y - 180);

        //setto i bordi
        setUpBoard(0);
        setDownBoard(size.y);
        setLeftBoard ((int) (size.x * (0.13)));
        setRightBoard(size.x);

        //setto colonne e righe dei mattoni
        setColumns(15);
        setRow(6);

        //setto altezza e base del mattone
        setBrickBase((float) ((size.y-(size.y/1.4))/getRow()));
        setBrickHeight((size.x/getColumns()) - 30);

        //setto il padding del campo di gioco
        setPaddingTopGame(0);
        setPaddingLeftGame((float) (size.x * (0.20)));

        //caricamento del livello con la generazione dei mattoni

        for(Level l: getLevels()) {
            if(l.getNumberLevel()==getNumberLevel()) {
                generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickHeight(),getBrickBase(),getPaddingLeftGame(),getPaddingTopGame());
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
        paint.setColor(Color.BLACK);
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
                r = new RectF(b.getX(), b.getY(), b.getX() + getBrickHeight(), b.getY()+ getBrickBase());
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



        Paint lineProperty = new Paint();
        lineProperty.setColor(Color.BLACK);
        lineProperty.setStrokeWidth((float) (size.x-(size.x*(0.75))));
        canvas.drawLine(0,0,0,size.y,lineProperty);

        // disegna testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(35);
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.font);
        paint.setTypeface(typeface);
        canvas.drawText("HP:" + getLifes(), (float) (size.x-(size.x*(0.98))), 80, paint);
        canvas.drawText("PT:" + getScore(), (float) (size.x-(size.x*(0.98))), 150, paint);
        canvas.drawText("LSR:" + getLaserSoundRemaining(), (float) (size.x-(size.x*(0.98))), 220, paint);
        canvas.drawText("PIANO:" + getHandsPianoRemaining(), (float) (size.x-(size.x*(0.98))), 290, paint);
        canvas.drawText("LEVEL:" + getNumberLevel(), (float) (size.x-(size.x*(0.98))), 370, paint);

        // in caso di sconfitta stampa "GameOver"
        if (isGameOver()) {
            if(levelCompleted()){
                gameListener.onWinGame();
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

    /*@Override
    //imposta il gioco per iniziare
    public void resetLevel() {
        getBall().setX(getSizeX() / 2);
        getBall().setY(getSizeY() - 280);
        getBall().createSpeed();
        getPowerUps().clear();
        getLaserDropped().clear();
        setBrickList(new ArrayList<BrickGameSearched>());

        generateBricks(getContext(), getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickHeight(),getBrickBase(),getPaddingLeftGame(),getPaddingTopGame());
    }*/

}
