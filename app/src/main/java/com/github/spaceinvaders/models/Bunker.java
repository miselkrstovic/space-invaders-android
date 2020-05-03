package com.github.spaceinvaders.models;

import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.enums.MissileDirection;
import com.github.spaceinvaders.orchestrators.BunkerOrchestrator;
import com.github.spaceinvaders.orchestrators.ExplosionOrchestrator;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

public class Bunker extends Model implements Paintable {

    private static final int BUNKER_WIDTH = 42;
    private static final int BUNKER_HEIGHT = 32;

    private Bitmap32 _picture;
    private boolean[][] _bunkerBitmap;

    private BunkerOrchestrator _bunkerOrchestrator;
    private ExplosionOrchestrator _explosionOrchestrator;

    public Bunker(BunkerOrchestrator bunkerOrchestrator, ExplosionOrchestrator explosionOrchestrator) {
        setWidth(BUNKER_WIDTH);
        setHeight(BUNKER_HEIGHT);

        _picture = new Bitmap32();
        _picture.setWidth(BUNKER_WIDTH);
        _picture.setHeight(BUNKER_HEIGHT);
        _picture.setMasterAlpha(0xFF);
        _picture.setPenColor(PIXEL_COLOR_ON);
        _picture.setFillRect(0, 0, _picture.getWidth(), _picture.getHeight(), PIXEL_COLOR_ON);
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);

        setBunkerOrchestrator(bunkerOrchestrator);
        setExplosionOrchestrator(explosionOrchestrator);

        reset();
    }

    @Override
    protected void finalize() throws Throwable {
        _picture = null;
        super.finalize();
    }

    public void paint() {
        CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
    }

    public boolean isPassable(Rect32 missileRect, MissileDirection missileDirection) {
        int i, j;
        int missileHeight;

        int slotIndex = Math.abs(getLeft() - missileRect.getLeft());

        boolean result = true;
        missileHeight = missileRect.getBottom() - missileRect.getTop();

        if (missileDirection == MissileDirection.MOVING_DOWN) {
            for (i = 0; i < getHeight(); i++) {
                if (!isBitmapFlagSet(slotIndex, i)) {
                    result = false;
                    for (j = i; j < Math.min(i + missileHeight, getHeight()); j++) {
                        // Alien missile explosion is MISSILE_WIDTH x2
                        setBitmapFlag(slotIndex - 1, j);
                        setBitmapFlag(slotIndex, j);
                        setBitmapFlag(slotIndex + 1, j);
                        setBitmapFlag(slotIndex + 2, j);
                    }
                    break;
                }
            }

            if (!result) {
                _picture.setPenColor(Utilities.setAlpha(PIXEL_COLOR_OFF, 0x00));
                _picture.setFillRect(
                        Math.max(Math.abs(getLeft() - missileRect.getLeft()) - 1, 0),
                        i,
                        Math.min(Math.abs(getLeft() - missileRect.getLeft()) + 2 + 1, _picture.getWidth()),
                        Math.min(i + missileHeight + 1, _picture.getHeight()),
                        Utilities.setAlpha(PIXEL_COLOR_OFF, 0x00)
                );
                result = false;
                setBitmapFlag(slotIndex, i);
            }
        } else {
            for (i = getHeight() - 1; i >= 0; i--) {
                if (!isBitmapFlagSet(slotIndex, i)) {
                    result = false;
                    for (j = Math.max(i, getHeight() - 1); j >= i - missileHeight; j--) {
                        // LaserCannon missile explosion is MISSILE_WIDTH x4
                        setBitmapFlag(slotIndex - 2, j);
                        setBitmapFlag(slotIndex - 1, j);
                        setBitmapFlag(slotIndex, j);
                        setBitmapFlag(slotIndex + 1, j);
                        setBitmapFlag(slotIndex + 2, j);
                        setBitmapFlag(slotIndex + 3, j);
                    }
                    break;
                }
            }

            if (!result) {
                _picture.setPenColor(Utilities.setAlpha(PIXEL_COLOR_OFF, 0x00));
                _picture.setFillRect(
                        Math.max(Math.abs(getLeft() - missileRect.getLeft()) - 2, 0),
                        Math.max(i - missileHeight, 0),
                        Math.min(Math.abs(getLeft() - missileRect.getLeft()) + 2 + 2, _picture.getWidth()),
                        Math.min(i + 1, _picture.getHeight()),
                        Utilities.setAlpha(PIXEL_COLOR_OFF, 0x00)
                );
                result = false;
                setBitmapFlag(slotIndex, i);
            }
        }
        return result;
    }

    private boolean isBitmapFlagSet(int x, int y) {
        try {
            return _bunkerBitmap[x][y];
        } catch (Exception ex) {
            return false;
        }
    }

    private void setBitmapFlag(int x, int y) {
        try {
            _bunkerBitmap[x][y] = true;
        } catch (Exception ex) {
            // Do nothing
        }
    }

    public void reset() {
        setLength(_bunkerBitmap, getWidth(), getHeight());
    }

    private void setLength(boolean[][] array, int width, int height) {
        _bunkerBitmap = new boolean[height][width];
    }

    public void update() {
        // Nothing
    }

    public Bitmap32 getPicture() {
        return _picture;
    }

    private void setBunkerOrchestrator(BunkerOrchestrator orchestrator) {
        _bunkerOrchestrator = orchestrator;
    }

    private void setExplosionOrchestrator(ExplosionOrchestrator orchestrator) {
        _explosionOrchestrator = orchestrator;
    }

}
