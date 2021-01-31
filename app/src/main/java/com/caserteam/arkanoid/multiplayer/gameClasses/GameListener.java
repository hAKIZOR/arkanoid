package com.caserteam.arkanoid.multiplayer.gameClasses;

import java.io.IOException;

public interface GameListener {
    void  onGameOver() throws IOException;
    void  onWinLevel() throws IOException;
    void  onWinGame();
    void  onPauseGame(boolean pause);
    void onResumeGame();
}
