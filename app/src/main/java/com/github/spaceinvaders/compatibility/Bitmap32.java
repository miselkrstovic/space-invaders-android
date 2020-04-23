package com.github.spaceinvaders.compatibility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import androidx.annotation.ColorInt;

import com.github.spaceinvaders.MainApplication;
import com.github.spaceinvaders.engine.Freeable;
import com.github.spaceinvaders.utils.CanvasHelper;

public class Bitmap32 extends Rect32 implements Freeable {

    public enum DrawMode {
        BLEND
    }

    private int defaultColor;
    private int defaultAlpha;

    private Bitmap _bitmap;
    private DrawMode _mode;

    public Bitmap32() {
        setup();
    }

    public Bitmap32(int left, int top, int right, int bottom) {
        setLeft(left);
        setTop(top);
        setRight(right);
        setBottom(bottom);

        setup();
    }

    private void setup() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        _bitmap = Bitmap.createBitmap(256, 256, conf); // this creates a MUTABLE bitmap
    }

    @Override
    public Rect32 getBoundsRect() {
        return new Bitmap32(
                getLeft(),
                getTop(),
                getWidth() + getLeft(),
                getTop() + getHeight()
        );
    }
    private Paint createPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(defaultColor);
        paint.setAlpha(defaultAlpha);
        return paint;
    }

    public void free() {
        _bitmap.recycle();
        _bitmap = null;
    }

    public void loadFromFile(int id) {
        Context context = MainApplication.getContext();
        _bitmap = BitmapFactory.decodeResource(context.getResources(), id);
    }

    public int getHeight() {
        return _bitmap.getHeight();
    }

    public int getWidth() {
        return _bitmap.getWidth();
    }

    public void draw(int left, int top, Bitmap32 picture) {
        Canvas32 canvas = CanvasHelper.createCanvas(_bitmap);
        canvas.drawBitmap(picture.getBitmap(), left, top, createPaint());
    }

    public void setDrawMode(DrawMode mode) {
        _mode = mode;
    }

    public void setWidth(int width) {
        _bitmap.setWidth(width);
    }

    public void setHeight(int height) {
        _bitmap.setHeight(height);
    }

    public void setMasterAlpha(int alpha) {
        defaultAlpha = alpha;
    }

    public void setPenColor(@ColorInt int penColor) {
        defaultColor = penColor;
    }

    public void setFillRect(int top, int left, int right, int bottom, @ColorInt int col) {
        Canvas32 canvas = CanvasHelper.createCanvas(_bitmap);
        Paint paint = createPaint();
        paint.setColor(col);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void setFrameRectS(Rect32 boundsRect, @ColorInt int col) {
        Canvas32 canvas = CanvasHelper.createCanvas(_bitmap);
        Paint paint = createPaint();
        paint.setColor(col);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(boundsRect.getLeft(), boundsRect.getTop(), boundsRect.getRight(), boundsRect.getBottom(), paint);
    }

    public Bitmap getBitmap() {
        return _bitmap;
    }

}
