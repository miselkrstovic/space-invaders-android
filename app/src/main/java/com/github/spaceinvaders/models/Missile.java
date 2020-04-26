package com.github.spaceinvaders.models;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.enums.MissileType;
import com.github.spaceinvaders.orchestrators.ExplosionOrchestrator;
import com.github.spaceinvaders.orchestrators.MissileOrchestrator;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

import static com.github.spaceinvaders.enums.ExplosionType.GROUND;
import static com.github.spaceinvaders.enums.ExplosionType.SKY;
import static com.github.spaceinvaders.enums.MissileType.PLAYER;

public class Missile extends Model implements Paintable {

    private static final int MISSILE_WIDTH = 2;
    private static final int MISSILE_HEIGHT = 8;

    private boolean _garbage;
    private boolean _drawing;

    public MissileType missileType = PLAYER;

    private ExplosionOrchestrator _explosionOrchestrator;
    private MissileOrchestrator _missileOrchestrator;

    private int _brushColor;
    private int _penColor;

    public Missile(MissileOrchestrator missileOrchestrator, ExplosionOrchestrator explosionOrchestrator) {
        setWidth(MISSILE_WIDTH);
        setHeight(MISSILE_HEIGHT);

        setBrushColor(PIXIL_COLOR_ON);
        setPenColor(PIXIL_COLOR_ON);

        _missileOrchestrator = missileOrchestrator;
        setExplosionOrchestrator(explosionOrchestrator);
    }

    public void garbage() {
        _garbage = true;
    }

    public void paint() {
        if (_garbage) return;

        boolean save = _drawing;
        _drawing = true;
        try {
            Rect32 boundsRect = getBoundsRect();
            CanvasHelper.drawRect(
                    boundsRect.getLeft(),
                    boundsRect.getTop(),
                    boundsRect.getRight(),
                    boundsRect.getBottom(),
                    CanvasHelper.createPaint()
            );
        } finally {
            _drawing = save;
        }
    }

    public void updateMotion() {
        Explosion explosion;
        switch (missileType) {
            case PLAYER:
                setTop(getTop() - GameSettings.PLAYER_MISSILE_PIXEL_SHIFT);
                if (getTop()<=0) {
                    if (_explosionOrchestrator!=null){
                        explosion = new Explosion(SKY, _explosionOrchestrator);
                        explosion.setLeft(Utilities.spriteCenter(
                                getWidth(),
                                explosion.getWidth(),
                                getLeft()
                        ));
                        explosion.setTop(Utilities.spriteTopAlign(getHeight(), explosion.getHeight(), getTop()));
                        _explosionOrchestrator.addExplosion(explosion);
                    }

                    setTop(0);
                    garbage();
                }
                break;
            case ALIEN:
                setTop(getTop() + GameSettings.ALIEN_MISSILE_PIXEL_SHIFT);
                if (getTop()>=(CanvasHelper.getHeight() - 48)) {
                    if (_explosionOrchestrator!=null){
                        explosion = new Explosion(GROUND, _explosionOrchestrator);
                        explosion.setLeft(Utilities.spriteCenter(
                                getWidth(),
                                explosion.getWidth(),
                                getLeft()
                        ));
                        explosion.setTop(Utilities.spriteBottomAlign(getHeight(), explosion.getHeight(), getTop()));
                        _explosionOrchestrator.addExplosion(explosion);
                    }

                    setTop(CanvasHelper.getHeight() - 48);
                    garbage();
                    _missileOrchestrator.registerHole(getLeft());
                }
                break;
        }
    }

    @Override
    public int getHeight() {
        return MISSILE_HEIGHT;
    }

    @Override
    public int getWidth() {
        return MISSILE_WIDTH;
    }

    public void setBrushColor(int brushColor) {
        _brushColor = brushColor;
    }

    public void setPenColor(int penColor) {
        _penColor = penColor;
    }

    public MissileType getMissileType() {
        return missileType;
    }

    public boolean isGarbage() {
        return _garbage;
    }

    public void setMissileType(MissileType type) {
        missileType = type;
    }

    private void setExplosionOrchestrator(ExplosionOrchestrator orchestrator) {
        _explosionOrchestrator = orchestrator;
    }

}
