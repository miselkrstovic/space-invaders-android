package com.github.spaceinvaders.models;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.R;
import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.enums.AlienDirection;
import com.github.spaceinvaders.enums.AlienSpecie;
import com.github.spaceinvaders.enums.SoundFile;
import com.github.spaceinvaders.orchestrators.AlienOrchestrator;
import com.github.spaceinvaders.orchestrators.ExplosionOrchestrator;
import com.github.spaceinvaders.orchestrators.MissileOrchestrator;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

import static com.github.spaceinvaders.enums.AlienDirection.LEFT;
import static com.github.spaceinvaders.enums.AlienDirection.RIGHT;
import static com.github.spaceinvaders.enums.AlienSpecie.ALPHA;
import static com.github.spaceinvaders.enums.MissileType.ALIEN;

public class Alien extends Model {

    private int _frame;
    private MissileOrchestrator _missileOrchestrator;
    private AlienOrchestrator _alienOrchestrator;
    private ExplosionOrchestrator _explosionOrchestrator;
    private AlienSpecie _specie = ALPHA;

    private boolean _visible;
    private Bitmap32 _picture;
    private AlienDirection _direction = LEFT;

    public Alien() {
        _picture = new Bitmap32();
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);

        _specie = ALPHA;
    }

    public void descend() {
        setTop(getTop() + GameSettings.VERTICAL_PIXEL_SHIFT);
    }

    @Override
    protected void finalize() throws Throwable {
        _picture = null;
        super.finalize();
    }

    public void die() {
        Utilities.playWave(SoundFile.ALIEN_EXPLOSION);
        _visible = false;
    }

    private void flipDirection() {
        if (_direction == LEFT) {
            _direction = RIGHT;
        } else {
            _direction = LEFT;
        }
    }

    public void setDirection(AlienDirection direction) {
        _direction = direction;
    }

    public AlienDirection getDirection() {
        return _direction;
    }

    @Override
    public int getHeight() {
        return _picture.getHeight();
    }

    @Override
    public int getWidth() {
        return _picture.getWidth();
    }

    public void doPaint() {
        CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
    }

    public void shootMissile() {
        if (_visible) {
            Missile missile = new Missile(_missileOrchestrator, _explosionOrchestrator);
            missile.setLeft(getLeft() + (int) (getWidth() / 2));
            missile.setTop(getTop() + getHeight());
            missile.setMissileType(ALIEN);

            _missileOrchestrator.addMissile(missile);
        }
    }

    public void updateMotion() {
        switch (_direction) {
            case LEFT:
                setLeft((getLeft() - GameSettings.HORIZONTAL_PIXEL_SHIFT));
                break;
            case RIGHT:
                setLeft(getLeft() + GameSettings.HORIZONTAL_PIXEL_SHIFT);
                break;
        }

        switch (_frame) {
            case 0:
                switch (_specie) {
                    case ALPHA:
                        _picture.loadFromFile(R.mipmap.alien_alpha_1);
                        break;
                    case BETA:
                        _picture.loadFromFile(R.mipmap.alien_beta_1);
                        break;
                    case GAMMA:
                        _picture.loadFromFile(R.mipmap.alien_gamma_1);
                        break;
                }
                break;
            case 4:
                switch (_specie) {
                    case ALPHA:
                        _picture.loadFromFile(R.mipmap.alien_alpha_2);
                        break;
                    case BETA:
                        _picture.loadFromFile(R.mipmap.alien_beta_2);
                        break;
                    case GAMMA:
                        _picture.loadFromFile(R.mipmap.alien_gamma_2);
                        break;
                }
                break;
        }
        _frame++;
        if (_frame > 8) _frame = 0;

        if (getLeft() <= 8) {
            flipDirection();
        } else if (getLeft() >= (CanvasHelper.getWidth() - 36)) {
            flipDirection();
        }
    }

    public AlienSpecie getSpecie() {
        return _specie;
    }

    public void setSpecie(AlienSpecie value) {
        _specie = value;
        switch (_specie) {
            case ALPHA:
                _picture.loadFromFile(R.mipmap.alien_alpha_1);
                break;
            case BETA:
                _picture.loadFromFile(R.mipmap.alien_beta_1);
                break;
            case GAMMA:
                _picture.loadFromFile(R.mipmap.alien_gamma_1);
                break;
        }
    }

    public boolean isVisible() {
        return _visible;
    }

    public void setMissileOrchestrator(MissileOrchestrator orchestrator) {
        _missileOrchestrator = orchestrator;
    }

    public void setAlienOrchestrator(AlienOrchestrator orchestrator) {
        _alienOrchestrator = orchestrator;
    }

    public void setExplosionOrchestrator(ExplosionOrchestrator orchestrator) {
        _explosionOrchestrator = orchestrator;
    }

    public void setVisibility(boolean visibility) {
        _visible = visibility;
    }

}
