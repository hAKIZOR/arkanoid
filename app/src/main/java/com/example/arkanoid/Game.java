package com.example.arkanoid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;


public class Game extends View implements SensorEventListener, View.OnTouchListener {

    private static final int DIMENSION = 90;
    private int lifes;
    private int score;
    private int numberLevel = 0;
    private Level level;
    private ArrayList<Level> levels;
    private ArrayList<Brick> brickList;

    private boolean start;
    private boolean gameOver;

    private SensorManager sManager;
    private Sensor accelerometer;

    private Context context;
    private Ball ball;
    private Paddle paddle;

    private int sizeX;
    private int sizeY;
    private int brickBase;
    private int brickHeight;

    public Game(Context context, int lifes, int score) {
        super(context);
        //crea un accelerometro e un SensorManager
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //impostare contesto
        this.context = context;

        //impostare vite, punteggi e livelli
        this.lifes = lifes;
        this.score = score;
        brickList = new ArrayList<Brick>();
        levels = new ArrayList<Level>();

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
        } else if (ball.getY() + ball.getySpeed() >= sizeY - 200) {
            checkLives();
        }
    }

    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkLives() {
        if (lifes == 1) {
            gameOver = true;
            start = false;
            numberLevel=0;
            invalidate();
        } else {
            lifes--;
            ball.setX(sizeX / 2);
            ball.setY(sizeY - 280);
            ball.createSpeed();
            ball.increaseSpeed(level.getNumberLevel());
            start = false;
        }
    }


    // ogni passaggio controlla se c'è una collisione, una perdita o una vittoria, ecc.
    public void update() {
        if (start) {
            win();
            checkBoards();
            ball.hitPaddle(paddle.getX(), paddle.getY());
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b.getX(), b.getY())) {
                    brickList.remove(i);
                    score = score + 80;
                }
            }
            ball.move();
        }
    }



    //imposta il gioco per iniziare
    public void resetLevel() {
        ball.setX(sizeX / 2);
        ball.setY(sizeY - 280);
        ball.createSpeed();
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



    public void pauseGame() { sManager.unregisterListener(this);
    }

    public void resumeGame() {
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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
}
