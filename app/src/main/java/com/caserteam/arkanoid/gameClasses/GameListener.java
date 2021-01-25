package com.caserteam.arkanoid.gameClasses;

public interface GameListener {
    void  onGameOver();
    void  onWinLevel();
    void  onWinGame();
    void  onPauseGame(boolean pause);
    void onResumeGame();
}
