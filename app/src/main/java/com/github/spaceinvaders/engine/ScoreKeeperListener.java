package com.github.spaceinvaders.engine;

public interface ScoreKeeperListener {
    void onUpdateScores(ScoreKeeper sender);
    void onUpdateLives(ScoreKeeper sender);
}
