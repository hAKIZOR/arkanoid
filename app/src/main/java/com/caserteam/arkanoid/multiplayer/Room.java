package com.caserteam.arkanoid.multiplayer;

import com.caserteam.arkanoid.multiplayer.gameClasses.Brick;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@IgnoreExtraProperties
public class Room {
    public String player1;
    public String player2;
    public int xSpeedBall;
    public int ySpeedBall;
    public float xBall;
    public float yBall;
    public float sizeXPlayer1;
    public float sizeXPlayer2;
    public float sizeYPlayer1;
    public float sizeYPlayer2;
    public float  xPaddlePlayer1;
    public float  xPaddlePlayer2;
    public boolean started;
    public int score;
    public int life;
    public float brickBasePlayer2;
    public float paddingLeftGamePlayer2;
    public float paddingTopGamePlayer2;
    public float brickHeightPlayer2;
    public float brickX;
    public float brickY;

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Room( String player1, String player2, int xSpeedBall,
                 int ySpeedBall,float xBall,float yBall,float xPaddlePlayer1, float xPaddlePlayer2,boolean started,
                 float sizeXPlayer1, float sizeXPlayer2, float sizeYPlayer1,float sizeYPlayer2, int score, int life, float brickBasePlayer2,float paddingLeftGamePlayer2, float paddingTopGamePlayer2,float brickHeightPlayer2,
                 float brickX,float brickY) {
        this.player1 = player1;
        this.player2 = player2;
        this.xSpeedBall = xSpeedBall;
        this.ySpeedBall = ySpeedBall;
        this.xBall = xBall;
        this.yBall = yBall;
        this.sizeXPlayer1 = sizeXPlayer1;
        this.sizeXPlayer2 = sizeXPlayer2;
        this.sizeYPlayer1 = sizeYPlayer1;
        this.sizeYPlayer2 = sizeYPlayer2;
        this.xPaddlePlayer1 = xPaddlePlayer1;
        this.xPaddlePlayer2 = xPaddlePlayer2;
        this.started=started;
        this.score=score;
        this.life=life;
        this.brickBasePlayer2=brickBasePlayer2;
        this.paddingLeftGamePlayer2=paddingLeftGamePlayer2;
        this.paddingTopGamePlayer2=paddingTopGamePlayer2;
        this.brickHeightPlayer2=brickHeightPlayer2;
        this.brickX=brickY;
        this.brickY=brickY;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("player1", player1);
        result.put("player2", player2);
        result.put("xSpeedBall",xSpeedBall);
        result.put("ySpeedBall",ySpeedBall);
        result.put("xBall",xBall);
        result.put("yBall",yBall);
        result.put("sizeXPlayer1",sizeXPlayer1);
        result.put("sizeXPlayer2",sizeXPlayer2);
        result.put("sizeYPlayer1",sizeYPlayer1);
        result.put("sizeYPlayer2",sizeYPlayer2);
        result.put("xPaddlePlayer1", xPaddlePlayer1);
        result.put("xPaddlePlayer2", xPaddlePlayer2);
        result.put("started",started);
        result.put("score",score);
        result.put("life",life);
        result.put("brickBasePlayer2",brickBasePlayer2);
        result.put("paddingLeftGamePlayer2",paddingLeftGamePlayer2);
        result.put("paddingTopGamePlayer2",paddingTopGamePlayer2);
        result.put("brickHeightPlayer2",brickHeightPlayer2);
        result.put("brickX",brickX);
        result.put("brickY",brickY);


        return result;
    }
}
