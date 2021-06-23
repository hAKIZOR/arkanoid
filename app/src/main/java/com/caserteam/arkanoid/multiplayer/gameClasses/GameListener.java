package com.caserteam.arkanoid.multiplayer.gameClasses;


public interface GameListener {
    void  onGameOver();
    void  onWinGame();
    void  onExitGame(boolean role_close);
    void  onPauseGame(boolean pause,boolean role_pause);
    void  onResumeGame();
}
