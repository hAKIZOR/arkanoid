package com.caserteam.arkanoid.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import com.caserteam.arkanoid.R;
import static com.caserteam.arkanoid.AppContractClass.*;

public class BallEditor extends View{


    private Bitmap ballSkin;
    protected float xSpeed;
    protected float ySpeed;
    private float x;
    private float y;

    public BallEditor(Context context, float x, float y, int typeSkin) {
        super(context);
        this.x = x;
        this.y = y;
        skin(typeSkin);

    }
    public void skin(int typeSkin) {
        switch (typeSkin) {
            case BALL_SKIN1:
                ballSkin = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
                break;
        }
    }

    public Bitmap getBallSkin() {
        return ballSkin;
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

}
