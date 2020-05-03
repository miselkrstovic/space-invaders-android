package com.github.spaceinvaders.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.github.spaceinvaders.compatibility.Canvas32;
import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.engine.Engine;
import com.github.spaceinvaders.models.Model;

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

    public static void drawLine(int startX, int startY, int stopX, int stopY, Paint paint) {
        getCanvas().drawLine(
                Utilities.pxToDp(startX),
                Utilities.pxToDp(startY),
                Utilities.pxToDp(stopX),
                Utilities.pxToDp(stopY),
                paint
        );
    }

    public static void drawText(String str, int x, int y) {
        getCanvas().drawText(
                str,
                Utilities.pxToDp(x),
                Utilities.pxToDp(y),
                createPaint()
        );
    }

    private static Rect textBounds = new Rect();
    private static Paint textPaint = new Paint();

    public static Rect getTextBounds(String text) {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds;
    }

    public static Canvas32 createCanvas(Bitmap bitmap) {
        return new Canvas32(bitmap);
    }

    public static Paint createPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Model.PIXEL_COLOR_ON);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

}
