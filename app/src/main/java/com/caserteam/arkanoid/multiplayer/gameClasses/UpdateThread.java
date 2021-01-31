package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.os.Handler;

public class UpdateThread extends Thread {
    Handler updateHandler;
    public UpdateThread(Handler uh) {
        super();
        updateHandler = uh;
    }

    public void run() {
        while (true) {
            try {
                sleep(30);
            } catch (Exception ex) {
            }
            updateHandler.sendEmptyMessage(0);
        }
    }

}

