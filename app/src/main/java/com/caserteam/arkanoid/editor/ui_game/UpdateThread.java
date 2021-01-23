package com.caserteam.arkanoid.editor.ui_game;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class UpdateThread extends Thread implements GameSearched.GameSearchedListener{
    public Handler updateHandler;
    private boolean pause = false;
    public UpdateThread(Handler updateHandler) {
        super();
        this.updateHandler = updateHandler;
    }

    public void run(){

            while (!pause){
                try {
                    sleep(30);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateHandler.sendEmptyMessage(0);
            }


    }

    @Override
    public void onGameOver() {
        pause = true;
    }

    @Override
    public void onWinGame() {
        pause = true;
    }

    @Override
    public void onPauseGame() {
        pause = true;
    }

    @Override
    public void onResumeGame() {

    }
}