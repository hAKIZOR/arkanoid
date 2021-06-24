package com.caserteam.arkanoid.gameClasses;

import java.io.IOException;

public interface GameListener {
    void  onGameOver();
    void  onWinGame();
    void  onPauseGame(boolean pause);
    void onResumeGame();
}
