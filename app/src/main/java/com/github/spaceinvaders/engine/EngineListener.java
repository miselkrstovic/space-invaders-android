package com.github.spaceinvaders.engine;

public interface EngineListener {
    void onStartGame(Engine sender);
    void onPauseGame(Engine sender);
    void onStopGame(Engine sender);
    void onShutdown(Engine sender);
    void onUpdateScores(ScoreKeeper sender);
    void onUpdateLives(ScoreKeeper sender);
    void onUpdateGroundHole(Engine sender);
    void onShowMessage(String message);
}
