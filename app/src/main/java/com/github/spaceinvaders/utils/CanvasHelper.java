package com.github.spaceinvaders.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.engine.Engine;

public class CanvasHelper {

    private static Paint stockPaint = new Paint();

    private static Canvas getCanvas() {
        return Engine.getCanvas();
    }

    public static int getHeight() {
        int heightInPixels = Utilities.dpToPx(getCanvas().getHeight());
        return heightInPixels;
    }

    public static int getWidth() {
        int widthInPixels = Utilities.dpToPx(getCanvas().getWidth());
        return widthInPixels;
    }

    public static void drawRect(Rect32 compatRect, Paint paint) {
        getCanvas().drawRect(
                new android.graphics.Rect(
                        Utilities.pxToDp(compatRect.getLeft()),
                        Utilities.pxToDp(compatRect.getTop()),
                        Utilities.pxToDp(compatRect.getRight()),
                        Utilities.pxToDp(compatRect.getBottom())
                ),
                paint
        );
    }

    public static void drawRect(int left, int top, int right, int bottom, Paint paint) {
        getCanvas().drawRect(
                Utilities.pxToDp(left),
                Utilities.pxToDp(top),
                Utilities.pxToDp(right),
                Utilities.pxToDp(bottom),
                paint
        );
    }

    public static void drawBitmap(Bitmap bitmap, int left, int top) {
        drawBitmap(bitmap, left, top, stockPaint);
    }

    public static void drawBitmap(Bitmap bitmap, int left, int top, Paint paint) {
        getCanvas().drawBitmap(
                bitmap,
                Utilities.pxToDp(left),
                Utilities.pxToDp(top),
                paint
        );
    }

    public static void drawText(String str, int x, int y) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        getCanvas().drawText(str, x, y, paint);
    }

    private static Rect textBounds = new Rect();
    private static Paint textPaint = new Paint();

    public static Rect getTextBounds(String text) {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds;
    }
}
