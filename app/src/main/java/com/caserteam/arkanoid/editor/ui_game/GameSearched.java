package com.caserteam.arkanoid.editor.ui_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.caserteam.arkanoid.IOUtils;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.Settings;
import com.caserteam.arkanoid.editor.BrickEditor;
import com.caserteam.arkanoid.editor.ui_search_check.LevelsSearchActivity;
import com.caserteam.arkanoid.gameClasses.GameListener;

import java.io.IOException;
import java.util.ArrayList;
import static com.caserteam.arkanoid.AppContractClass.*;

import androidx.core.view.GestureDetectorCompat;


public class GameSearched extends View implements
        SensorEventListener,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        ButtonPause.ButtonPauseListener{
    private static final String DEBUG_STRING = "Game";


    private static final int DIMENSION = 90;
    private static final String TAG = "Game";
    private int lifes;
    private int score;
    private int numberLevel = 1;
    private ArrayList<BrickGameSearched> brickList;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<LaserSound> laserDropped;



    private ButtonPause fabButtonPause;


    private boolean winLevel = false;
    private boolean pause = false;

    //variabili di gestione loop
    private static final int TIMINGFORWIN = 1000; // tempo di loop max, oltre questo tempo la partita viene automaticamente vinta
    private static final int MINBRICKFORTIMING = 4; // numero di mattoni minimo per poter iniziare il timing durante il loop
    private int timing = 0;
    private int counterBrickTiming = 0;


    private boolean start;
    private boolean gameOver;

    //variabili per la gestione del powerUp handsPiano
    private boolean handsPianoPowerFlag = false;
    private int handsPianoRemaining = 0;
    //variabili per la gestione del powerUp LaserSound
    private boolean laserSoundFlag = false;
    private LaserSound laserSound;
    private int laserSoundRemaining = 0;

    private SensorManager sManager;
    private Sensor accelerometer;
    private int sens;

    private GestureDetectorCompat gestureDetector;


    public Sensor getAccelerometer() {
        return accelerometer;
    }

    private Context context;
    private BallGameSearched ball;
    private PaddleGameSearched paddle;
    private PowerUp powerUp;

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

    private ButtonPause buttonPause;

    private int nS=1; //variabile usata per completare il nome del sound nel caricamento
    SoundPool soundPool;
    int[] soundNote = {-1, -1, -1, -1, -1, -1, -1, -1};
    AudioAttributes audioAttributes;
    private String structure;

    GameListener gameSearchedListener;


    public GameSearched(Context context, int lifes, int score) {
        super(context);

        //impostare contesto
        this.context = context;

        loadControlSystemFromFile();
        //impostare vite, punteggi e livelli
        this.lifes = lifes;
        this.score = score;
        brickList = new ArrayList<>();

        powerUps= new ArrayList<>();
        laserDropped= new ArrayList<>();



        //avviare un GameOver per scoprire se la partita è in piedi e se il giocatore non l'ha persa
        start = false;
        gameOver = false;

        //crea palla e paddle
        ball = new BallGameSearched(context,0, 0, 0);
        paddle = new PaddleGameSearched(context,0, 0, 0);

        //inizializzo soundPool
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);



    }

    private void loadControlSystemFromFile() {
        //crea un accelerometro e un SensorManager

        Settings settings = null;
        try {
            settings = (Settings) IOUtils.readObjectFromFile(context,FILE_NAME);
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
                gestureDetector = new GestureDetectorCompat(context,this);
                break;
            case SYSTEM_CONTROL_SCROLL:
                gestureDetector = new GestureDetectorCompat(context,this);
                sManager = null;
                accelerometer = null;
                break;
        }

    }



    //controllare che la palla non abbia toccato il bordo
    private void checkBoards() {
        if (ball.getX() + ball.getxSpeed() >= rightBoard) {
            ball.changeDirection("right");
        } else if (ball.getX() + ball.getxSpeed() <= leftBoard) {
            ball.changeDirection("left");
        } else if (ball.getY() + ball.getySpeed() <= upBoard) {
            ball.changeDirection("up");
        } else if ((ball.getY()+ ball.getySpeed() >= paddle.getY()-40)&&(ball.getY()+ ball.getySpeed() <= paddle.getY()+40) ){
            if ((ball.getX() < paddle.getX() + paddle.getWidthp() && ball.getX() > paddle.getX()) || (ball.getX() + ball.getHALFBALL() < paddle.getX() + paddle.getWidthp() && ball.getX() + ball.getHALFBALL() > paddle.getX())) {
                ball.changeDirectionPaddle(paddle);
            }

        }else if((ball.getY() + ball.getySpeed() >= sizeY - 70)&&(ball.getY() + ball.getySpeed() <= sizeY)){

            checkLives();

        }
    }

    // controllo collisione powerup paddle
    private void checkGetPowerUp(PowerUp powerUp) {
        if ((powerUp.getY()+ 45 + powerUp.getySpeed() >= sizeY - 200)&&(powerUp.getY()+ 45 + powerUp.getySpeed() <= sizeY - 185) ){
            if ((powerUp.getX() < paddle.getX() + paddle.getWidthp() && powerUp.getX() > paddle.getX()) || (powerUp.getX() + 48 < paddle.getX() + paddle.getWidthp() && powerUp.getX() + 48 > paddle.getX())) {
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
            numberLevel=1;
            invalidate();
        } else{
            lifes--;

            powerUps.clear();
            ball.setX(sizeX / 2);
            ball.setY(sizeY - 280);
            ball.createSpeed();
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
            counterBrickTiming = brickList.size();
            for (int i = 0; i < brickList.size(); i++) {
                BrickGameSearched b = brickList.get(i);
                if (ball.hitBrick(b)) {
                        if (b.getHitted()==b.getHit()) {
                                if (generatePowerUp(b.getX(), b.getY()).getPower() != null) {
                                    powerUps.add(this.powerUp);
                                }
                                soundPool.play(soundNote[b.getSoundName() - 1], 1, 1, 0, 0, 1);
                                brickList.remove(i);

                        } else {
                            brickList.get(i).hittedOnce();
                            brickList.get(i).setSkinById(b.getSkin());
                        }


                        score = score + 80;
                        timing = 0;
                        break;

                }


            }


            for (int i = 0; i < brickList.size(); i++) {
                BrickGameSearched b = brickList.get(i);

                for(int j = 0; j < laserDropped.size(); j++){
                if (laserDropped.get(j).hitBrick(b.getX(), b.getY())) {


                        if (generatePowerUp(b.getX(), b.getY()).getPower() != null) {
                            powerUps.add(this.powerUp);
                        }
                        soundPool.play(soundNote[b.getSoundName()], 1, 1, 0, 0, 1);
                        brickList.remove(i);

                    laserDropped.remove(j);
                    score = score + 80;
                }
                }
            }

            checkWinForLoop();

            for (int y= 0; y < powerUps.size(); y++) {
                checkGetPowerUp(powerUps.get(y));
            }

            ball.move();
            for (int j = 0; j < powerUps.size(); j++) {
                powerUps.get(j).move();
            }
            for (int j = 0; j < laserDropped.size(); j++) {
                laserDropped.get(j).move();
            }
        }
    }

    // controlla una vittoria automatica nel caso di loop per tot secondi e con un minimo di mattoni
    public void checkWinForLoop(){
        if(counterBrickTiming<=MINBRICKFORTIMING && counterBrickTiming == brickList.size()){
            timing++;
        }

        if(timing>TIMINGFORWIN){
            score = score + (80*brickList.size());
            brickList.clear();
            laserDropped.clear();
            win();
        }
    }

    // crea random powerUp dopo la rottura del mattone
    public PowerUp generatePowerUp(float x , float y){
        this.powerUp = new PowerUp(context,x,y);
        return powerUp;
    }

    // crea random powerUp dopo la rottura del mattone
    public LaserSound generateLaserDropped(float x , float y){
        this.laserSound = new LaserSound(context,x,y);
        return laserSound;
    }


    //scopri se il giocatore ha vinto o meno
    private void win() {
        if (levelCompleted()) {
            winLevel = true;
            gameSearchedListener.onWinGame();
        }
    }



    public void pauseGame() {
        if(sManager!= null){
            sManager.unregisterListener(this);
        }
    }

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

        } else {
            setStart(true);
        }
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {

        if(handsPianoPowerFlag){
            handsPianoPower(e.getX(),e.getY());
            if (handsPianoRemaining == 0) {
                handsPianoPowerFlag = false;
            }
        }

        if(laserSoundFlag){
            if(laserSoundRemaining != 0) {
                soundPool.play(soundNote[0], 1, 1, 0, 0, 1);
                laserDropped.add(generateLaserDropped(paddle.getX(), paddle.getY()));
                laserDropped.add(generateLaserDropped(paddle.getX() + paddle.getWidthp(), paddle.getY()));

                laserSoundRemaining--;
            }else {
                laserSoundFlag = false;
            }

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
        if ((e2.getX() - (paddle.getWidthp()/2)>=leftBoard && (e2.getX() - (paddle.getWidthp()/2)<= (rightBoard - paddle.getWidthp())))){
            if(e2.getY()>(sizeY*0.75)) {
                paddle.setX(e2.getX() - (paddle.getWidthp()/2));
            }
        }else if ((e2.getX() - (paddle.getWidthp()/2) < leftBoard)){
            if(e2.getY()>(sizeY*0.75)) {
                paddle.setX(leftBoard);
            }
        }else if ((e2.getX() - (paddle.getWidthp()/2) > (rightBoard - paddle.getWidthp()))){
            if(e2.getY() > (sizeY*0.75)) {
                paddle.setX(rightBoard-paddle.getWidthp());
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

    public void setBrickList(ArrayList<BrickGameSearched> brickList) { this.brickList = brickList; }

    public ArrayList<BrickGameSearched> getBrickList() { return brickList; }


    public boolean isGameOver() {
        return gameOver;
    }

    public int getLifes() {
        return lifes;
    }

    public int getScore() {
        return score;
    }

    public BallGameSearched getBall() { return ball; }

    public void setBall() { this.ball = ball; }

    public PaddleGameSearched getPaddle() { return paddle; }

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

    public int getNumberLevel() {
        return numberLevel;
    }

    public void setNumberLevel(int numberLevel) {
        this.numberLevel = numberLevel;
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
                    if(paddle.getWidthp() < paddle.getMaxWidth()) {
                        paddle.setWidth(paddle.getWidthp() + 50);
                    }
                    break;
            case 4:
                if(paddle.getWidthp() > paddle.getMinWidth()) {
                    paddle.setWidth(paddle.getWidthp() - 50);
                }
                break;
            case 5:
                handsPianoPowerFlag=true;
                handsPianoRemaining += 3;
                break;
            case 6:
                laserSoundRemaining += 3;
                laserSoundFlag=true;
                break;
        }
    }

    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkLivesAfterEffects() {

        if (lifes == 1) {
            gameOver = true;
            start = false;
            numberLevel=1;
            paddle.resetPaddle();
            invalidate();
        } else {
            lifes--;
        }
    }

    public void handsPianoPower(double xSelected, double ySelected) {
        int i, indexMin = 0;
        BrickGameSearched brick;
        double distance;
        double minDistance = Math.sqrt(Math.pow(brickList.get(0).getX() - xSelected, 2) + Math.pow(brickList.get(0).getY() - ySelected, 2));

        for (i = 0; i < brickList.size(); i++) {
            brick = brickList.get(i);
            distance = Math.sqrt(Math.pow((brick.getX()+(brickBase/2)) - xSelected, 2) + Math.pow((brick.getY()+(brickHeight/2)) - ySelected, 2));
            if (distance < minDistance) {
                minDistance = distance;
                indexMin = i;

            }


        }
        if(minDistance < 200) {
            soundPool.play(soundNote[brickList.get(indexMin).getSoundName() - 1], 1, 1, 0, 0, 1);
            brickList.remove(indexMin);
            score += 80;
            handsPianoRemaining--;
        }

    }


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

    public ArrayList<LaserSound> getLaserDropped() {
        return laserDropped;
    }

    public boolean levelCompleted(){
        boolean completed=true;
        for(int i =0; i<brickList.size(); i++){
            if(brickList.get(i).getSkin() != BRICK_OSTACLE1) {
                completed = false;
            }
        }
        return completed;
    }

    public int getHandsPianoRemaining() {
        return handsPianoRemaining;
    }

    public int getLaserSoundRemaining() {
        return laserSoundRemaining;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }
    public boolean isPaused() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    protected void generateLevelFromStructure(Context context, int columnsGrid, int rowsGrid, float brickWidth, float brickHeight, float paddingTopGrid, float paddingLeftGrid,String structure) {
        String [] list = structure.split(",");
        int indexList = 0;
        for (int i = 0; i < rowsGrid; i++) {
            for (int j = 0; j < columnsGrid; j++) {
                if(!list[indexList].equals("1")) {
                    Log.d("noedge--->", String.valueOf(indexList));
                    brickList.add(new BrickGameSearched(context, brickWidth * j + paddingLeftGrid, brickHeight * i + paddingTopGrid, Integer.parseInt(list[indexList]), brickBase, brickHeight));
                }
                indexList ++;
            }
        }

    }
    public ButtonPause getFabButtonPause() {
        return fabButtonPause;
    }

    public void setFabButtonPause(ButtonPause fabButtonPause) {
        this.fabButtonPause = fabButtonPause;
    }
    public void initializeButtonPause(Activity activity){
        fabButtonPause = new ButtonPause.Builder(activity)
                .withDrawable(getResources().getDrawable(R.drawable.pause_on,null))
                .withButtonColor(R.color.teal_200)
                .withGravity(Gravity.BOTTOM | Gravity.CENTER)
                .withMargins(0, 0, 0, 0)
                .create();
        fabButtonPause.setButtonPauseListener(this);
    }

    public void setGameSearchedListener(GameListener gameSearchedListener) {
        this.gameSearchedListener = gameSearchedListener;
    }

    @Override
    public void onButtonPauseClicked() {
        Log.d(TAG,"----------> cliccato pausa!");
        if(pause){
            pause= false;
        } else {
            pause = true;
        }
        gameSearchedListener.onPauseGame(pause);
    }


    public boolean getWinLevel() {
        return winLevel;
    }
}
