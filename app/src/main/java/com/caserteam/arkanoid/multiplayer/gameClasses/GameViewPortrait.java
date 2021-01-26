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

import com.caserteam.arkanoid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;


public class GameViewPortrait extends Game {

    private Bitmap background;
    private Display display;
    private Point size;
    private Paint paint;
    private RectF r;


    public GameViewPortrait(Context context,int lifes, int score,String playerRole, DatabaseReference roomRef){
        super(context, lifes, score,roomRef);
        paint = new Paint();
        setBackground(context);
        setSens(2); // <-- setta la sensitività dell'accellerometro
        setSizeXThisDevice(size.x);
        setSizeYThisDevice(size.y);

        this.playerRole = playerRole;
        //crea palla e paddle
        ball = new Ball(context,0, 0, 0,(float) (size.x * (0.05)),(float) (size.y * (0.03)));
        super.roomRef.addValueEventListener(this);

        //setta posizione della palla
        getBall().setX(size.x / 2);
        getBall().setY(size.y - 280);


        //setto le posizioni iniziali dei paddle

        paddle.setWidthp((float) (size.x * (0.1)));
        paddle2.setWidthp((float) (size.x * (0.1)));



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
            fieldSizeXPlayerThisDevice = "sizeXPlayer1";
            fieldSizeXPlayerOtherDevice = "sizeXPlayer2";
            fieldSizeYPlayerThisDevice = "sizeYPlayer1";
            fieldSizeYPlayerOtherDevice = "sizeYPlayer2";
            fieldxBall="xBall";
            fieldyBall="yBall";
            fieldxSpeedBall="xSpeedBall";
            fieldySpeedBall="ySpeedBall";
            fieldStarted="started";

            roomRef.child(fieldSizeXPlayerThisDevice).setValue(size.x);
            roomRef.child(fieldSizeXPlayerOtherDevice).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        sendToDb((float) ((value/2) - (value*(0.1))));
                        roomRef.child(fieldSizeXPlayerOtherDevice).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            paddle.setX((size.x/2));
            paddle.setY((float) (size.y - (size.y / 50)));
            paddle2.setX((size.x/2) - (paddle.getWidthp()));
            paddle2.setY((float) (size.y - (size.y / 50)));

            minPositionPaddle = size.x/2;
            maxPositionPaddle = size.x;

            fieldXPaddleThisDevice = "xPaddlePlayer2";
            fieldXPaddleOtherDevice = "xPaddlePlayer1";
            fieldxBall="xBall";
            fieldyBall="yBall";
            fieldxSpeedBall="xSpeedBall";
            fieldySpeedBall="ySpeedBall";
            fieldStarted="started";
            fieldSizeXPlayerThisDevice = "sizeXPlayer2";
            fieldSizeXPlayerOtherDevice = "sizeXPlayer1";
            fieldSizeYPlayerThisDevice = "sizeYPlayer2";
            fieldSizeYPlayerOtherDevice = "sizeYPlayer1";

            roomRef.child(fieldSizeXPlayerThisDevice).setValue(size.x);
            roomRef.child(fieldSizeXPlayerOtherDevice).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        sendToDb((float) (value/2));
                        roomRef.child(fieldSizeXPlayerThisDevice).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        roomRef.child(fieldSizeYPlayerThisDevice).setValue(size.y);
        roomRef.child(fieldSizeYPlayerOtherDevice).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float value = Float.parseFloat(snapshot.getValue().toString());
                if( value != 0){
                    sizeYOtherDevice = value;
                    roomRef.child(fieldSizeYPlayerOtherDevice).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //setto i bordi
        setUpBoard(150);
        setDownBoard(size.y);
        setLeftBoard(0);
        setRightBoard(getSizeXThisDevice() - 60);

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


        // disegna le barre
        paint.setColor(Color.WHITE);
        r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + paddle.getWidthp(), paddle.getY() + paddle.getHeightp());
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), null, r, paint);
        canvas.drawCircle(paddle.getX(), paddle.getY(),8,paint);
        r = new RectF(paddle2.getX(), paddle2.getY(), paddle2.getX() + paddle2.getWidthp(), paddle2.getY() + paddle2.getHeightp());
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), null, r, paint);
        canvas.drawCircle(paddle2.getX(), paddle2.getY(),3,paint);


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
        Typeface typeface = ResourcesCompat.getFont(super.getContext(), R.font.font);
        paint.setTypeface(typeface);
        canvas.drawText("HP:" + getLifes(), 1, 100, paint);
        canvas.drawText("PT:" + getScore(), 200, 100, paint);
        canvas.drawText("LSR:" + getLaserSoundRemaining(), 550, 100, paint);
        canvas.drawText("PIANO:" + getHandsPianoRemaining(), 800, 100, paint);

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
        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getPaddle().setX(getPaddle().getX() - (event.values[0] * getSens()));
            if (getPaddle().getX() + event.values[0] > size.x - getPaddle().getWidthp()) {
                getPaddle().setX(size.x - getPaddle().getWidthp());
            } else if (getPaddle().getX() - event.values[0] <= 20) {
                getPaddle().setX(20);
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}