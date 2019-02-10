package com.github.spaceinvaders.compatibility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.spaceinvaders.MainApplication;
import com.github.spaceinvaders.engine.Freeable;

public class Bitmap32 extends Rect32 implements Freeable {

    public enum DrawMode {
        BLEND
    }

    private int defaultColor;
    private int defaultAlpha;

    private Paint _paint;
    private Bitmap _bitmap;
    private DrawMode _mode;
    private Canvas _canvas;

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
        _canvas = new Canvas(_bitmap);

        _paint = new Paint();
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
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(defaultColor);
        p.setAlpha(defaultAlpha);
        return p;
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
        Canvas canvas = new Canvas(_bitmap);
        canvas.drawBitmap(picture.getBitmap(), left, top, _paint);
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
        _paint.setAlpha(alpha);
    }

    public void setPenColor(int penColor) {
        _paint.setColor(penColor);
    }

    public void setFillRect(int top, int left, int right, int bottom, int col) {
        Canvas canvas = new Canvas(_bitmap);
        Paint paint1 = createPaint();
        paint1.setColor(col);
        paint1.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, paint1);
    }

    public void setFrameRectS(Rect32 boundsRect, int col) {
        Canvas canvas = new Canvas(_bitmap);
        Paint paint1 = createPaint();
        paint1.setColor(col);
        canvas.drawRect(boundsRect.getLeft(), boundsRect.getTop(), boundsRect.getRight(), boundsRect.getBottom(), paint1);
    }

    public Bitmap getBitmap() {
        return _bitmap;
    }

}
