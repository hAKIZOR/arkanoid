package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.IOUtils;
import com.caserteam.arkanoid.Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class Game extends View implements
        SensorEventListener,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        ValueEventListener {
    private static final String DEBUG_STRING = "Game";


    private static final int DIMENSION = 27;
    private Level level;
    private ArrayList<Brick> brickList;
    private ArrayList<Integer> list;

    private boolean start;
    private boolean gameOver;

    private SensorManager sManager;
    private Sensor accelerometer;
    private int sens;

    private GestureDetectorCompat gestureDetector;

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    private Context context;
    private Ball ball;
    private Paddle paddle,paddle2;

    private int sizeX;
    private int sizeY;
    private float brickBase;
    private float brickHeight;

    private int upBoard;
    private int downBoard;
    private int leftBoard;
    private int rightBoard;

    private int columns;
    private int row;
    private float paddingLeftGame;
    private float paddingTopGame;
    private DatabaseReference room;
    private String p1,p2;
    private int scoreP1,scoreP2;
    private String playerRole;


    private int nS=1; //variabile usata per completare il nome del sound nel caricamento
    SoundPool soundPool;
    int[] soundNote = {-1, -1, -1, -1, -1, -1, -1, -1};
    AudioAttributes audioAttributes;

    public Game(Context context,DatabaseReference room,String p1,String p2,String playerRole) {
        super(context);

        //impostare contesto
        this.context = context;
        this.p1 = p1;
        this.p2 = p2;
        this.room = room;
        this.playerRole = playerRole;
        room.addValueEventListener(this);



        loadControlSystemFromFile();
        //impostare vite, punteggi e livelli
        brickList = new ArrayList<>();
        list = new ArrayList<>();
        scoreP1=0;
        scoreP2=0;
        //avviare un GameOver per scoprire se la partita è in piedi e se il giocatore non l'ha persa
        start = true;
        gameOver = false;

        //crea palla e paddle
        ball = new Ball(context,0, 0, 0);
        paddle = new Paddle(context,0, 0, 0);
        paddle2 = new Paddle(context,0, 0, 0);

        //crea livello dal DB locale

        for(int i=0; i<DIMENSION; i++){
            if(i>8 && i<18) list.add(0);
            else list.add(0);
        }

        level = new Level(list,100,"MULTIPLAYER");

        // fine caricamento da DB ----------------------------------------------

            Log.e("sdk","DENTRO ELSE");
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            for(int i=0; i<8; i++) {
                Log.e("error", ""+nS);
                descriptor = assetManager.openFd("sound"+nS+".mp3");
                soundNote[i] = soundPool.load(descriptor, 0);
                nS++;
            }
        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }


    }

    private void loadControlSystemFromFile() {
        //crea un accelerometro e un SensorManager

        Settings settings = null;
        try {
            settings = (Settings) IOUtils.readObjectFromFile(context,Settings.FILE_NAME);
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        /*Log.d(DEBUG_STRING,String.valueOf(settings.getControlMode()));
        switch (settings.getControlMode()){

            case Settings.SYSTEM_CONTROL_SENSOR:
                sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                gestureDetector = new GestureDetectorCompat(context,this);
                break;
            case Settings.SYSTEM_CONTROL_SCROLL:
                gestureDetector = new GestureDetectorCompat(context,this);
                sManager = null;
                accelerometer = null;
                break;
        }*/

        gestureDetector = new GestureDetectorCompat(context,this);
        sManager = null;
        accelerometer = null;

    }

    // riempire l'elenco con i mattoni
    public void generateBricks(Context context, Level level, int columns, int row, float brickBase, float brickHeight, float paddingLeftGame, float paddingTopGame ) {
        int a=0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < columns; j++) {
                if(level.getA(a)!=0) {
                    brickList.add(new Brick(context,  (brickBase * j) + paddingLeftGame, (brickHeight* i) + paddingTopGame, level.getA(a),brickBase,brickHeight));
                }
                a++;
            }
        }
    }

    //controllare che la palla non abbia toccato il bordo
    private void checkBoards() {
        if (ball.getX() + ball.getxSpeed() >= rightBoard) {
            ball.changeDirection("right");
        } else if (ball.getX() + ball.getxSpeed() <= leftBoard) {
            ball.changeDirection("left");
        }  else if (ball.getY() + ball.getySpeed() <= upBoard) {
            checkScore("player1");
        } else if ((ball.getY()+ ball.getySpeed() >= paddle.getY()-40)&&(ball.getY()+ ball.getySpeed() <= paddle.getY()+40) ){
            if ((ball.getX() < paddle.getX() + paddle.getWidthp() && ball.getX() > paddle.getX()) || (ball.getX() + ball.getHALFBALL() < paddle.getX() + paddle.getWidthp() && ball.getX() + ball.getHALFBALL() > paddle.getX())) {
                ball.changeDirectionPaddle(paddle);
                    room.child("xSpeedBall").setValue(-ball.xSpeed);
                    room.child("ySpeedBall").setValue(-ball.ySpeed);
            }

        }else if((ball.getY() + ball.getySpeed() >= sizeY - 70)&&(ball.getY() + ball.getySpeed() <= sizeY)){

            checkScore("player2");

        }
    }

    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkScore(String player) {

        if(player.equals("player1")) {
            if (scoreP1== 2) {
                gameOver = true;
                start = false;
                invalidate();
            } else {
                scoreP1++;

                ball.setX(sizeX / 2);
                ball.setY((float) ((sizeY*0.10)+getPaddle().getHeightp())-30);
                start = true;
            }

        }else{
            if (scoreP2== 2) {
                gameOver = true;
                start = false;
                invalidate();
            } else {
                scoreP2++;

                ball.setX(sizeX / 2);
                ball.setY(sizeY - 280);
                start = true;
            }
        }

        paddle.resetPaddle();
    }



    // ogni passaggio controlla se c'è una collisione, una perdita o una vittoria, ecc.
    public void update() {
        if (start) {
            win();
            checkBoards();
            ball.move();
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b)) {
                        if (b.getHitted()==b.getHit()) {

                                soundPool.play(soundNote[b.getSoundName() - 1], 1, 1, 0, 0, 1);
                                brickList.remove(i);

                        } else {
                            brickList.get(i).hittedOnce();
                            brickList.get(i).setSkinById(b.getSkin());
                        }
                        break;
                }
            }
           ball.move();

        }
    }

    //imposta il gioco per iniziare
    public void resetLevel() {
        ball.setX(sizeX / 2);
        ball.setY(sizeY - 280);

        brickList = new ArrayList<Brick>();

        generateBricks(context, getLevel(),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());
    }

    //scopri se il giocatore ha vinto o meno
    private void win() {

    }



    public void pauseGame() { if(sManager!= null) sManager.unregisterListener(this); }

    public void resumeGame() {
        if(sManager!= null) sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //cambiare accelerometro
    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //serve a sospendere il gioco in caso di un nuovo gioco
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isGameOver() == true && isStart() == false) {
            scoreP1=0;
            scoreP2=0;
            resetLevel();
            setGameOver(false);

        } else {
            setStart(true);
        }
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        if (isGameOver() == true && isStart() == false) {
            scoreP1=0;
            scoreP2=0;
            resetLevel();
            setGameOver(false);

        } else {
            setStart(true);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    public GestureDetectorCompat getGestureDetector() {
        return gestureDetector;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    if(accelerometer==null) {
        if ((e2.getX() - (paddle.getWidthp()/2)>=0 && (e2.getX() - (paddle.getWidthp()/2)<= (sizeX - paddle.getWidthp())))){
            if(e2.getY()>(sizeY*0.75)) {
                paddle.setX(e2.getX() - (paddle.getWidthp()/2));
                room.child(p1).setValue(e2.getX() - (paddle.getWidthp()/2));
            }
        }else if ((e2.getX() - (paddle.getWidthp()/2) < 0)){
            if(e2.getY()>(sizeY*0.75)) {
                paddle.setX(0);
                room.child(p1).setValue(0);
            }
        }else if ((e2.getX() - (paddle.getWidthp()/2) > (sizeX - paddle.getWidthp()))){
            if(e2.getY()>(sizeY*0.75)) {
                paddle.setX(sizeX-paddle.getWidthp());
                room.child(p1).setValue(sizeX-paddle.getWidthp());
            }
        }
    }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void setBrickList(ArrayList<Brick> brickList) { this.brickList = brickList; }

    public ArrayList<Brick> getBrickList() { return brickList; }

    public boolean isGameOver() {
        return gameOver;
    }

    public Ball getBall() { return ball; }

    public void setBall() { this.ball = ball; }

    public Paddle getPaddle() { return paddle; }

    public void setPaddle() { this.paddle = paddle;}

    public Paddle getPaddle2() { return paddle2; }

    public void setPaddle2() { this.paddle2 = paddle2;}

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getSizeX() { return sizeX; }

    public void setSizeX(int sizeX) { this.sizeX = sizeX; }

    public int getSizeY() { return sizeY; }

    public void setSizeY(int sizeY) { this.sizeY = sizeY; }

    public float getBrickBase() {
        return brickBase;
    }

    public void setBrickBase(float brickBase) {
        this.brickBase = brickBase;
    }

    public float getBrickHeight() {
        return brickHeight;
    }

    public void setBrickHeight(float brickHeight) {
        this.brickHeight = brickHeight;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getSens() { return sens; }

    public void setSens(int sens) { this.sens = sens; }

    public int getUpBoard() { return upBoard;  }

    public void setUpBoard(int upBoard) {  this.upBoard = upBoard;  }

    public int getDownBoard() { return downBoard; }

    public void setDownBoard(int downBoard) { this.downBoard = downBoard; }

    public int getLeftBoard() { return leftBoard; }

    public void setLeftBoard(int leftBoard) { this.leftBoard = leftBoard; }

    public int getRightBoard() { return rightBoard; }

    public void setRightBoard(int rightBoard) { this.rightBoard = rightBoard;  }

    public int getColumns() { return columns; }

    public void setColumns(int columns) { this.columns = columns; }

    public int getRow() { return row; }

    public void setRow(int row) { this.row = row;    }

    public float getPaddingLeftGame() {
        return paddingLeftGame;
    }

    public void setPaddingLeftGame(float paddingLeftGame) {
        this.paddingLeftGame = paddingLeftGame;
    }

    public float getPaddingTopGame() {
        return paddingTopGame;
    }

    public void setPaddingTopGame(float paddingTopGame) {
        this.paddingTopGame = paddingTopGame;
    }


    public void setMultiplayerData(float xPaddle2){
        paddle2.setX(xPaddle2);
    }

    public float getMultiplayerData(){
        return  paddle.getX();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        setMultiplayerData(Float.parseFloat(snapshot.child(p2).getValue().toString()));

        if(playerRole.equals("player1")){

            getBall().setSpeed(Integer.parseInt(snapshot.child("xSpeedBall").getValue().toString()),
                    Integer.parseInt(snapshot.child("ySpeedBall").getValue().toString()));
        } else{
            if(Integer.parseInt(snapshot.child("xSpeedBall").getValue().toString())!=-ball.getxSpeed() && Integer.parseInt(snapshot.child("ySpeedBall").getValue().toString())!=-ball.getySpeed()) {
                getBall().setSpeed((-1) * (Integer.parseInt(snapshot.child("xSpeedBall").getValue().toString())),
                        (-1) * (Integer.parseInt(snapshot.child("ySpeedBall").getValue().toString())));
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public String getP1(){return p1;}

    public int getScoreP1() {
        return scoreP1;
    }

    public void setScoreP1(int scoreP1) {
        this.scoreP1 = scoreP1;
    }

    public int getScoreP2() {
        return scoreP2;
    }

    public void setScoreP2(int scoreP2) {
        this.scoreP2 = scoreP2;
    }
}
