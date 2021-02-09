package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
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
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.caserteam.arkanoid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import static com.caserteam.arkanoid.AppContractClass.*;


public class GameViewPortrait extends Game {

    private Bitmap background;
    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;
    private int screen_width;
    private int screen_height;


    public GameViewPortrait(Context context, int lifes, int score, String playerRole, DatabaseReference roomRef){
        super(context, lifes, score, playerRole, roomRef);
        paint = new Paint();
        setBackground(context);

        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeX(1080);
        setSizeY(1920);

        //setto i bordi
        setUpBoard(((screen_height - getSizeY())/2));
        setDownBoard(getSizeY() + getUpBoard());
        setLeftBoard((screen_width - getSizeX())/2);
        setRightBoard(screen_width - getLeftBoard());

        fieldxBall="xBall";
        fieldyBall="yBall";
        fieldxSpeedBall="xSpeedBall";
        fieldySpeedBall="ySpeedBall";
        fieldStarted="started";

        paddle.setWidth((int) (getSizeX()*0.1));
        paddle2.setWidth((int) (getSizeX()*0.1));

        //setta posizione della palla




        if(playerRole.equals(ROLE_PLAYER1)) {
            paddle.setX((screen_width/2) - (paddle.getWidthp()));
            paddle.setY((float) (getDownBoard() - (getSizeY()/20)));
            Log.d("Game", "x---->" + String.valueOf(screen_width - ((screen_width/4))));
            paddle2.setX(screen_width/2);
            paddle2.setY((float) (getDownBoard() - (getSizeY()/20)));

            minPositionPaddle = getLeftBoard();
            maxPositionPaddle = screen_width/2;


            fieldXPaddleThisDevice = "xPaddlePlayer1";
            fieldXPaddleOtherDevice = "xPaddlePlayer2";

            getBall().setX((float) ((screen_width/2) + 17.5));
            getBall().setY((float) ((screen_height/2) - 17.5));

            Log.d("Game", "valori " + String.valueOf(getBall().getX()) + " " + String.valueOf(getBall().getY()));

            roomRef.child(fieldXPaddleOtherDevice).setValue(getSizeX()/2);
            roomRef.child(fieldxBall).setValue(getBall().getX());
            roomRef.child(fieldyBall).setValue(getBall().getY());

        }else {

            paddle.setX((screen_width/2));
            paddle.setY((float) (getDownBoard() - (getSizeY()/20)));
            paddle2.setX((screen_width/2) - (paddle.getWidthp()));
            paddle2.setY((float) (getDownBoard() - (getSizeY()/20)));

            minPositionPaddle = screen_width/2;
            maxPositionPaddle = getRightBoard();

            fieldXPaddleThisDevice = "xPaddlePlayer2";
            fieldXPaddleOtherDevice = "xPaddlePlayer1";

            roomRef.child(fieldXPaddleOtherDevice).setValue((getSizeX()/2) - paddle.getWidthp());

        }

        //setto colonne e righe dei mattoni
        setColumns(9);
        setRow(10);

        //setto altezza e base del mattone
        setBrickBase((float) ((getSizeX() - (getSizeX()*0.05)))/getColumns());
        setBrickHeight((float) ((getSizeY()-(getSizeY() * 0.7))/getRow()));

        //setto il padding del campo di gioco
        setPaddingLeftGame((float) (((screen_width-getSizeX())/2) + (getSizeX()*0.02)));
        setPaddingTopGame((float) (((screen_height-getSizeY())/2) + (getSizeX()*0.16)));

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
        //canvas.drawBitmap(background, 0, 0, paint);
        Rect dest = new Rect(0, 0, screen_width, screen_height);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(background, null, dest, paint);

        paint.setColor(Color.BLACK);
        canvas.drawLine(getLeftBoard(),getUpBoard(),getLeftBoard(),getDownBoard(),paint);
        canvas.drawLine(getRightBoard(),getUpBoard(),getRightBoard(),getDownBoard(),paint);
        canvas.drawLine(getLeftBoard(),getUpBoard(),getRightBoard(),getUpBoard(),paint);
        canvas.drawLine(getLeftBoard(),getDownBoard(),getRightBoard(),getDownBoard(),paint);

        paint.setTextSize(30);


        canvas.drawText(playerRole, (float) screen_width/2, (float) getUpBoard() - 100, paint);
        canvas.drawText("width " + String.valueOf(screen_width) + "height " + String.valueOf(screen_height), (float) screen_width/2, (float) screen_height/2 - 100, paint);
        canvas.drawText("xBall " + String.valueOf(getBall().getX()) + "yBall " + String.valueOf(getBall().getY()), (float) screen_width/2, (float) screen_height/2, paint);
        // disegna la pallina
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawBitmap(getBall().getSkin(), getBall().getX(), getBall().getY(), paint);

        // disegna la barra
        paint.setColor(Color.WHITE);
        r = new RectF(paddle.getX(), getPaddle().getY(), getPaddle().getX() + getPaddle().getWidthp(), getPaddle().getY() + getPaddle().getHeightp());
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), null, r, paint);

        paint.setColor(Color.WHITE);
        r = new RectF(paddle2.getX(), paddle2.getY(), paddle2.getX() + paddle2.getWidthp(), paddle2.getY() + paddle2.getHeightp());
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
        canvas.drawText("Level: " + getNumberLevel(), (float) (screen_width-(screen_width*(0.60))), (float) (screen_width), paint);


        // in caso di sconfitta stampa "GameOver"
        /*if (isGameOver()) {
            if(levelCompleted()){
                if(getNumberLevel()<=15) {
                    try {
                        gameListener.onWinLevel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else { gameListener.onWinGame(); }

            }
        }*/

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
