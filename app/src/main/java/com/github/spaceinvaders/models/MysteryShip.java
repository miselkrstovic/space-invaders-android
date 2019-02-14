package com.github.spaceinvaders.models;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.R;
import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.enums.MysteryShipDirection;
import com.github.spaceinvaders.enums.SoundFile;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

import static com.github.spaceinvaders.enums.MysteryShipDirection.LEFT;

public class MysteryShip extends Model implements Freeable {

    private MysteryShipDirection _direction = LEFT;
    private int _mysteryShipCount;
    private boolean _mysteryShipEnabled;
    private int _mysteryShipThreshold;

    private Bitmap32 _picture;

    public MysteryShip() {
        _picture = new Bitmap32();
        _picture.loadFromFile(R.mipmap.mystery_ship);
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);
        setTop(8);

        _mysteryShipThreshold = 1000 / GameSettings.FPS * GameSettings.MYSTERY_SHIP_FREQUENCY;
    }

    public void free() {
        _picture = null;
    }

    public void die() {
        Utilities.playWave(SoundFile.MYSTERY_SHIP_EXPLOSION);
        init();
    }

    @Override
    public int getHeight() {
        return _picture.getHeight();
    }

    @Override
    public int getWidth() {
        return _picture.getWidth();
    }

    public void init() {
        _direction = MysteryShipDirection.values()[Utilities.random(2)];
        switch (_direction) {
            case LEFT:
                setLeft(CanvasHelper.getWidth());
                break;
            case RIGHT:
                setLeft(-1 * getWidth());
                break;
        }
        _mysteryShipCount = 0;
        _mysteryShipEnabled = false;
    }

    public void doPaint() {
        CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
    }

    public void update() {
        if (!_mysteryShipEnabled) {
            _mysteryShipCount = _mysteryShipCount + 1;
            if (_mysteryShipCount >= _mysteryShipThreshold) {
                _direction = MysteryShipDirection.values()[Utilities.random(2)];
                switch (_direction) {
                    case LEFT:
                        setLeft(CanvasHelper.getWidth());
                        break;
                    case RIGHT:
                        setLeft(-1 * getWidth());
                        break;
                }
            }
// TODO:            Utilities.playWave(SoundFile.MYSTERY_SHIP_CRUISE, true);
            _mysteryShipEnabled = true;
        } else {
            switch (_direction) {
                case LEFT:
                    setLeft(getLeft() - GameSettings.HORIZONTAL_PIXEL_SHIFT);
                    if (getLeft() <= -1 * getWidth()) {
                        _mysteryShipEnabled = false;
                        _mysteryShipCount = 0;
                    }
                    break;
                case RIGHT:
                    setLeft(getLeft() + GameSettings.HORIZONTAL_PIXEL_SHIFT);
                    if (getLeft() >= CanvasHelper.getWidth()) {
                        _mysteryShipEnabled = false;
                        _mysteryShipCount = 0;
                    }
                    break;
            }
        }
    }

    public boolean isShowing() {
        return _mysteryShipEnabled;
    }

}
