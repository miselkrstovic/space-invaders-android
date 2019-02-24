package com.github.spaceinvaders.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseBooleanArray;

import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.utils.CanvasHelper;

public class Ground extends Model implements Freeable {

    private Bitmap32 _picture;
    public SparseBooleanArray _bitmap;

    public void init() {
        _bitmap = new SparseBooleanArray(CanvasHelper.getWidth()); // TODO: Must be refreshed on screen resize
    }

    public void doPaint() {
        try {
            Paint paint = CanvasHelper.createPaint();
            paint.setColor(PIXIL_COLOR_ON);
            CanvasHelper.drawLine(0, 1, _bitmap.size(), 1, paint);
            CanvasHelper.drawLine(0, 2, _bitmap.size(), 2, paint);

            paint.setColor(PIXIL_COLOR_OFF);
            for (int i = 0; i < _bitmap.size(); i++) {
                if (_bitmap.get(i)) {
                    CanvasHelper.drawRect(
                            new Model(i, 1, i + 3, 3),
                            paint);
                }
            }
        } finally {
//            bvlGround.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void free() {
        _picture = null;
        _bitmap.clear();
    }

    public void reset() {
        _zero(_bitmap);
    }

    public void _zero(SparseBooleanArray array) {
        array.clear();
        for (int i = 0; i < array.size(); i++) {
            array.append(i, false);
        }
    }

    public void append(int key, boolean b) {
        _bitmap.append(key, true);
    }
}
