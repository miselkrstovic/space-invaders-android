package com.github.spaceinvaders.engine;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.utils.Utilities;

public class ScoreKeeper implements Freeable {

    private int _playerScore1;
    private int _playerScore2;
    private int _highScore;
    private int _upLives;

    private ScoreKeeperListener _listener;

    private static final ScoreKeeper INSTANCE = new ScoreKeeper();

    private ScoreKeeper() {
        _playerScore1 = 0;
        _playerScore2 = 0;
        _highScore = 0;
        _upLives = 0;
    }

    public static ScoreKeeper getInstance(ScoreKeeperListener listener) {
        if (listener != null) {
            INSTANCE._listener = listener;
        }
        return INSTANCE;
    }

    public void clear() {
      _playerScore1 = 0;
      _playerScore2 = 0;

      if (_listener != null) {
          _listener.onUpdateScores(this);
      }
    }

    public void clearAll() {
      _playerScore1 = 0;
      _playerScore2 = 0;
      _highScore = 0;

      if (_listener != null) {
          _listener.onUpdateScores(this);
      }
    }

    public String formatScore(int value) {
    	return formatScore(value, 4);
    }
    
    public String formatScore(int value, int minWidth) {
      String result = Integer.toString(value);
      if (minWidth>=4) {
        if (result.length() < minWidth) {

          result = Utilities.stringOfChar('0', minWidth-result.length()) + result;
        }
      }
      return result;
	}

    public int getPlayerScore1() {
        return _playerScore1;
    }

    public int getPlayerScore2() {
        return _playerScore2;
    }

    public int getHighScore() {
        return _highScore;
    }

    public void setPlayerScore1(int Value) {
      if (Value>=0) {
        _playerScore1 = Value;
        _highScore = Math.max(_highScore, _playerScore1);

        if (_listener != null) {
            _listener.onUpdateScores(this);
        }

        if ((Value / GameSettings.SCORE_MARK_FOR_NEW_LIFE) > _upLives) {
          if (_listener != null) {
              _listener.onUpdateLives(this);
          }
          _upLives = Value / GameSettings.SCORE_MARK_FOR_NEW_LIFE;
        }
      }
    }

    public void setPlayerScore2(int Value) {
      if (Value>=0) {
        _playerScore2 = Value;
        _highScore = Math.max(_highScore, _playerScore2);

        if (_listener != null) {
            _listener.onUpdateScores(this);
        }

        if ((Value / GameSettings.SCORE_MARK_FOR_NEW_LIFE) > _upLives) {
          if (_listener != null) {
              _listener.onUpdateLives(this);
          }
          _upLives = Value / GameSettings.SCORE_MARK_FOR_NEW_LIFE;
        }        
      }
    }

    public void free() {
        _listener = null;
    }

}















