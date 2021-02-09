package com.caserteam.arkanoid.editor.editor_module;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;

import com.caserteam.arkanoid.editor.BallEditor;
import com.caserteam.arkanoid.editor.BrickEditor;
import com.caserteam.arkanoid.editor.PaddleEditor;
import com.caserteam.arkanoid.R;
import static com.caserteam.arkanoid.AppContractClass.*;

public class EditorViewLandScape extends Editor {
    private static final int COLUMNS_GRID = 15;
    private static final int ROWS_GRID = 6;

    private Activity activity;

    public EditorViewLandScape(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        actionBarHeight = getHeightOfActionBar();
        float [] initialPosition = { (float) (size.x/2), (float) (size.y/1.3),
                (float)(size.x/1.9),(float) (size.y/1.4),
                (float)(size.x/2),(float) (size.y/2) };

        paddleEditor = new PaddleEditor(context,0,0, PADDLE_SKIN1);
        paddleEditor.setX(initialPosition[0]);
        paddleEditor.setY(initialPosition[1]);

        ballEditor = new BallEditor(context,0,0, BALL_SKIN1);
        ballEditor.setX(initialPosition[2]);
        ballEditor.setY(initialPosition[3]);

        brickEditorTemp = new BrickEditor(context,0,0,1);
        brickEditorTemp.setX(initialPosition[4]);
        brickEditorTemp.setY(initialPosition[5]);
        brickEditorTemp.setBrick(invisibleSkin);

        paddleWidth = (size.x/12);
        paddleHeight =(size.y/60);

        float x = size.x;

        nColumnGrid = COLUMNS_GRID;
        nRowGrid = ROWS_GRID;

        brickWidth = ((size.x)/COLUMNS_GRID)-30;
        brickHeight =(float)(((size.y-(size.y/1.5))/ROWS_GRID));


        paddingTopGrid = 0;
        paddingLeftGrid=(float) (size.x*0.10);

        generateGrid(context,COLUMNS_GRID,ROWS_GRID,brickWidth,brickHeight,paddingTopGrid,paddingLeftGrid);
    }

    public EditorViewLandScape(Context context, Activity activity, String structure) {
        super(context);
        this.activity = activity;
        actionBarHeight = getHeightOfActionBar();
        float [] initialPosition = { (float) (size.x/2), (float) (size.y/1.3),
                (float)(size.x/1.9),(float) (size.y/1.4),
                (float)(size.x/2),(float) (size.y/2) };

        paddleEditor = new PaddleEditor(context,0,0, PADDLE_SKIN1);
        paddleEditor.setX(initialPosition[0]);
        paddleEditor.setY(initialPosition[1]);

        ballEditor = new BallEditor(context,0,0, BALL_SKIN1);
        ballEditor.setX(initialPosition[2]);
        ballEditor.setY(initialPosition[3]);

        brickEditorTemp = new BrickEditor(context,0,0,1);
        brickEditorTemp.setX(initialPosition[4]);
        brickEditorTemp.setY(initialPosition[5]);
        brickEditorTemp.setBrick(invisibleSkin);

        nColumnGrid = COLUMNS_GRID;
        nRowGrid = ROWS_GRID;

        brickWidth = ((size.x)/COLUMNS_GRID)-30;
        brickHeight =(float)(((size.y-(size.y/1.5))/ROWS_GRID));

        paddingTopGrid = 0;
        paddingLeftGrid=(float) (size.x*0.10);

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

        // crea uno sfondo solo una volta
        Rect dest = new Rect(0, 0, screen_width, screen_height);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(background, null, dest, paint);

        canvas.drawBitmap(ballEditor.getBallSkin(), ballEditor.getX(), ballEditor.getY(), paint);

        // disegna la barra

        r = new RectF(paddleEditor.getX(), paddleEditor.getY(), paddleEditor.getX() + paddleWidth, paddleEditor.getY() + paddleHeight);
        canvas.drawBitmap(paddleEditor.getPaddleSkin(), null, r, paint);


        r = new RectF(brickEditorTemp.getX(), brickEditorTemp.getY(), brickEditorTemp.getX() + brickWidth, brickEditorTemp.getY()+ brickHeight);
        canvas.drawBitmap(brickEditorTemp.getBrick(), null, r, paint);


        if(selectionBrick) {
            //aggiungo bordo nero
            Bitmap brickUpdate = brickEditorArrayList.get(brickSelectedIndex).getBrick();
            brickEditorArrayList.get(brickSelectedIndex).setBrick(addBorder(brickUpdate,10));
            selectionBrick = false;
        } else if(!keepselectionBrick){
            //tolgo bordo nero
            brickEditorArrayList.get(brickSelectedIndex).skin(brickEditorSelected.getIdSkinById(brickEditorSelected.getBrick()));
            brickSelectedIndex = -1;
            keepselectionBrick = true;
        }





        for (int i = 0; i < brickEditorArrayList.size(); i++) {
            BrickEditor b = brickEditorArrayList.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() +brickWidth , b.getY() +brickHeight);
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }



    }
    @Override
    public void setPaddingTopGrid(float paddingTopGrid){
        this.paddingTopGrid = paddingTopGrid;
    }


}