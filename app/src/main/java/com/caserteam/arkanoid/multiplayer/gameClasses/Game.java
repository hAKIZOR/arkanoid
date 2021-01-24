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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.caserteam.arkanoid.DatabaseHelper;
import com.caserteam.arkanoid.IOUtils;
import com.caserteam.arkanoid.Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.validation.Validator;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;


public class Game extends View implements
        SensorEventListener,
        View.OnTouchListener,
        GestureDetector.OnGestureListener{
    private static final String DEBUG_STRING = "Game";


    private static final int DIMENSION = 90;
    private int lifes;
    private int score;
    private int numberLevel = 1;
    private Level level;
    private ArrayList<Level> levels;
    protected ArrayList<Brick> brickList;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<LaserSound> laserDropped;
    protected float minPositionPaddle;
    protected float maxPositionPaddle;

    protected String fieldXPaddle1;
    protected String fieldXPaddle2;
    protected String fieldSizeXPlayer1;
    protected String fieldSizeXPlayer2;
    protected String fieldSizeYPlayer1;
    protected String fieldSizeYPlayer2;
    protected String fieldxBall;
    protected String fieldyBall;
    protected String fieldxSpeedBall;
    protected String fieldySpeedBall;
    protected String fieldStarted;

    protected String brickBasePlayer2="brickBasePlayer2";
    protected String paddingLeftGamePlayer2="paddingLeftGamePlayer2";
    protected String paddingTopGamePlayer2= "paddingTopGamePlayer2";
    protected String brickHeightPlayer2="brickHeightPlayer2";

    protected float dpi2;
    protected float size2X;
    protected float size2Y;


    protected boolean start;
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

    protected Context context;
    protected Ball ball;
    protected Paddle paddle;
    protected Paddle paddle2;
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

    private int nS=1; //variabile usata per completare il nome del sound nel caricamento
    SoundPool soundPool;
    int[] soundNote = {-1, -1, -1, -1, -1, -1, -1, -1};
    AudioAttributes audioAttributes;
    protected String playerRole;
    protected DatabaseReference roomRef;
    protected DisplayMetrics metrics;

    public Game(Context context, int lifes, int score, String playerRole,DatabaseReference roomRef) {
        super(context);
        this.roomRef = roomRef;
        //impostare contesto
        this.context = context;

        loadControlSystemFromFile();
        //impostare vite, punteggi e livelli
        this.lifes = lifes;
        this.score = score;
        brickList = new ArrayList<>();
        levels = new ArrayList<>();
        powerUps= new ArrayList<>();
        laserDropped= new ArrayList<>();

        this.playerRole = playerRole;

        metrics = getResources().getDisplayMetrics();



        //avviare un GameOver per scoprire se la partita è in piedi e se il giocatore non l'ha persa
        start = false;
        gameOver = false;

        //crea palla e paddle

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

        Log.d(DEBUG_STRING,String.valueOf(settings.getControlMode()));
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
        }

    }

    // riempire l'elenco con i mattoni
    public void generateBricks(Context context, Level level, int columns, int row, float brickBase, float brickHeight, float paddingLeftGame, float paddingTopGame ) {


        if (playerRole.equals("player1")){

            int a=0;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < columns; j++) {
                    if(level.getA(a)!=0) {
                        brickList.add(new Brick(context,  (brickBase * j) + paddingLeftGame, (brickHeight* i) + paddingTopGame, level.getA(a),brickBase,brickHeight));
                    }
                    a++;
                }
            }

            roomRef.child("sizeXPlayer2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        size2X=value;
                        roomRef.child("sizeXPlayer2").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            roomRef.child("sizeYPlayer2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        size2Y=value;
                        roomRef.child("sizeYPlayer2").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            roomRef.child(brickBasePlayer2).setValue((getBrickBase()*1080)/1080);
            roomRef.child(brickHeightPlayer2).setValue((getBrickHeight()*2088)/2062);
            roomRef.child(paddingLeftGamePlayer2).setValue((getPaddingLeftGame()*1080)/1080);
            roomRef.child(paddingTopGamePlayer2).setValue((getPaddingTopGame()*2088)/2062);

        }else{


            roomRef.child(brickBasePlayer2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        setBrickBase(value);
                        roomRef.child(brickBasePlayer2).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            roomRef.child(brickHeightPlayer2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        setBrickHeight(value);
                        roomRef.child(brickHeightPlayer2).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            roomRef.child(paddingLeftGamePlayer2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        setpaddingLeftGame(value);
                        roomRef.child(paddingLeftGamePlayer2).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            roomRef.child(paddingTopGamePlayer2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float value = Float.parseFloat(snapshot.getValue().toString());
                    if( value != 0){
                        setpaddingTopGame(value);
                        roomRef.child(paddingTopGamePlayer2).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
            ball.increaseSpeed(level.getNumberLevel());
            start = false;
        }
        paddle.resetPaddle((float) (sizeX * (0.1)));
    }



    // ogni passaggio controlla se c'è una collisione, una perdita o una vittoria, ecc.
    public void update() {
        if (start) {
            win();
            checkBoards();
            ball.move();
            Log.d("Game","-----> sizeXplayer2" + size2X);
            Log.d("Game","-----> sizeYplayer2" + size2Y);
            for (int i = 0; i < brickList.size(); i++) {
                Brick b = brickList.get(i);
                if (ball.hitBrick(b)) {

                    if(playerRole.equals("player1")){

                        roomRef.child("brickX").setValue((float)( b.getX()*size2X)/sizeX);
                        roomRef.child("brickY").setValue((float)(b.getY()*size2Y)/sizeY);

                    }
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



            if(playerRole.equals("player1")) {
                ball.move();
                roomRef.child(fieldxBall).setValue((ball.getX()*size2X)/sizeX);
                roomRef.child(fieldyBall).setValue((ball.getY()*size2Y)/sizeY);
                roomRef.child(fieldxSpeedBall).setValue(ball.getxSpeed());
                roomRef.child(fieldySpeedBall).setValue(ball.getySpeed());
            }

        }else{
            if(playerRole.equals("player1")) {
                roomRef.child(fieldxBall).setValue((ball.getX()*size2X)/sizeX);
                roomRef.child(fieldyBall).setValue((ball.getY()*size2Y)/sizeY);
                roomRef.child(fieldxSpeedBall).setValue(ball.getxSpeed());
                roomRef.child(fieldySpeedBall).setValue(ball.getySpeed());
            }
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

    //imposta il gioco per iniziare
    public void resetLevel() {
        ball.setX(sizeX / 2);
        ball.setY(sizeY - 280);
        ball.createSpeed();
        powerUps.clear();
        laserDropped.clear();
        handsPianoRemaining = 0;
        brickList = new ArrayList<Brick>();

        generateBricks(context, getLevels().get(getNumberLevel()-1),getColumns(),getRow(),getBrickBase(),getBrickHeight(),getPaddingLeftGame(),getPaddingTopGame());
    }

    //scopri se il giocatore ha vinto o meno
    private void win() {
        if (levelCompleted()) {
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
            if(playerRole.equals("player1")){
                setStart(true);
                roomRef.child(fieldStarted).setValue(true);
            }
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

            if(e2.getY() > (sizeY*0.75)) {
                if ((e2.getX() - (paddle.getWidthp()/2) >= minPositionPaddle && (e2.getX() - (paddle.getWidthp()/2)<= (maxPositionPaddle - paddle.getWidthp())))){
                    float p = (e2.getX() * size2X) / sizeX;
                    sendToDb((float) (p - (size2X*(0.1)/2)));
                    paddle.setX(e2.getX() - (paddle.getWidthp()/2));



                } else if ((e2.getX() - (paddle.getWidthp()/2) < minPositionPaddle)) {

                    if(playerRole.equals("player2")){
                        sendToDb((float) (size2X/2));
                    } else {
                        sendToDb(minPositionPaddle);
                    }

                    paddle.setX(minPositionPaddle);

                } else if ((e2.getX() - (paddle.getWidthp()/2) > (maxPositionPaddle - paddle.getWidthp()))){

                    if(playerRole.equals("player1")){
                        sendToDb((float) ((size2X/2)-(size2X*(0.1))));
                    } else {
                        sendToDb((float) ((size2X)-(size2X*(0.1))));
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

    public Level getLevel() {
        return levels.get(numberLevel-1);
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

    public float getpaddingLeftGame() {
        return paddingLeftGame;
    }

    public void setpaddingLeftGame(float paddingLeftGame) {
        this.paddingLeftGame = paddingLeftGame;
    }
    public float getpaddingTopGame() {
        return paddingTopGame;
    }

    public void setpaddingTopGame(float paddingTopGame) {
        this.paddingTopGame = paddingTopGame;
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
                    paddle.setWidthp(paddle.getWidthp() + 50);
                }
                break;
            case 4:
                if(paddle.getWidthp() > paddle.getMinWidth()) {
                    paddle.setWidthp(paddle.getWidthp() - 50);
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
            paddle.resetPaddle((float) (sizeX * (0.1)));
            invalidate();
        } else {
            lifes--;
        }
    }

    public void handsPianoPower(double xSelected, double ySelected) {
        int i, indexMin = 0;
        Brick brick;
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
            if(brickList.get(i).getSkin()!=20){
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
    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    protected void sendToDb(float p1) {
        /*
        ((Dpi2 + size2) * p1) / (Dpi1 + size1))
        */

        //float value = (p1 * size2) / sizeX;
        //float value = ((dpi2 + size2) * p1) / (metrics.densityDpi + sizeX);

        roomRef.child(fieldXPaddle1).setValue(p1);
    }

}
