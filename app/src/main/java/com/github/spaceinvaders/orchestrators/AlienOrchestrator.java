package com.github.spaceinvaders.orchestrators;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.compatibility.Point32;
import com.github.spaceinvaders.engine.Engine;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.models.Alien;
import com.github.spaceinvaders.utils.Utilities;

import java.math.BigDecimal;

import static com.github.spaceinvaders.enums.AlienDirection.LEFT;
import static com.github.spaceinvaders.enums.AlienDirection.RIGHT;
import static com.github.spaceinvaders.enums.AlienSpecie.ALPHA;
import static com.github.spaceinvaders.enums.AlienSpecie.BETA;
import static com.github.spaceinvaders.enums.AlienSpecie.GAMMA;

public class AlienOrchestrator implements BatchPaintable, Orchestratable, Freeable {

    private int _thresholdCount;
    private Alien[][] _map2D;
    private MissileOrchestrator _missileOrchestrator;
    private ExplosionOrchestrator _explosionOrchestrator;
    private double _accelerateX;
    private double _accelerateY;
    private boolean _flipFlop;
    private Engine _engine;

    public AlienOrchestrator(Engine engine) {
        // Nothing here yet
        _thresholdCount = 0;
        _engine = engine;

        _map2D = new Alien[GameSettings.ALIEN_MESH_WIDTH + 1][GameSettings.ALIEN_MESH_HEIGHT + 1];
    }

    public void free() {
        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (_map2D[x][y] != null) {
                    _map2D[x][y] = null;
                }
            }
        }
    }

    public void init() {
        resetAcceleration();
        _thresholdCount = 0;

        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (_map2D[x][y] != null) {
                    // We are reusing the previous alien object
                    _map2D[x][y].setVisibility(true);
                } else {
                    _map2D[x][y] = new Alien();
                    _map2D[x][y].setMissileOrchestrator(_missileOrchestrator);
                    _map2D[x][y].setAlienOrchestrator(this);
                    _map2D[x][y].setExplosionOrchestrator(_explosionOrchestrator);
                    switch (y) {
                        case 4:
                        case 5:
                            _map2D[x][y].setSpecie(ALPHA);
                            break;
                        case 2:
                        case 3:
                            _map2D[x][y].setSpecie(BETA);
                            break;
                        default:
                            _map2D[x][y].setSpecie(GAMMA);
                    }
                }
                _map2D[x][y].setLeft(x * 32);
                _map2D[x][y].setTop((int) (y * 32)); // Initial height of the alien mesh
            }
        }
    }

    public void update() {
        BigDecimal distance;
        Point32 laserCanon;

        _thresholdCount = _thresholdCount + 1;
        if (_thresholdCount > 5) _thresholdCount = 0;

        _updateFlipFlop();

        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (_map2D[x][y] != null) {
                    if (_map2D[x][y].isVisible()) {
                        if ((_thresholdCount % 6) == 0) {
                            _map2D[x][y].updateMotion();
                        }
                    }
                }
            }
        }

        BigDecimal memDist = BigDecimal.valueOf(Double.MAX_VALUE);
        Point32 memAlien = new Point32(0, 0);

        if (_missileOrchestrator.getAlienMissileCount() < GameSettings.MAX_ALIEN_MISSILE_COUNT) {
            if (_flipFlop) {
                _map2D[Utilities.random(GameSettings.ALIEN_MESH_WIDTH) + 1][Utilities.random(GameSettings.ALIEN_MESH_HEIGHT) + 1].
                        shootMissile();
            } else {
                laserCanon = _engine.getLaserCannonPosition();
                for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
                    for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                        if (_map2D[x][y] != null) {
                            if (_map2D[x][y].isVisible()) {
                                distance = new BigDecimal(Math.floor(Math.sqrt(Math.pow((_map2D[x][y].getLeft() - laserCanon.getX()),2)+Math.pow((_map2D[x][y].
                                        getTop() - laserCanon.getY()),2))));
                                if (distance.compareTo(memDist)==-1) {
                                    memDist = distance;
                                    memAlien = new Point32(x, y);
                                }
                            }
                        }
                    }
                }

                if (!memAlien.equals(new Point32(0, 0))) {
                    _map2D[memAlien.getX()][memAlien.getY()].shootMissile();
                }
            }
        }

        detectAnomalies();
    }

    public void _updateFlipFlop() {
        _flipFlop = !_flipFlop;
    }

    public void clear() {
        // Do nothing
    }

    public void accelerate() {
        _accelerateX = _accelerateX + GameSettings.X_AXIS_ACCELERATION;
        _accelerateY = _accelerateY + GameSettings.Y_AXIS_ACCELERATION;
    }

    public int getAlienCount() {
        int result = 0;
        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (_map2D[x][y]!=null){
                    if (_map2D[x][y].isVisible()) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public void resetAcceleration() {
        _accelerateX = 0;
        _accelerateY = 0;
    }

    public void detectAnomalies() {
        int leftYes = 0;
        int rightYes = 0;

        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (_map2D[x][y] != null && _map2D[x][y].isVisible()) {
                    switch (_map2D[x][y].getDirection()) {
                        case LEFT:
                            leftYes++;
                            break;
                        case RIGHT:
                            rightYes++;
                            break;
                    }
                }
            }
        }

        if (leftYes == 0) return;
        if (rightYes == 0) return;

        if (leftYes > rightYes) {
            for (int x=1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
                for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                    if (_map2D[x][y] != null) {
                        if (_map2D[x][y].isVisible()) {
                            _map2D[x][y].setDirection(RIGHT);
                            _map2D[x][y].descend();
                        }
                    }
                }
            }
        } else {
            for (int x=1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
                for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                    if (_map2D[x][y] != null) {
                        if (_map2D[x][y].isVisible()) {
                            _map2D[x][y].setDirection(LEFT);
                            _map2D[x][y].descend();
                        }
                    }
                }
            }
        }
    }

    public Alien getMap2D(int x, int y) {
        return _map2D[x][y];
    }

    public void setMissileOrchestrator(MissileOrchestrator missileOrchestrator) {
        this._missileOrchestrator = missileOrchestrator;
    }

    public void setExplosionOrchestrator(ExplosionOrchestrator explosionOrchestrator) {
        this._explosionOrchestrator = explosionOrchestrator;
    }

    public void batchPaint() {
        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++){
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++){
                if (_map2D[x][y]!=null){
                    if (_map2D[x][y].isVisible()) {
                        _map2D[x][y].paint();
                    }
                }
            }
        }
    }

}
