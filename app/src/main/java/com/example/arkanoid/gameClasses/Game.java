package com.example.arkanoid.gameClasses;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.arkanoid.Settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import androidx.core.view.GestureDetectorCompat;


public class Game extends View implements SensorEventListener, View.OnTouchListener, GestureDetector.OnGestureListener{
    private static final String DEBUG_STRING = "Game";
    private static final int SYSTEM_CONTROL_SENSOR = 1; // sistema di controllo con movimento sensore
    private static final int SYSTEM_CONTROL_SCROLL = 0; // sistema di controllo con sliding
    private static final String FILE_NAME = "config.txt";

    private static final int DIMENSION = 90;
    private int lifes;
    private int score;
    private int numberLevel = 0;
    private Level level;
    private ArrayList<Level> levels;
    private ArrayList<Brick> brickList;
    private ArrayList<PowerUp> powerUps;

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
    private Paddle paddle;
    private PowerUp powerUp;

    private int sizeX;
    private int sizeY;
    private int brickBase;
    private int brickHeight;

    public Game(Context context, int lifes, int score) {
        super(context);

        //impostare contesto
        this.context = context;

        loadControlSystemFromFile();
        //impostare vite, punteggi e livelli
        this.lifes = lifes;
        this.score = score;
        brickList = new ArrayList<Brick>();
        levels = new ArrayList<Level>();
        powerUps= new ArrayList<PowerUp>();


        //avviare un GameOver per scoprire se la partita è in piedi e se il giocatore non l'ha persa
        start = false;
        gameOver = false;

        //crea palla e paddle
        ball = new Ball(0, 0);
        paddle = new Paddle(0, 0);

        //crea lista di livelli dal DB locale
        int i;
        String nameLevel = "Primo Livello";
        ArrayList<Integer> list = new ArrayList<Integer>(DIMENSION);
        ArrayList<Integer> list2 = new ArrayList<Integer>(DIMENSION);
        for(i=0; i<90; i++){
            if(i%2==0) list.add(0);
            else list.add(6);
        }

        levels.add(level = new Level(list,numberLevel,nameLevel));
        list.clear();
        for(i=0; i<90; i++){
            list.add(3);
        }
        levels.add(level = new Level(list,1,"Secondo Livello"));


        // fine caricamento da DB ----------------------------------------------

    }

    private void loadControlSystemFromFile() {
        //crea un accelerometro e un SensorManager
        Settings settings = null;

        try {

            FileInputStream fileIn = context.openFileInput(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            settings = (Settings) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        Log.d(DEBUG_STRING,String.valueOf(settings.getControlMode()));
        switch (settings.getControlMode()){

            case SYSTEM_CONTROL_SENSOR:
                sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                gestureDetector = null;
                break;
            case SYSTEM_CONTROL_SCROLL:
                gestureDetector = new GestureDetectorCompat(context,this);
                sManager = null;
                accelerometer = null;
                break;
        }

    }

    // riempire l'elenco con i mattoni
    public void generateBricks(Context context, Level level) {
        int a=0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                if(level.getA(a)!=0) {
                    brickList.add(new Brick(context, brickBase * j + 20, brickHeight  * i + 150, level.getA(a)));
                }
                a++;
            }
        }
    }

    //controllare che la palla non abbia toccato il bordo
    private void checkBoards() {
        if (ball.getX() + ball.getxSpeed() >= sizeX - 60) {
            ball.changeDirection("right");
        } else if (ball.getX() + ball.getxSpeed() <= 0) {
            ball.changeDirection("left");
        } else if (ball.getY() + ball.getySpeed() <= 150) {
            ball.changeDirection("up");
        } else if ((ball.getY()+ 45 + ball.getySpeed() >= sizeY - 200)&&(ball.getY()+ 45 + ball.getySpeed() <= sizeY - 185) ){
            if ((ball.getX() < paddle.getX() + paddle.getWidth() && ball.getX() > paddle.getX()) || (ball.getX() + 48 < paddle.getX() + paddle.getWidth() && ball.getX() + 48 > paddle.getX())) {
                 Log.e("pos",""+ball.getY()+"");
                Log.e("pos2", "" + paddle.getY() + "");
                Log.e("sizeY",""+sizeY+"");
                ball.changeDirection("down");
            } /*else {

                checkLives();
            }*/

        }else if((ball.getY() + ball.getySpeed() >= sizeY - 70)&&(ball.getY() + ball.getySpeed() <= sizeY - 50)){
            Log.e("pos",""+ball.getY()+"");
            Log.e("pos2",""+paddle.getY()+"");

            checkLives();

        }
    }

