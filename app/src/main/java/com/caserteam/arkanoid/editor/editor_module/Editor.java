package com.caserteam.arkanoid.editor.editor_module;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import androidx.core.view.GestureDetectorCompat;
import com.caserteam.arkanoid.editor.Ball;
import com.caserteam.arkanoid.editor.Brick;
import com.caserteam.arkanoid.editor.FloatingActionButtonMinus;
import com.caserteam.arkanoid.editor.FloatingActionButtonPlus;
import com.caserteam.arkanoid.editor.Paddle;
import com.caserteam.arkanoid.editor.PromptUtils;
import com.caserteam.arkanoid.R;

public abstract class Editor extends View implements
        GestureDetector.OnGestureListener {

    protected static final int DIMENSION_GRID = 90;
    protected int nRowGrid;
    protected int nColumnGrid;

    protected float actionBarHeight;

    protected View fabButtonPlus;
    protected View fabButtonMinus;
    protected PromptUtils promptUtils;
    protected Bitmap background;
    protected Paint paint;
    protected Display display;
    protected Point size;
    protected Context context;
    protected ArrayList<Brick> brickArrayList;

    protected GestureDetectorCompat gestureDetector;
    protected RectF r;
    protected Paddle paddle;
    protected Ball ball;

    protected Brick brickTemp = null;

    protected int brickSelectedIndex = -1;
    protected Brick brickSelected;

    protected float brickWidth;
    protected float brickHeight;

    protected float paddleWidth;
    protected float paddleHeight;

    protected boolean mIsScrolling = false;
    protected boolean selectionBrick = false;
    protected boolean keepselectionBrick = true;
    protected boolean switchSelection = false;

    protected float paddingLeftGrid;
    protected float paddingTopGrid;

    Bitmap edgeSkin =  BitmapFactory.decodeResource(getResources(), R.drawable.brick_editor);
    Bitmap invisibleSkin =  BitmapFactory.decodeResource(getResources(), R.drawable.brick_inv);

    protected Editor(Context context) {
        super(context);

        this.context = context;
        promptUtils = PromptUtils.initialize(context);
        setBackground(context);
        paint = new Paint();
        brickSelected = new Brick(context,0,0,invisibleSkin);
        brickArrayList = new ArrayList<>(DIMENSION_GRID);
        gestureDetector = new GestureDetectorCompat(context,this);


    }


    protected void generateGridFromStructure(Context context, int columnsGrid, int rowsGrid, float brickWidth, float brickHeight, float paddingTopGrid, float paddingLeftGrid,String structure) {
        String [] list = structure.split(",");
        int indexList = 0;
        for (int i = 0; i < rowsGrid; i++) {
            for (int j = 0; j < columnsGrid; j++) {

                if(list[indexList].equals("1")){
                    Log.d("EditorActivity","passo");
                    Log.d("edge--->",String.valueOf(indexList));
                    brickArrayList.add(new Brick(context,  brickWidth * j + paddingLeftGrid , brickHeight  * i + paddingTopGrid , edgeSkin));
                } else {
                    Log.d("noedge--->",String.valueOf(indexList));
                    brickArrayList.add(new Brick(context,  brickWidth * j + paddingLeftGrid , brickHeight  * i + paddingTopGrid , Integer.parseInt(list[indexList])));
                }
                indexList ++;
            }
        }

    }

    protected void generateGrid(Context context, int columns, int row, float brickWidth, float brickHeight,float paddingTop, float paddingLeft) {
        //si moltiplicano gli indici i e j con i rispettivi valori  brickHeight,brickWidth per garantire un degno distanziamento
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < columns; j++) {
                brickArrayList.add(new Brick(context,  brickWidth * j + paddingLeft , brickHeight  * i + paddingTop , edgeSkin));
            }
        }

    }


    public  void replaceFromGridBrickToTempBrick() {
        int indexSlotSelected;

        if(!(brickTemp.getBrick().sameAs(invisibleSkin))) {

            indexSlotSelected = getIndexOfSlotSelected(brickTemp.getX(),brickTemp.getY());
            brickArrayList.get(indexSlotSelected).setBrick(brickTemp.getBrick());
            if(existPreviousOneSelection()) {
                brickArrayList.get(brickSelectedIndex).setBrick(brickSelected.getBrick());
                brickSelected.setBrick(brickTemp.getBrick());
                brickSelectedIndex = indexSlotSelected;
                setParamsSelection(true,true);
                switchSelection = false;
            } else {
                setParamsSelection(false,true);
                switchSelection = false;
            }

            brickTemp.setBrick(invisibleSkin);

        }

    }

    private void selectBrick(float xSelected, float ySelected) {
        Log.d("SelectBrick----->","REFRESH");


            if(isSelectedGrid(ySelected)){

                int indexSlotSelected = getIndexOfSlotSelected(xSelected,ySelected);
                Log.d("SelectBrick----->",String.valueOf(indexSlotSelected));
                if(switchSelection) switchSelection = false; else switchSelection = true;
                if(existPreviousOneSelection()) {
                    if(iSlotSelectedEmpty(indexSlotSelected)) {
                        promptUtils.showMessage(PromptUtils.SELECTION_MESSAGE_BRICK);
                        setParamsSelection(false,false);
                        switchSelection = false;
                    } else if(!isBrickSelectedTheSameOf(indexSlotSelected)) {

                        brickArrayList.get(brickSelectedIndex).setBrick(brickSelected.getBrick());
                        validateSelectionTo(indexSlotSelected);

                    } else if(switchSelection) {
                        setParamsSelection(false,false);
                    } else {
                        setParamsSelection(true,true);
                    }

                } else {

                    if(!iSlotSelectedEmpty(indexSlotSelected)) {
                        validateSelectionTo(indexSlotSelected);
                    } else {
                        promptUtils.showMessage(PromptUtils.SELECTION_MESSAGE_BRICK);
                    }
                }


            } else {
                promptUtils.showMessage(PromptUtils.SELECTION_MESSAGE_BRICK);
                if(existPreviousOneSelection()){
                    setParamsSelection(false,false);
                } else {
                    setParamsSelection(false,true);
                }

            }


        }
    private boolean existPreviousOneSelection(){
        return brickSelectedIndex > -1;
    }

    private boolean iSlotSelectedEmpty(int slot){
        return brickArrayList.get(slot).getBrick().sameAs(edgeSkin);
    }
    private boolean isBrickSelectedTheSameOf(int slot){
        return brickSelectedIndex == slot;
    }

    private boolean isSelectedGrid(float y) {
        // controllo se è stata selezionata la parte dello schermo relativa alla griglia
        return (y <= (brickHeight * (nRowGrid)) );
    }


    private void setParamsSelection(boolean newValueSelectionBrick,boolean newValueKeepselectionBrick) {
        selectionBrick = newValueSelectionBrick;
        keepselectionBrick = newValueKeepselectionBrick;
    }

    private void validateSelectionTo(int indexNewBrickToSelect) {
        brickSelected.setBrick(brickArrayList.get(indexNewBrickToSelect).getBrick());
        brickSelectedIndex = indexNewBrickToSelect;
        setParamsSelection(true,true);

    }

    private int getIndexOfSlotSelected(float xSelected,float ySelected) {
        Log.d("onTap","passo");
        Brick brick;
        double minDistance = Math.sqrt(Math.pow(brickArrayList.get(0).getX() - xSelected,2) + Math.pow(brickArrayList.get(0).getY() - ySelected,2));
        double distance;
        int indexMin = 0;
        for(int i = 1; i< brickArrayList.size(); i++){
            brick = brickArrayList.get(i);
            distance = Math.sqrt(Math.pow(brick.getX() - xSelected,2) + Math.pow(brick.getY() - ySelected,2));
            if(distance < minDistance){
                minDistance = distance;
                indexMin = i;
            }
        }
        return indexMin;
    }

    // impostare lo sfondo
    private void setBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.background_editor));
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public Bitmap addBorder(Bitmap bmp, int borderSize) {
        Log.d("addBorder"," passo");
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }


    public void setPaddle(int skinPaddle) {
        paddle.skin(skinPaddle);
    }
    public void setBall(int skinBall) {
        ball.skin(skinBall);
    }

    public void createbrick(int i) {

        brickTemp.skin(i);
        brickTemp.setX(size.x/2-(brickWidth/2));
        brickTemp.setY(size.y/2-(brickHeight/2));
    }

    public void deleteBrickSelected() {

        if(brickSelectedIndex != -1) {
            brickArrayList.get(brickSelectedIndex).setBrick(edgeSkin);
            brickSelectedIndex = -1;
            selectionBrick = false;
            brickSelected.setBrick(invisibleSkin);
            promptUtils.showMessage(PromptUtils.DELETE_MESSAGE_BRICK);
        } else {
            promptUtils.showMessage(PromptUtils.DELETE_MESSAGE_FAILURE);
        }

    }

    public void clearBricks() {
        if(GridHasNoBricks()){
            promptUtils.showMessage(PromptUtils.DELETE_ALL_MESSAGE_FAILURE);
        } else {
            promptUtils.showMessage(PromptUtils.DELETE_ALL_MESSAGE_BRICK);
        }
        for(Brick brick: brickArrayList) { brick.setBrick(edgeSkin); }
        brickSelectedIndex = -1;
        selectionBrick = false;



    }

    private boolean GridHasNoBricks() {
        boolean f = true;
        for(Brick brick: brickArrayList) {
            if(brick.getIdSkinById(brick.getBrick()) != 0){
                    return false;
            }
        }

        return  f;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {


        selectBrick(motionEvent.getX()-(brickWidth/2),motionEvent.getY() - (float) (actionBarHeight + (brickHeight/2)));
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) { }
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) { return false; }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        brickTemp.setX(motionEvent1.getX() - (float) (brickWidth/2));
        brickTemp.setY(motionEvent1.getY() - (float) (actionBarHeight + (brickHeight/2)));
        mIsScrolling = true;
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {}

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) { return false; }

    public  GestureDetectorCompat getGestureDetector() { return  gestureDetector; }

    public boolean ismIsScrolling() { return mIsScrolling; }

    public void setmIsScrolling(boolean mIsScrolling) { this.mIsScrolling = mIsScrolling; }

    public void buildActionButtonPlus(Activity activity){

        fabButtonPlus = new FloatingActionButtonPlus.Builder(activity)
                .withDrawable(getResources().getDrawable(R.drawable.plus,null))
                .withButtonColor(R.color.design_default_color_secondary)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 0, 0)
                .create();

    }
    public void buildActionButtonMinus(Activity activity){

        fabButtonMinus = new FloatingActionButtonMinus.Builder(activity)
                .withDrawable(getResources().getDrawable(R.drawable.minus,null))
                .withButtonColor(R.color.design_default_color_secondary)
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(0, 0, 0, 0)
                .create();

    }
    public float getActionBarHeight() {
        return actionBarHeight;
    }

    public void setActionBarHeight(float actionBarHeight) {
        this.actionBarHeight = actionBarHeight;
    }

    public abstract void setPaddingTopGrid(float paddingTopGrid);
    public String convertListBrickToString(){
        if(existPreviousOneSelection()){
            brickArrayList.get(brickSelectedIndex).setBrick(brickSelected.getBrick());
        }
        String result = null;
        if (!GridHasNoBricks()){
            result = new String();
            for(Brick b :brickArrayList){
                if(b.getBrick().sameAs(edgeSkin)){
                    result = result + String.valueOf(1)+",";
                } else {
                    result = result + String.valueOf(b.getIdSkinById(b.getBrick()))+",";
                }
            }
        }
        return result;
    }

    public PromptUtils getPromptUtils() {
        return promptUtils;
    }

    public void setPromptUtils(PromptUtils promptUtils) {
        this.promptUtils = promptUtils;
    }
}
