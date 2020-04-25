package com.github.spaceinvaders.orchestrators;

import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.engine.HoleListener;
import com.github.spaceinvaders.models.Missile;

import java.util.ArrayList;
import java.util.List;

public class MissileOrchestrator implements BatchPaintable, Orchestratable, Freeable {

    private List<Missile> _alienObjectList;
    private List<Missile> _playerObjectList;

    private HoleListener listener;

    public MissileOrchestrator(HoleListener listener) {
        _alienObjectList = new ArrayList<Missile>();
        _playerObjectList = new ArrayList<Missile>();

        this.listener = listener;
    }

    public void free() {
        _alienObjectList.clear();
        _alienObjectList = null;
        _playerObjectList.clear();
        _playerObjectList = null;
    }

    public void init() {
        // Do nothing
    }

    public void update() {
        Missile missile;

        if (_alienObjectList.size() > 0) {
            for (int i = 0; i < _alienObjectList.size(); i++) {
                missile = _alienObjectList.get(i);
                missile.updateMotion();
            }
        }

        if (_playerObjectList.size() > 0) {
            for (int i = 0; i < _playerObjectList.size(); i++) {
                missile = _playerObjectList.get(i);
                missile.updateMotion();
            }
        }

        takeOutTheTrash();
    }

    public void takeOutTheTrash() {
        for (int i = _alienObjectList.size() - 1; i >= 0; i--) {
            if (_alienObjectList.get(i).isGarbage()) {
                _alienObjectList.remove(i);
            }
        }

        for (int i = _playerObjectList.size() - 1; i >= 0; i--) {
            if (_playerObjectList.get(i).isGarbage()) {
                _playerObjectList.remove(i);
            }
        }
    }

    public void clear() {
        _alienObjectList.clear();
        _playerObjectList.clear();
    }

    public void addMissile(Missile missile) {
        switch (missile.getMissileType()) {
            case ALIEN:
                _alienObjectList.add(missile);
                break;
            case PLAYER:
                _playerObjectList.add(missile);
                break;
        }
    }

    public int getAlienMissileCount() {
        return _alienObjectList.size();
    }

    public int getPlayerMissileCount() {
        return _playerObjectList.size();
    }

    public void registerHole(int position) {
        if (listener != null) listener.updateHole(this, position, 0);
    }

    public List<Missile> getPlayerObjects() {
        return _playerObjectList;
    }

    public List<Missile> getAlienObjects() {
        return _alienObjectList;
    }

    private Missile getItem(int index) {
        return _alienObjectList.get(index);
    }

    private void setItem(int index, Missile value) {
        _alienObjectList.set(index, value);
    }

    public void batchPaint() {
        if (_alienObjectList.size() > 0) {
            for (int i = 0; i < _alienObjectList.size(); i++) {
                Missile missile = _alienObjectList.get(i);
                missile.paint();
            }
        }

        if (_playerObjectList.size() > 0) {
            for (int i = 0; i < _playerObjectList.size(); i++) {
                Missile missile = _playerObjectList.get(i);
                missile.paint();
            }
        }
    }

}
