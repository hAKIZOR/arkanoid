package com.example.arkanoid;

public class Ball {

    protected float xSpeed;
    protected float ySpeed;
    private float x;
    private float y;

    public Ball(float x, float y) {
        this.x = x;
        this.y = y;
        createSpeed();
    }

    // crea una palla di velocità casuale
    // crear una bola de velocidad aleatoria
    protected void createSpeed() {
        int maxX = 13;
        int minX = 7;
        int maxY = -17;
        int minY = -23;
        int rangeX = maxX - minX + 1;
        int rangeY = maxY - minY + 1;

        xSpeed = (int) (Math.random() * rangeX) + minX;
        ySpeed = (int) (Math.random() * rangeY) + minY;
    }

    //cambia direzione in base alla velocità
    //cambia de dirección según la velocidad
    protected void changeDirection() {
        if (xSpeed > 0 && ySpeed < 0) {
            reversexSpeed();
        } else if (xSpeed < 0 && ySpeed < 0) {
            reverseySpeed();
        } else if (xSpeed < 0 && ySpeed > 0) {
            reversexSpeed();
        } else if (xSpeed > 0 && ySpeed > 0) {
            reverseySpeed();
        }
    }

    //aumentare la velocità in base al livello
    //aumentar la velocidad según el nivel
    protected void increaseSpeed(int level) {
        xSpeed = xSpeed + (1 * level);
        ySpeed = ySpeed - (1 * level);
    }

    //cambia direzione a seconda del muro che ha toccato e della velocità
    //cambia de dirección según la pared que ha golpeado y la velocidad
    protected void changeDirection(String wall) {
        if (xSpeed > 0 && ySpeed < 0 && wall.equals("right")) {
            reversexSpeed();
        } else if (xSpeed > 0 && ySpeed < 0 && wall.equals("up")) {
            reverseySpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && wall.equals("up")) {
            reverseySpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && wall.equals("left")) {
            reversexSpeed();
        } else if (xSpeed < 0 && ySpeed > 0 && wall.equals("left")) {
            reversexSpeed();
        } else if (wall.equals("down")) { // <--- l'unico caso in cui colpisce il Paddle
            reverseySpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && wall.equals("right")) {
            reversexSpeed();
        }
    }

    //dice se la palla è vicina
    // dice si la pelota esta cerca
    private boolean isClosed(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        if ((Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow(ay - by, 2))) < 80) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 100) - bx, 2) + Math.pow(ay - by, 2))) < 60) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 150) - bx, 2) + Math.pow(ay - by, 2))) < 60) {
            return true;
        }
        return false;
    }

    //scopri se la palla è vicina a un mattone
    //averigua si la pelota está cerca de un ladrillo
    private boolean isClosedBrick(float ax, float ay, float bx, float by) {
        bx += 12;
        by += 11;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;
    }

    //se la palla urta con il paddle, cambia direzione
    //si la pelota golpea "the paddle", cambia de dirección
    protected void hitPaddle(float xPaddle, float yPaddle) {
        if (isClosed(xPaddle, yPaddle, getX(), getY())) changeDirection("down");
    }

    //se la palla entra in collisione con un mattone, cambia direzione
    //si la bola choca con un ladrillo, cambia de dirección
    protected boolean hitBrick(float xBrick, float yBrick) {
        if (isClosedBrick(xBrick, yBrick, getX(), getY())) {
            changeDirection();
            return true;
        } else return false;
    }

    // si muove alla velocità specificata
    // se mueve a la velocidad especificada
    protected void move() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public void reversexSpeed() {
        xSpeed = -xSpeed;
    }

    public void reverseySpeed() {
        ySpeed = -ySpeed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }
}
