package com.example.arkanoid.gameClasses;

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

import com.example.arkanoid.R;

import androidx.core.content.res.ResourcesCompat;



public class GameViewPortrait extends Game{

    private Bitmap background;
    private Bitmap redBall;
    private Bitmap stretch;
    private Bitmap paddle_p;


    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;
    private Game game;


    public GameViewPortrait(Context context,int lifes, int score){
        super(context, lifes, score);
        paint = new Paint();
        setBackground(context);
        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeX(size.x);
        setSizeY(size.y);

        //crea una bitmap per la palla e la barra
        redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        paddle_p = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);

        //setta posizione della palla e della barra
        getBall().setX(size.x / 2);
        getBall().setY(size.y - 280);
        getPaddle().setX(size.x / 2);
        getPaddle().setY(size.y - 200);
        setBrickBase((size.x-40)/9);
        setBrickHeight((size.y-1200)/10);

        setUpBoard(150);
        setDownBoard(size.y);
        setLeftBoard(0);
        setRightBoard(getSizeX() - 60);

        setColumns(10);
        setRow(9);
        setW(getBrickBase());
        setH(getBrickHeight());
        setPaddW(20);
        setPaddH(150);

        for(Level l: getLevels()) {
            if(l.getNumberLevel()==getNumberLevel()) {
                System.out.println(getNumberLevel()+"-->"+l.getNameLevel());

                generateBricks(context, getLevels().get(getNumberLevel()),getColumns(),getRow(),getW(),getH(),getPaddW(),getPaddH());
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
    }

    protected void onDraw(Canvas canvas) {
        // crea uno sfondo solo una volta
        /*if (stretch == null) {
            stretch = Bitmap.createScaledBitmap(background, size.x, size.y, false);
        }*/
        canvas.drawBitmap(background, 0, 0, paint);
        // disegna la pallina
        paint.setColor(Color.RED);
        canvas.drawBitmap(redBall, getBall().getX(), getBall().getY(), paint);

        // disegna la barra
        paint.setColor(Color.WHITE);
        r = new RectF(getPaddle().getX(), getPaddle().getY(), getPaddle().getX() + getPaddle().getWidth(), getPaddle().getY() + getPaddle().getHeight());
        canvas.drawBitmap(paddle_p, null, r, paint);

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
        }

        // disegna testo
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.play_prented);
        paint.setTypeface(typeface);
        canvas.drawText("LIFES :" + getLifes(), 200, 100, paint);
        canvas.drawText("SCORE :" + getScore(), 600, 100, paint);

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

            if (getPaddle().getX() + event.values[0] > size.x - getPaddle().getWidth()) {
                getPaddle().setX(size.x - getPaddle().getWidth());
            } else if (getPaddle().getX() - event.values[0] <= 20) {
                getPaddle().setX(20);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
