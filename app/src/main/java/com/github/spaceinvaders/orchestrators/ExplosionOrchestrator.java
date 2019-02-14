package com.github.spaceinvaders.orchestrators;

import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.models.Explosion;

import java.util.ArrayList;
import java.util.List;

public class ExplosionOrchestrator implements Freeable {

    private List<Explosion> _explosionObjectList;

    public void addExplosion(Explosion explosion) {
        if (explosion!=null) {
            _explosionObjectList.add(explosion);
        }
    }

    public void clear() {
        _explosionObjectList.clear();
    }

    public ExplosionOrchestrator() {
        _explosionObjectList = new ArrayList<Explosion>();
    }

    public void free() {
        _explosionObjectList.clear();
        _explosionObjectList = null;
    }

    private int getExplosionCount() {
        return _explosionObjectList.size();
    }

    private Explosion getItem(int index) {
        return _explosionObjectList.get(index);
    }

    public void doPaint() {
        Explosion explosion;
        if (_explosionObjectList.size() > 0) {
            for (int i = 0; i < _explosionObjectList.size(); i ++) {
                explosion = _explosionObjectList.get(i);
                if (explosion!=null) {
                    explosion.doPaint();
                }
            }
        }
    }

    private void setItem(int index, Explosion value) {
        _explosionObjectList.set(index, value);
    }

    public void takeOutTheTrash() {
        for (int i = _explosionObjectList.size() - 1; i >= 0; i--) {
            if (_explosionObjectList.get(i).isGarbage()) {
                _explosionObjectList.remove(i);
            }
        }
    }

    public void update() {
        Explosion explosion;

        if (_explosionObjectList.size() > 0) {
            for (int i = 0; i <_explosionObjectList.size(); i++){
                explosion = _explosionObjectList.get(i);
                if (explosion!=null) {
                    explosion.updateAnimation();
                }
            }
        }

        takeOutTheTrash();
    }

}
