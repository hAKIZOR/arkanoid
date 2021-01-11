package com.caserteam.arkanoid.editor.editor_module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;

import com.caserteam.arkanoid.editor.Ball;
import com.caserteam.arkanoid.editor.Brick;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.editor.Paddle;
import com.caserteam.arkanoid.R;

public class EditorViewPortrait extends Editor{
    private static final int ROWS_GRID = 10;
    private static final int COLUMNS_GRID = 9;

    private Activity activity;

   public EditorViewPortrait(Context context, Activity activity){
        super(context);
       float [] initialPosition = { (float) (size.x/2.4), (float) (size.y/1.2),
               (float)(size.x/2),(float) (size.y/1.25),
               (float)(size.y/2),(float) (size.x/2) };

       this.activity = activity;
       actionBarHeight = getHeightOfActionBar();
       paddle = new Paddle(context,0,0,Paddle.PADDLE_SKIN1);
       paddle.setX(initialPosition[0]);
       paddle.setY(initialPosition[1]);

       ball = new Ball(context,0,0, Ball.BALL_SKIN1);
       ball.setX(initialPosition[2]);
       ball.setY(initialPosition[3]);

       brickTemp = new Brick(context,0,0,1);
       brickTemp.setX(initialPosition[4]);
       brickTemp.setY(initialPosition[5]);
       brickTemp.setBrick(invisibleSkin);

       paddleWidth = (size.x/5);
       paddleHeight =(size.y/80);

       //set dei parametri da usare nel generateGrid
       nColumnGrid = COLUMNS_GRID;
       nRowGrid = ROWS_GRID;

       brickWidth = (size.x)/COLUMNS_GRID;
       brickHeight =(float)((size.y-(size.y/1.7))/ROWS_GRID);

       paddingTopGrid = 0;
       paddingLeftGrid = 0;
       generateGrid(context,COLUMNS_GRID,ROWS_GRID,brickWidth,brickHeight,paddingTopGrid,paddingLeftGrid);
    }

    public EditorViewPortrait(Context context, Activity activity, String structure) {
        super(context);
        this.activity = activity;
        float [] initialPosition = { (float) (size.x/2.4), (float) (size.y/1.2),
                (float)(size.x/2),(float) (size.y/1.25),
                (float)(size.y/2),(float) (size.x/2) };

        this.activity = activity;
        actionBarHeight = getHeightOfActionBar();
        paddle = new Paddle(context,0,0,Paddle.PADDLE_SKIN1);
        paddle.setX(initialPosition[0]);
        paddle.setY(initialPosition[1]);

        ball = new Ball(context,0,0, Ball.BALL_SKIN1);
        ball.setX(initialPosition[2]);
        ball.setY(initialPosition[3]);

        brickTemp = new Brick(context,0,0,1);
        brickTemp.setX(initialPosition[4]);
        brickTemp.setY(initialPosition[5]);
        brickTemp.setBrick(invisibleSkin);

        paddleWidth = (size.x/5);
        paddleHeight =(size.y/80);

        //set dei parametri da usare nel generateGrid
        nColumnGrid = COLUMNS_GRID;
        nRowGrid = ROWS_GRID;

        brickWidth = (size.x)/COLUMNS_GRID;
        brickHeight =(float)((size.y-(size.y/1.7))/ROWS_GRID);

        paddingTopGrid = 0;
        paddingLeftGrid = 0;

        generateGridFromStructure(context,COLUMNS_GRID,ROWS_GRID,brickWidth,brickHeight,paddingTopGrid,paddingLeftGrid,structure);
    }


    private float getHeightOfActionBar() {
        TypedValue tv = new TypedValue();
        float actionBarHeight=0;
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

            Log.d("Height" ,String.valueOf(actionBarHeight + getResources().getDimensionPixelSize(R.dimen.status_bar)));
        }
        return actionBarHeight + getResources().getDimensionPixelSize(R.dimen.status_bar);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(background, 0, 0, paint);

        canvas.drawBitmap(ball.getBallSkin(), ball.getX(), ball.getY(), paint);

        // disegna la barra

        r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + paddleWidth, paddle.getY() + paddleHeight);
        canvas.drawBitmap(paddle.getPaddleSkin(), null, r, paint);


        r = new RectF(brickTemp.getX(), brickTemp.getY(), brickTemp.getX() + brickWidth, brickTemp.getY()+ brickHeight);
        canvas.drawBitmap(brickTemp.getBrick(), null, r, paint);

        if(selectionBrick) {
            //aggiungo bordo nero
            Bitmap brickUpdate = brickArrayList.get(brickSelectedIndex).getBrick();
            brickArrayList.get(brickSelectedIndex).setBrick(addBorder(brickUpdate,10));
            selectionBrick = false;
        } else if(!keepselectionBrick){
            //tolgo bordo nero
            brickArrayList.get(brickSelectedIndex).skin(brickSelected.getIdSkinById(brickSelected.getBrick()));
            brickSelectedIndex = -1;
            keepselectionBrick = true;
        }


        for (int i = 0; i < brickArrayList.size(); i++) {
            Brick b = brickArrayList.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() +brickWidth , b.getY() +brickHeight);
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }


    }
    @Override
    public void setPaddingTopGrid(float paddingTopGrid) {
        this.paddingTopGrid = paddingTopGrid;
    }
}
