package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.IOUtils;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.Settings;
import com.caserteam.arkanoid.editor.ui_game.ButtonPause;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import static com.caserteam.arkanoid.AppContractClass.*;
import java.io.IOException;
import java.util.ArrayList;
import static com.caserteam.arkanoid.AppContractClass.*;


public class Game extends View implements
        SensorEventListener,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        ValueEventListener,
        ButtonPause.ButtonPauseListener{
    private static final String DEBUG_STRING = "Game";


    private static final int DIMENSION = 90;
    private static final String TAG = "Game";
    private int lifes;
    private int score;
    private int numberLevel = 1;
    private Level level;
    private ArrayList<Brick> brickList;
    private ArrayList<Level> levels;



    private ButtonPause fabButtonPause;

    private boolean pause = false;

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
    protected Paddle paddle;

    private int sizeX;
    private int sizeY;
    private float brickBase;
    private float brickHeight;


    //variabili multiplayer
    protected Paddle paddle2;
    protected String fieldXPaddleThisDevice;
    protected String fieldXPaddleOtherDevice;
    protected String fieldxBall;
    protected String fieldyBall;
    protected String fieldxSpeedBall;
    protected String fieldySpeedBall;
    protected String fieldScore;
    protected String fieldLifes;
    protected String fieldStarted;
    protected String fieldNumberLevel;
    protected float minPositionPaddle;
    protected float maxPositionPaddle;
    protected DatabaseReference roomRef;
    protected String playerRole;


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

    GameListener gameListener;


    public Game(Context context, int lifes, int score, String playerRole, DatabaseReference roomRef) {
        super(context);

        //impostare contesto
        this.context = context;

        loadControlSystemFromFile();
        //impostare vite, punteggi e livelli
        this.lifes = lifes;
        this.score = score;
        levels = new ArrayList<>();
        brickList = new ArrayList<>();

        //impostare valori multiplayer
        this.playerRole=playerRole;
        this.roomRef=roomRef;
        roomRef.addValueEventListener(this);

        //avviare un GameOver per scoprire se la partita è in piedi e se il giocatore non l'ha persa
        start = false;
        gameOver = false;

        //crea palla e paddle
        ball = new Ball(context,0, 0, 0);
        paddle = new Paddle(context,0, 0, 0);
        paddle2 = new Paddle(context,0, 0, 0);

        //crea lista di livelli dal DB locale
        Cursor c = null;

        try {
            DatabaseHelper myDbHelper = new DatabaseHelper(context);
            myDbHelper.openDataBase();
            c = myDbHelper.query("levels", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    ArrayList<Integer> list = new ArrayList<Integer>(DIMENSION);

                    String[] splitList = c.getString(2).split(",");

                    for(int i=0;  i < splitList.length; i++){
                        list.add(Integer.parseInt(String.valueOf(splitList[i])));
                    }
                    levels.add(level = new Level(list,Integer.parseInt(c.getString(0)),c.getString(1)));
                } while (c.moveToNext());
            }

            c.close();
            myDbHelper.close();

        } catch (IOException e){

        } catch (SQLException sqle) {
            throw sqle;
        }
        // fine caricamento da DB ----------------------------------------------

        //inizializzo soundPool
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
            if ((ball.getX() < paddle2.getX() + paddle2.getWidthp() && ball.getX() > paddle2.getX()) || (ball.getX() + ball.getHALFBALL() < paddle2.getX() + paddle2.getWidthp() && ball.getX() + ball.getHALFBALL() > paddle2.getX())) {
                ball.changeDirectionPaddle(paddle2);
            }

        }else if(ball.getY() + ball.getySpeed() > paddle.getY()+100){

            checkLives();

        }
    }


    // controlla lo stato del gioco. se le mie vite o se il gioco è finito
    public void checkLives() {

        if (lifes == 1) {
            gameOver = true;
            numberLevel=1;
            invalidate();
        } else{
            lifes--;
            roomRef.child(fieldLifes).setValue(lifes);
            ball.setX((sizeX / 2) + leftBoard);
            ball.setY(downBoard - 280);
            ball.createSpeed();
        }

        if(playerRole.equals(ROLE_PLAYER1)) {
            start = false;
            roomRef.child(fieldStarted).setValue(false);
        } else {
            start = false;
        }

        paddle.resetPaddle();
    }



    // ogni passaggio controlla se c'è una collisione, una perdita o una vittoria, ecc.
    public void update() {
        if (start) {

            //ball.hitPaddle(paddle.getX(), paddle.getY());
            win();
            if(playerRole.equals(ROLE_PLAYER1)){
                checkBoards();
                ball.move();
                setValuesOtherDevice();
            }
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
                        score = score + 80;


                        break;

                }


            }
        }else {
            if(playerRole.equals(ROLE_PLAYER1)){
                setValuesOtherDevice();
            }
        }
    }

    private void setValuesOtherDevice() {
        float ballxOther = (ball.getX() - leftBoard);
        float ballyOther = (ball.getY() - upBoard);
        roomRef.child(fieldScore).setValue(score);
        roomRef.child(fieldLifes).setValue(lifes);
        roomRef.child(fieldNumberLevel).setValue(numberLevel);
        roomRef.child(fieldxBall).setValue(ballxOther);
        roomRef.child(fieldyBall).setValue(ballyOther);
        roomRef.child(fieldxSpeedBall).setValue(ball.getxSpeed());
        roomRef.child(fieldySpeedBall).setValue(ball.getySpeed());

    }

    //imposta il gioco per iniziare
    public void resetLevel() {
        ball.setX((rightBoard+leftBoard) / 2);
        ball.setY(downBoard - 280);
        ball.createSpeed();
        brickList = new ArrayList<Brick>();

        generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());
    }

    //scopri se il giocatore ha vinto o meno
    private void win() {
        if (levelCompleted()) {
            ++numberLevel;
            resetLevel();
            ball.increaseSpeed(numberLevel);
            if(playerRole.equals(ROLE_PLAYER1)) {
                start = false;
                roomRef.child(fieldStarted).setValue(false);
            }
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
            if(playerRole.equals(ROLE_PLAYER1)) {
                setStart(true);
                roomRef.child(fieldStarted).setValue(true);
            }
        }
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {
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

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(accelerometer==null) {

            if(e2.getY() > (downBoard-(sizeY*0.25))) {

                if ((e2.getX() - (paddle.getWidthp()/2) >= minPositionPaddle && (e2.getX() - (paddle.getWidthp()/2)<= (maxPositionPaddle - paddle.getWidthp())))){
                    float p = (e2.getX() - leftBoard) - (paddle.getWidthp()/2);
                    roomRef.child(fieldXPaddleThisDevice).setValue(p);
                    paddle.setX(e2.getX() - (paddle.getWidthp()/2));


                } else if ((e2.getX() - (paddle.getWidthp()/2) < minPositionPaddle)) {

                    if(playerRole.equals(ROLE_PLAYER2)){
                        roomRef.child(fieldXPaddleThisDevice).setValue((sizeX/2));
                    } else {
                        roomRef.child(fieldXPaddleThisDevice).setValue(0);

                    }

                    paddle.setX(minPositionPaddle);

                } else if ((e2.getX() - (paddle.getWidthp()/2) > (maxPositionPaddle - paddle.getWidthp()))){

                    if(playerRole.equals(ROLE_PLAYER1)){
                        roomRef.child(fieldXPaddleThisDevice).setValue((float) ((sizeX/2)-(paddle.getWidthp())));
                    } else {
                        roomRef.child(fieldXPaddleThisDevice).setValue((float) ((sizeX)-(paddle.getWidthp())));
                    }

                    paddle.setX(maxPositionPaddle-paddle.getWidthp());

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

    public boolean levelCompleted(){
        boolean completed=true;
        for(int i =0; i<brickList.size(); i++){
            if(brickList.get(i).getSkin()!= BRICK_OSTACLE1) {
                completed = false;
            }
        }
        return completed;
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
                    brickList.add(new Brick(context, brickWidth * j + paddingLeftGrid, brickHeight * i + paddingTopGrid, Integer.parseInt(list[indexList]), brickBase, brickHeight));
                }
                indexList ++;
            }
        }

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

    public ButtonPause getFabButtonPause() {
        return fabButtonPause;
    }

    public void setFabButtonPause(ButtonPause fabButtonPause) {
        this.fabButtonPause = fabButtonPause;
    }
    public void initializeButtonPause(Activity activity){
        fabButtonPause = new ButtonPause.Builder(activity)
                .withDrawable(getResources().getDrawable(R.drawable.pause_on,null))
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(0, 0, 0, 0)
                .create();
        fabButtonPause.setButtonPauseListener(this);
    }

    public void setGameSearchedListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public void onButtonPauseClicked() {
        Log.d(TAG,"----------> cliccato pausa!");
        if(pause){
            pause= false;
        } else {
            pause = true;
        }
        gameListener.onPauseGame(pause);
    }



    public ArrayList<Level> getLevels() {
        return levels;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        float xPaddle = Float.parseFloat(snapshot.child(fieldXPaddleOtherDevice).getValue().toString());
        paddle2.setX((leftBoard + xPaddle));

        if(playerRole.equals(ROLE_PLAYER2)) {
            float ballX = Float.parseFloat(snapshot.child(fieldxBall).getValue().toString());
            float ballY = Float.parseFloat(snapshot.child(fieldyBall).getValue().toString());
            float ballspeedX = Integer.parseInt(snapshot.child(fieldxSpeedBall).getValue().toString());
            float ballspeedY = Integer.parseInt(snapshot.child(fieldySpeedBall).getValue().toString());
            boolean startValue = Boolean.parseBoolean(snapshot.child(fieldStarted).getValue().toString());


            ball.setX((float) (ballX + leftBoard + 17.5));
            ball.setY((float) ((ballY + upBoard) - 17.5));
            ball.setSpeed(ballspeedX,ballspeedY);
            numberLevel = Integer.parseInt(snapshot.child(fieldNumberLevel).getValue().toString());
            lifes = Integer.parseInt(snapshot.child(fieldLifes).getValue().toString());
            score = Integer.parseInt(snapshot.child(fieldScore).getValue().toString());
            start = startValue;
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }


}
