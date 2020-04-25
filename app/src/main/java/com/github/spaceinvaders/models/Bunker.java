package com.github.spaceinvaders.models;

import android.graphics.Color;

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
    private boolean _bitmap[][];

    private BunkerOrchestrator _bunkerOrchestrator;
    private ExplosionOrchestrator _explosionOrchestrator;

    public Bunker(BunkerOrchestrator bunkerOrchestrator, ExplosionOrchestrator explosionOrchestrator) {
        setWidth(BUNKER_WIDTH);
        setHeight(BUNKER_HEIGHT);

        _picture = new Bitmap32();
        _picture.setWidth(BUNKER_WIDTH);
        _picture.setHeight(BUNKER_HEIGHT);
        _picture.setMasterAlpha(0xFF);
        _picture.setPenColor(PIXIL_COLOR_ON);
        _picture.setDrawMode(Bitmap32.DrawMode.BLEND);
        _picture.setFillRect(0, 0, _picture.getWidth(), _picture.getHeight(), PIXIL_COLOR_ON);

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
        CanvasHelper.drawRect(getBoundsRect(), CanvasHelper.createPaint());

// TODO:        CanvasHelper.drawBitmap(_picture.getBitmap(), getLeft(), getTop());
    }

    private boolean isValidSlotIndex(int value) {
        return (value >= 0) && (value < getWidth());
    }

    public boolean isPassable(Rect32 missileRect, MissileDirection missileDirection) {
        int i, j;
        int missileHeight;
        boolean result = true;

        int slotIndex = Math.abs(getLeft() - missileRect.getLeft());
        if (!isValidSlotIndex(slotIndex)) {
            // TODO: Why is slotIndex yielding 42 (Width of the bunker)??
            return result;
        }

        missileHeight = missileRect.getBottom() - missileRect.getTop();

        if (missileDirection == MissileDirection.MOVING_DOWN) {
            for (i = 0; i < getHeight(); i++) {
                try {
                    if (!_bitmap[slotIndex][i]) {
                        result = false;
                        for (j = i; j < Math.min(i + missileHeight, getHeight()); j++) {
                            // Alien missile explosion is MISSILE_WIDTH x2
                            if (isValidSlotIndex(slotIndex - 1)) _bitmap[slotIndex - 1][j] = true;
                            if (isValidSlotIndex(slotIndex)) _bitmap[slotIndex][j] = true;
                            if (isValidSlotIndex(slotIndex + 1)) _bitmap[slotIndex + 1][j] = true;
                            if (isValidSlotIndex(slotIndex + 2)) _bitmap[slotIndex + 2][j] = true;
                        }

                        break;
                    }
                } catch (Exception e) {
                    // TODO: Remove this try/catch
                }
            }

            if (!result) {
                _picture.setPenColor(Utilities.setAlpha(PIXIL_COLOR_OFF, 0x00));
                _picture.setFillRect(
                        Math.max(Math.abs(getLeft() - missileRect.getLeft()) - 1, 0),
                        i,
                        Math.min(Math.abs(getLeft() - missileRect.getLeft()) + 2 + 1, _picture.getWidth()),
                        Math.min(i + missileHeight + 1, _picture.getHeight()),
                        Utilities.setAlpha(PIXIL_COLOR_OFF, 0x00)
                );
                result = false;
                _bitmap[slotIndex][i] = true;
            }
        } else {
            for (i = getHeight() - 1; i >= 0; i--) {
                if (!_bitmap[slotIndex][i]){
                    result = false;

                    for (j = Math.max(i, getHeight() - 1); j >= i - missileHeight; j--) {
                        // LaserCannon missile explosion is MISSILE_WIDTH x4
                        if (isValidSlotIndex(slotIndex - 2)) _bitmap[slotIndex - 2][j] = true;
                        if (isValidSlotIndex(slotIndex - 1)) _bitmap[slotIndex - 1][j] = true;
                        if (isValidSlotIndex(slotIndex)) _bitmap[slotIndex][j] = true;
                        if (isValidSlotIndex(slotIndex + 1)) _bitmap[slotIndex + 1][j] = true;
                        if (isValidSlotIndex(slotIndex + 2)) _bitmap[slotIndex + 2][j] = true;
                        if (isValidSlotIndex(slotIndex + 3)) _bitmap[slotIndex + 3][j] = true;
                    }
                    break;
                }
            }

            if (!result) {
                _picture.setPenColor(Utilities.setAlpha(PIXIL_COLOR_OFF, 0x00));
                _picture.setFillRect(
                        Math.max(Math.abs(getLeft() - missileRect.getLeft()) - 2, 0),
                        Math.max(i - missileHeight, 0),
                        Math.min(Math.abs(getLeft() - missileRect.getLeft()) + 2 + 2, _picture.getWidth()),
                        Math.min(i + 1, _picture.getHeight()),
                        Utilities.setAlpha(PIXIL_COLOR_OFF, 0x00)
                );
                result = false;
                _bitmap[slotIndex][i] = true;
            }
        }
        return result;
    }

    public void reset() {
        setLength(_bitmap, 0, 0); // Required to reset the bitmap
        setLength(_bitmap, getWidth(), getHeight());
    }

    private void setLength(boolean[][] array, int width, int height) {
        _bitmap = new boolean[height][width];
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
