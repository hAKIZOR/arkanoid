package com.caserteam.arkanoid.gameClasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;

import com.caserteam.arkanoid.R;

import java.io.IOException;


public class GameViewPortrait extends Game {

    private Bitmap background;
    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;
    private int screen_height;
    private int screen_width;



    public GameViewPortrait(Context context,int lifes, int score){
        super(context, lifes, score);
        paint = new Paint();
        setBackground(context);

        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeX(size.x);
        setSizeY(size.y);


        //setta posizione della palla e della barra
        getBall().setX(size.x / 2);
        getBall().setY(size.y - 280);
        getPaddle().setX(size.x / 2);
        getPaddle().setY(size.y - 200);

        //setto i bordi
        setUpBoard(150);
        setDownBoard(size.y);
        setLeftBoard(0);
        setRightBoard(getSizeX());

        //setto colonne e righe dei mattoni
        setColumns(9);
        setRow(10);

        //setto altezza e base del mattone
        setBrickBase((size.x-40)/getColumns());
        setBrickHeight((size.y-1200)/getRow());

        //setto il padding del campo di gioco
        setPaddingLeftGame(20);
        setPaddingTopGame(150);

        for(Level l: getLevels()) {
            if(l.getNumberLevel()==getNumberLevel()) {
                generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());
            }
        }

        this.setOnTouchListener(this);
    }

    // impostare lo sfondo
    private void setBackground(Context context) {
        int navBarHeight = 0;

        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        size = new Point();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        boolean hasPhysicalHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (android.os.Build.VERSION.SDK_INT >= 17){
            display.getRealSize(size);
            screen_width = size.x;
            screen_height = size.y;
        } else if (hasPhysicalHomeKey){
            screen_height = dm.heightPixels;
        } else {
            screen_height = dm.heightPixels + navBarHeight;
        }

        background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score));
        paint.setColor(Color.RED);

    }

    protected void onDraw(Canvas canvas) {

        // crea uno sfondo solo una volta
        Rect dest = new Rect(0, 0, screen_width, screen_height);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(background, null, dest, paint);

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

        //disegna powerUp
        paint.setColor(Color.GREEN);
        for(int j = 0; j < getPowerUps().size(); j++){
            PowerUp p = getPowerUps().get(j);
            r = new RectF(p.getX(), p.getY(), p.getX(), p.getY());
            canvas.drawBitmap(p.getPower(), p.getX(), p.getY(), paint);
        }

        //disegna powerUp Laser
        paint.setColor(Color.GREEN);
        for(int j = 0; j < getLaserDropped().size(); j++){
            LaserSound p = getLaserDropped().get(j);
            r = new RectF(p.getX(), p.getY(), p.getX(), p.getY());
            canvas.drawBitmap(p.getLaser(), p.getX(), p.getY(), paint);
        }

        // disegna testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.font);
        paint.setTypeface(typeface);
        canvas.drawText("HP:" + getLifes(), 1, 100, paint);
        canvas.drawText("PT:" + getScore(), 200, 100, paint);
        canvas.drawText("LSR:" + getLaserSoundRemaining(), 550, 100, paint);
        canvas.drawText("PIANO:" + getHandsPianoRemaining(), 800, 100, paint);
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
