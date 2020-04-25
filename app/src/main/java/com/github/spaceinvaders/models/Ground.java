package com.github.spaceinvaders.models;

import android.graphics.Paint;
import android.util.SparseBooleanArray;

import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.utils.CanvasHelper;

public class Ground extends Model implements Paintable, Freeable {

    private Bitmap32 _picture;
    public SparseBooleanArray _bitmap;
    private boolean _initialized;

    private static final Ground INSTANCE = new Ground();

    private Ground() {}

    public static Ground getInstance() {
        return INSTANCE;
    }

    public void init() {
        // Do nothing
    }

    private void lazyInit() {
        _bitmap = new SparseBooleanArray();

        setLeft(0);
        setRight(CanvasHelper.getWidth());
        setTop(CanvasHelper.getHeight() - 5);
        setHeight(5);

        _initialized = true;
    }

    public void paint() {
        if (!_initialized) lazyInit();

        Paint paint = CanvasHelper.createPaint();
        paint.setColor(PIXIL_COLOR_ON);
        CanvasHelper.drawLine(getLeft(), getTop() + 1, _bitmap.size(), getTop() + 1, paint);
        CanvasHelper.drawLine(getLeft(), getTop() + 2, _bitmap.size(), getTop() + 2, paint);

        paint.setColor(PIXIL_COLOR_OFF);
        for (int i = 0; i < _bitmap.size(); i++) {
            if (_bitmap.get(i)) {
                CanvasHelper.drawRect(
                        new Model(i, 1, i + 3, 3),
                        paint);
            }
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
        if (array == null) return;

        array.clear();
        for (int i = 0; i < array.size(); i++) {
            array.append(i, false);
        }
    }

    public void append(int key, boolean b) {
        _bitmap.append(key, true);
    }

}