    // controllo collisione powerup paddle
    private void checkGetPowerUp(PowerUp powerUp) {
        if ((powerUp.getY()+ 45 + powerUp.getySpeed() >= sizeY - 200)&&(powerUp.getY()+ 45 + powerUp.getySpeed() <= sizeY - 185) ){
            if ((powerUp.getX() < paddle.getX() + paddle.getWidth() && powerUp.getX() > paddle.getX()) || (powerUp.getX() + 48 < paddle.getX() + paddle.getWidth() && powerUp.getX() + 48 > paddle.getX())) {
                powerUpEffect(powerUp);
                this.powerUps.remove(powerUp);
            }
        }else if((powerUp.getY() + powerUp.getySpeed() >= sizeY - 70)&&(powerUp.getY() + powerUp.getySpeed() <= sizeY - 50)){


            this.powerUps.remove(powerUp);

        }
    }
    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkLives() {

        if (lifes == 1) {
            gameOver = true;
            start = false;
            numberLevel=0;
            invalidate();
        } else{
            lifes--;

            powerUps.clear();
            ball.setX(sizeX / 2);
            ball.setY(sizeY - 280);
            ball.createSpeed();
            ball.increaseSpeed(level.getNumberLevel());
            start = false;
        }
        paddle.resetPaddle();
    }



    // ogni passaggio controlla se c'è una collisione, una perdita o una vittoria, ecc.
    public void update() {
        if (start) {
            win();
            checkBoards();
            //ball.hitPaddle(paddle.getX(), paddle.getY());
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b.getX(), b.getY())) {

                    if(generatePowerUp(b.getX(),b.getY()).getPower()!=null) {
                        powerUps.add(this.powerUp);
                    }
                    brickList.remove(i);

                    score = score + 80;
                }
            }
            for (int y= 0; y < powerUps.size(); y++) {
                checkGetPowerUp(powerUps.get(y));
            }

            ball.move();
            for (int j = 0; j < powerUps.size(); j++) {
                powerUps.get(j).move();
            }
        }
    }

    // crea random powerUp dopo la rottura del mattone
    public PowerUp generatePowerUp(float x , float y){
        this.powerUp = new PowerUp(context,x,y);
        return powerUp;
    }

    //imposta il gioco per iniziare
    public void resetLevel() {
        ball.setX(sizeX / 2);
        ball.setY(sizeY - 280);
        ball.createSpeed();
        powerUps.clear();
        brickList = new ArrayList<Brick>();
        generateBricks(context, levels.get(numberLevel));
    }

    //scopri se il giocatore ha vinto o meno
    private void win() {
        if (brickList.isEmpty()) {
            ++numberLevel;

            resetLevel();
            ball.increaseSpeed(numberLevel);
            start = false;
        }
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
            setScore(0);
            setLifes(3);
            resetLevel();
            setGameOver(false);

        } else {
            setStart(true);
        }
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(DEBUG_STRING, "onDown"+ e.getX() + " " + e.getY());

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(DEBUG_STRING, "onShowPress"+ e.getX() + " " + e.getY());


    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(DEBUG_STRING, "onSingleTapUp"+ e.getX() + " " + e.getY());

        return false;
    }

    public GestureDetectorCompat getGestureDetector() {
        return gestureDetector;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(DEBUG_STRING, "onScroll"+ e1.getX() + " " + e1.getY()
                + " " + e2.getX() + " " + e2.getY()
                + "  " + String.valueOf(distanceX) + "  " + String.valueOf(distanceY));

        paddle.setX(e2.getX());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_STRING, "onLongPress"+ e.getX() + " " + e.getY());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(DEBUG_STRING, "onFling"+ e1.getX() + " " + e1.getY()
                + " " + e2.getX() + " " + e2.getY()
                + "  " + String.valueOf(velocityX) + "  " + String.valueOf(velocityY));
        return false;
    }
    public void setBrickList(ArrayList<Brick> brickList) { this.brickList = brickList; }

    public ArrayList<Brick> getBrickList() { return brickList; }

    public Level getLevel() {
        return levels.get(numberLevel);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getLifes() {
        return lifes;
    }

    public int getScore() {
        return score;
    }

    public Ball getBall() { return ball; }

    public void setBall() { this.ball = ball; }

    public Paddle getPaddle() { return paddle; }

    public void setPaddle() { this.paddle = paddle; }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public void setScore(int score) {
        this.score = score;
    }

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

    public int getBrickBase() {
        return brickBase;
    }

    public void setBrickBase(int brickBase) {
        this.brickBase = brickBase;
    }

    public int getBrickHeight() {
        return brickHeight;
    }

    public void setBrickHeight(int brickHeight) {
        this.brickHeight = brickHeight;
    }

    public int getNumberLevel() {
        return numberLevel;
    }

    public void setNumberLevel(int numberLevel) {
        this.numberLevel = numberLevel;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    public int getSens() { return sens; }

    public void setSens(int sens) { this.sens = sens; }


    public ArrayList<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(ArrayList<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    public void powerUpEffect(PowerUp powerUp){
        switch(powerUp.getTypePower()){
            case 1:
                    lifes++;
                    break;
            case 2:
                    checkLivesAfterEffects();
                    break;
            case 3:
                    if(paddle.getWidth() < paddle.getMaxWidth()) {
                        paddle.setWidth(paddle.getWidth() + 50);
                    }
                    break;
            case 4:
                if(paddle.getWidth() > paddle.getMinWidth()) {
                    paddle.setWidth(paddle.getWidth() - 50);
                }
                break;
        }
    }

    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkLivesAfterEffects() {

        if (lifes == 1) {
            gameOver = true;
            start = false;
            numberLevel=0;
            paddle.resetPaddle();
            invalidate();
        } else {
            lifes--;
        }


    }
}
