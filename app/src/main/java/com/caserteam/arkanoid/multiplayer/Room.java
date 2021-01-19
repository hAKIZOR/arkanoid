package com.caserteam.arkanoid.multiplayer;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
@IgnoreExtraProperties
public class Room {
    public String player1;
    public String player2;
    public int xSpeedBall;
    public int ySpeedBall;
    public float sizeXPlayer1;
    public float sizeXPlayer2;
    public float dpiPlayer1;
    public float dpiPlayer2;
    public float  xPaddlePlayer1;
    public float  xPaddlePlayer2;


    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Room( String player1, String player2, int xSpeedBall,
                 int ySpeedBall,float xPaddlePlayer1, float xPaddlePlayer2,
                 float sizeXPlayer1, float sizeXPlayer2,
                float dpiPlayer1,float dpiPlayer2) {
        this.player1 = player1;
        this.player2 = player2;
        this.xSpeedBall = xSpeedBall;
        this.ySpeedBall = ySpeedBall;
        this.sizeXPlayer1 = sizeXPlayer1;
        this.sizeXPlayer2 = sizeXPlayer2;
        this.dpiPlayer1 = dpiPlayer1;
        this.dpiPlayer2 = dpiPlayer2;
        this.xPaddlePlayer1 = xPaddlePlayer1;
        this.xPaddlePlayer2 = xPaddlePlayer2;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("player1", player1);
        result.put("player2", player2);
        result.put("xSpeedBall",xSpeedBall);
        result.put("ySpeedBall",ySpeedBall);
        result.put("sizeXPlayer1",sizeXPlayer1);
        result.put("sizeXPlayer2",sizeXPlayer2);
        result.put("dpiPlayer1",dpiPlayer1);
        result.put("dpiPlayer2",dpiPlayer2);
        result.put("xPaddlePlayer1", xPaddlePlayer1);
        result.put("xPaddlePlayer2", xPaddlePlayer2);
        return result;
    }
}
