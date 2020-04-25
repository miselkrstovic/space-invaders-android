package com.github.spaceinvaders.models;

import com.github.spaceinvaders.R;
import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.enums.ExplosionType;
import com.github.spaceinvaders.orchestrators.ExplosionOrchestrator;
import com.github.spaceinvaders.utils.CanvasHelper;

import static com.github.spaceinvaders.enums.ExplosionType.PLAYER;

public class Explosion extends Model implements Paintable {

    private boolean _garbage;
    private int _frames;

    private ExplosionType _explosionType = PLAYER;
    private ExplosionOrchestrator _explosionOrchestrator;

    private Bitmap32 _picture;

    public Explosion(ExplosionType explosionType, ExplosionOrchestrator orchestrator) {
        _picture = new Bitmap32();
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);

        setExplosionType(explosionType);
        setExplosionOrchestrator(orchestrator);
    }

    @Override
    protected void finalize() throws Throwable {
        _picture = null;
        super.finalize();
    }

    @Override
    public int getHeight() {
        return _picture.getHeight();
    }

    @Override
    public int getWidth() {
        return _picture.getWidth();
    }

    public void garbage() {
        _garbage = true;
    }

    public void paint() {
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);
        CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
    }

    public void updateAnimation() {
        _frames++;
        if (_frames > 10) garbage();
    }

    public boolean isGarbage() {
        return _garbage;
    }

    public ExplosionOrchestrator getExplosionOrchestrator() {
        return _explosionOrchestrator;
    }

    private void setExplosionOrchestrator(ExplosionOrchestrator orchestrator) {
        _explosionOrchestrator = orchestrator;
    }

    public ExplosionType getExplosionType() {
        return _explosionType;
    }

    private void setExplosionType(ExplosionType value) {
        _explosionType = value;
        switch (_explosionType) {
            case PLAYER:
                _picture.loadFromFile(R.mipmap.explosion);
                break;
            case ALIEN:
                _picture.loadFromFile(R.mipmap.explosion);
                break;
            case MISSILE:
                _picture.loadFromFile(R.mipmap.missile_explosion);
                break;
            case MYSTERY_SHIP:
                _picture.loadFromFile(R.mipmap.mystery_ship_explosion);
                break;
            case SKY:
                _picture.loadFromFile(R.mipmap.sky_explosion);
                break;
            case GROUND:
                _picture.loadFromFile(R.mipmap.ground_explosion);
                break;
            default:
                _picture.loadFromFile(R.mipmap.explosion);
        }
    }

}
