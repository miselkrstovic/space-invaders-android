package com.github.spaceinvaders.models;

import com.github.spaceinvaders.R;
import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.utils.CanvasHelper;

public class LaserCannon extends Model implements Paintable, Freeable {

    private int _laserCannonCount;
    private boolean _laserCannonEnabled;

    private Bitmap32 _picture;

    private static final LaserCannon INSTANCE = new LaserCannon();

    private LaserCannon() {
        _picture = new Bitmap32();
        _picture.loadFromFile(R.mipmap.laser_cannon);
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);
    }

    public static LaserCannon getInstance() {
        return INSTANCE;
    }

    public void free() {
        _picture = null;
    }

    public void die() {
        _laserCannonEnabled = false;
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
        _laserCannonCount = 0;
        _laserCannonEnabled = true;
    }

    public void paint() {
        if (_laserCannonEnabled) {
            setTop(CanvasHelper.getHeight() - (int) (48 * 1.5));
            CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
        }
    }

    public void update() {
        if (!_laserCannonEnabled) {
            _laserCannonCount = _laserCannonCount + 1;

            if (_laserCannonCount >= 100) _laserCannonEnabled = true;
        }
    }

    public boolean isShowing() {
        return _laserCannonEnabled;
    }

}
