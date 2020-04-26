package com.github.spaceinvaders.compatibility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.spaceinvaders.engine.Engine;
import com.github.spaceinvaders.utils.Utilities;

public class Canvas32 extends Canvas {

    private Bitmap _bitmap;

    public Canvas32() {
    }

    public Canvas32(@NonNull Bitmap bitmap) {
        super(bitmap);
    }

    private Canvas getCanvas() {
        if (_bitmap != null) {
            return new Canvas32(_bitmap);
        } else {
            return Engine.getCanvas();
        }
    }

    @Override
    public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable Paint paint) {
        super.drawBitmap(bitmap, left, top, paint);
    }

    @Override
    public void drawRect(float left, float top, float right, float bottom, @NonNull Paint paint) {
        getCanvas().drawRect(
                Utilities.pxToDp(left),
                Utilities.pxToDp(top),
                Utilities.pxToDp(right),
                Utilities.pxToDp(bottom),
                paint
        );
    }

}
