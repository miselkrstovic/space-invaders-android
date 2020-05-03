package com.github.spaceinvaders.orchestrators;

import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.models.Bunker;
import com.github.spaceinvaders.models.Model;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BunkerOrchestrator implements Orchestratable, Freeable {

    private final int BUNKER_COUNT = 4;

    private List<Bunker> _map;
    private ExplosionOrchestrator _explosionOrchestrator;

    private boolean _initialized = false;

    public BunkerOrchestrator() {
        _map = new ArrayList<>(BUNKER_COUNT);
        _map.add(null);
        _map.add(null);
        _map.add(null);
        _map.add(null);
    }

    public void free() {
        for (int i = 0; i < _map.size(); i++) {
            if (_map.get(i) != null) {
                _map.set(i, null);
            }
        }
    }

    public void init() {
        // Deferred to lazy initialization to get proper canvas values
    }

    private void lazyInit() {
        int segment = Utilities.floorDiv(CanvasHelper.getWidth(), BUNKER_COUNT);

        for (int x = 0; x < _map.size(); x++) {
            if (_map.get(x) != null) {
                Bunker bunker = _map.get(x); // We are reusing the previous bunker object
                bunker.getPicture().setPenColor(Utilities.setAlpha(Model.PIXEL_COLOR_ON, 0xFF));
                bunker.getPicture().setFillRect(0, 0, _map.get(x).getWidth(), _map.get(x).getHeight(), Utilities.setAlpha(Model.PIXEL_COLOR_ON, 0xFF));
                _map.get(x).reset();
            } else {
                Bunker bunker = new Bunker(this, _explosionOrchestrator);
                _map.set(x, bunker);
            }

            _map.get(x).setLeft(Utilities.floorDiv((segment - _map.get(x).getWidth()), 2) + segment * x);
            _map.get(x).setTop(Utilities.floorDiv(CanvasHelper.getHeight() * 69, 100));
        }

        _initialized = true;
    }

    public void update() {
        // Do nothing
    }

    public void clear() {
        // Do nothing
    }

    private int getBunkerCount() {
        return _map.size();
    }

    public ExplosionOrchestrator getExplosionOrchestrator() {
        return _explosionOrchestrator;
    }

    public void setExplosionOrchestrator(ExplosionOrchestrator orchestrator) {
        _explosionOrchestrator = orchestrator;
    }

    public List<Bunker> getMap() {
        return _map;
    }

    public void setMap(List<Bunker> map) {
        _map = map;
    }

    public Bunker getMapItem(int i) {
        return _map.get(i);
    }

    public void batchPaint() {
        if (!_initialized) lazyInit();

        for (int i = 0; i < _map.size() ; i++) {
            if (_map.get(i) != null) {
                _map.get(i).paint();
            }
        }
    }

}
